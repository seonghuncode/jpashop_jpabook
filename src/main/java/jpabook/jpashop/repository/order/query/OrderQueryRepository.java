package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    //컬렉션
    public List<OrderQueryDto> findOrderQueryDtos(){
        //주문 DTO를 컬렉션 부분을 제외하고 가지고 온다 (OneToOne, ManyToOne관계는 조인을 해도 row수가 증가하지 않는다)
        List<OrderQueryDto> result = findOrders();

        //result를 반복문을 돌리면서 result에 있는 OrderQueryDto의 orderItems값이 없기 때문에 해당 데이터를 넣어주는 작업
        //(일대다관계의 경우 조인을 하면 row수가 증가하기 때문에 제외하고 result를 가지고온 후 해당 result에 필요한 값을 다시 넣어주는 방식)
        //order의 갯수만큼 추가 쿼리가 나가는 단점이 있다(최적화 필요)
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }


    private List<OrderItemQueryDto> findOrderItems(Long orderId){
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

   public List<OrderQueryDto> findOrders(){
        return em.createQuery(
                //생성자에는 컬렉션을 넣을 수 없다.
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class
        ).getResultList();
    }



    //일대다 관계에서 JPA에서 DTO를 직접 조회할 경우 order의 갯수만큼 추가 쿼리가 나가는 n+1문제를 최적화한 방법법
   public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();

        //주문에 대해서 orderId로 변경
       List<Long> orderIds = toOrderIds(result);

       //주문 데이터 만큼 데이터를 가지고 온다
       Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

       //반복문을 돌리면서 바로 위에서 메모리에 올려둔 데이터를 추가 해준다(반복문을 통해 부족했던 데이터를 채워준다)
       result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

       return result;
   }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        //주문과 관련된 orderItems를 한번에 가지고 오는 쿼리
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        //orderItems를 그냥 사용해도 되지만 최적화를 위해 map으로 변경해 준다.
        //장점 : 쿼리를 한번 날리고 메모리에서 매칭을 통해 값을 할당해준다.
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
        return orderItemMap;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
        return orderIds;
    }


}
