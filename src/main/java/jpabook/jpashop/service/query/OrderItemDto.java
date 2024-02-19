package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.OrderItem;
import lombok.Getter;

//DTO안에 있는 엔티티에 대해 DTO로 변환할 DTO클래스
@Getter
public class OrderItemDto {

    private String itemName; //상품명
    private int orderPrice; //주문가격
    private int count; //주문수량

    public OrderItemDto(OrderItem orderItem) {
        itemName = orderItem.getItem().getName();
        orderPrice = orderItem.getOrderPrice();
        count = orderItem.getCount();
    }
}
