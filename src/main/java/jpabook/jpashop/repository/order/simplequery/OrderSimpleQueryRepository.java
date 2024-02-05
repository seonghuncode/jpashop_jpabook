package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    //repository의 경우 엔티티를 조회하는 것이 주요 목적이기 때문에 api스펙에 의한 dto를 조회하고 반환하는 경우 별도의 하위 패키지에 관리하는 것이 좋다
    public List<OrderSimpleQueryDto> findOrderDtos(){
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        "from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class
        ).getResultList();
        //JPA에는 엔티티는 value Object만 반환이 가능
        // 하지만 DTO타입을 바로 반환하기 위해서는 OrderSimpleQueryDto타입으로 반환이 필요
        //DTO를 반환하기 위해서는 new DTO경로(직접 파라미터를 모두 넘겨주어야 한다)
    }

}
