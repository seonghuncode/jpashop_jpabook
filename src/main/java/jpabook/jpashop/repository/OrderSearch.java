package jpabook.jpashop.repository;

import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearch {

    //파라미터 이름
    private String memberName; //회원 이름
    private OrderStatus orderStatus; //주문 상태(ORDER, CANCEL)
}
