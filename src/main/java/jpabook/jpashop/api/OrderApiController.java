package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    //엔티티를 직접 노출하는 방법
    @GetMapping("/api/v1/orders")
    public List<Order> orderV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        //바로 all을 반환할 경우 지연로딩에 대해 처리해주지 않으면 오류가 발생하므로 지연로딩 객체 데이터를 가지고 온다다
       for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            //Order엔티티에서 orderItems는 1대다 관계이며 해당 orderItems에 있는 지연로딩 객체에 대해 강제 초기화 해주는 작업
           //강제 초기화 이유 : Hibernate5Module을 사용하는데 해당 기본 설정은 지연설정에 대해 데이터를 뿌리지 않기 때문에
           // 초기화를 통해 데이터가 있다는 것을 알려주어 뿌릴 수 있게 해준다
           List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }




    //엔티티를 DTO로 변환해서 반환하는 방법
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        //엔티티 조회
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        //orders -> DTO로 변환
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @Getter
    static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate  = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            //orderItems는 엔티티이기 때문에 DTO로 변환 시켜서 반환해주어야 한다.
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }

    }


    //DTO안에 있는 엔티티에 대해 DTO로 변환할 DTO클래스
    @Getter
    static class OrderItemDto{

        private String itemName; //상품명
        private int orderPrice; //주문가격
        private int count; //주문수량

        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }



}
