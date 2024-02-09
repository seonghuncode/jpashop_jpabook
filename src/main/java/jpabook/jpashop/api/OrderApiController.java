package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    //엔티티를 직접 노출하는 방법
    @GetMapping("/api/v1/orders")
    public List<Order> orderV1() {
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
    public List<OrderDto> ordersV2() {
        //엔티티 조회
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        //orders -> DTO로 변환
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }


    //엔티티를 DTO로 반환하는 APT를 fetch join으로 성능 최적화
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        //orders -> DTO로 변환
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }


    //엔티티를 DTO로 변환할 경우 일대다관계에서 fetch join을 사용하면 페이징이 불가능 하기 때문에 페이징이 가능하도록 하는 로직
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                       @RequestParam(value = "limit", defaultValue = "100") int limit) {

        //1. OneToOne, ManyToOne의 경우 fetch join에 영향을 주지 않기 때문에 바로 fetch join해서 가지고 온다.
        List<Order> orders = orderRepository.finalAllWithMemberDelivery(offset, limit);

        //2.
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }


    //JPA에서 DTO를 직접 조회(일대다관계가 있는 경우?)
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }


    //JPA에서 DTO를 직잡 조회(n+1에 대한 쿼리 문제를 해결하기 위한 최적화 방법 적용)
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    //JPA에서 DTO를 직접 조회(조인한 쿼리 한번으로 필요한 모든 데이터를 조회할수 있도록 최적화)
    //주의점 : 1:N 조인을 하면 다쪽에 맞추어 테이블이 생성되기 때문에 중복데이터가 발생
    //장점 : 쿼리가 1번 실행
    //단점 : 데이터가 많을 경우 v5보다 늘릴수 있다, 개발자가 직접 중복을 제거하기 때문에 추가 작업이 많다, 페이징 불가능
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        //orderFlatDto를 OrderQueryDto로 변환(이유 필요한 API스펙이 OrderQueryDto이기 때문)
        //개발자가 중복되는 데이터를 직접 걸러주는 로직
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }


    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
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
    static class OrderItemDto {

        private String itemName; //상품명
        private int orderPrice; //주문가격
        private int count; //주문수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }


}
