package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //생성자들을 같은 패키지 내에서만 접근 가능 하도록 설정
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // 지연로딩 : DB에서 member를 가지고 오는것이 아니라  Order이 데이터만 가지고 온다
    // member에 null을 넣을 수 없기 때문에 하이버네이트에서 가짜 proxyMember를 사용해서 상속받아 넣어놓는다.
    // 실제 member를 사용하려고 할때 그때 DB로 부터 member를 가지고 온다 -> json에서 요청할 경우 member에 처리할 수 없는 값이 들어있기때문에 오류 발생
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") //매핑할 이름
    private Member member;

    //@BatchSize(size = 1000) //일대다 관계에서 특정 객체에 대해서만 배치 사이즈를 적용하고 싶을 경우 (일대다 관계에서!)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //order을 저장하면 -> orderItem도 강제고 persist를 한다.
    //cascade : orderItem에 값을 넣고 저장하기 위해서는 먼저 하고 order를 persist로 저장해주어야 하는데 -> order만 persist해도 자동으로 orderItem을 저장
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id") //연관관계의 주인(FK있는 테이블)
    private Delivery delivery;

    private LocalDateTime orderDate;

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


    //생성 메서드(주문의 경우 복잡하게 연관관계가 있기 때문에 별도로 생성 메서드를 만들어 사용하는 것이 편리하다.)
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //비즈니스 로직
    //- 주문취소
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능 합니다.");
        }

        this.setStatus(OrderStatus.CANCEL); //위의 벨리데이션 통과시 상태를 cancel로 변경해주면 된다.

        //재고를 원상복구 해야한다.(주문에 여러개가 담겨 있을 수 있기 때문에 담겨있는 갯수만큼 취소를 해주어야 한다.)
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }


    //죄회 로직
    //- 전체 주문 가격 조회
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }



}
