package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

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
}
