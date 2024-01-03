package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") //매핑할 이름
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    //cascade : orderItem에 값을 넣고 저장하기 위해서는 먼저 하고 order를 persist로 저장해주어야 하는데 -> order만 persist해도 자동으로 orderItem을 저장
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id") //연관관계의 주인(FK있는 테이블)
    private Delivery delivery;

    private LocalDateTime localDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태

    //연관관계 메서드(양방향일 경우 사용하면 좋다)
    public void setMember(Member member){
        this.member= member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

}
