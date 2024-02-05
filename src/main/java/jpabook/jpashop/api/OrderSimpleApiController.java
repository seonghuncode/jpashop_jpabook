package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    /*
    - 핵심 기능 : 주문조회
    - 연관관계
        주문 -> 회원
        주문 -> 배송정보
    - 핵심 포인트 (ManyToOne, OneToOne에서의 성능최적화)
     */

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    //주문조회 (엔티티를 반환하는 방식 -> 이렇게 사용하는 것은 좋지X)
    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        /*
        문제1
        Order에서 -> Member -> Order 계속 무한로프로 도는 문제가 발생
        => 양방향연관관계의 경우 한쪽에 @JsonIgone를 사용해야 한다

        문제2
        지연로딩 사용할 경우
        해결방안 : 지연로딩일 경우 하이버네이트에게 json에 대해 신경 쓰지 않도록 한다
        - 하이버네이트 파이브 모듈 설치(Hibernate5Module의존성 추가 + 빈등록)
         */
        for (Order order : all){
            order.getMember().getName(); //Lazy강제초기화 -> 직접 DB로 데이터 가지고 오기
            order.getDelivery().getAddress();
        }

        return all;
    }

    //주문조회 (엔티티 반환이 아닌 DTO를 반환하는 방식)
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch()); //주문조회

        //주문조회한 데이터를 SimpleOrderDto타입으로 변환해서 반환
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }


    //위의 "api/v2/simple-orders"의 경우 엔티티를 DTO객체로 변환하는데 있어 쿼리가 많이 나가 성능 최적화가 필요하다다
    @GetMapping("/api/v3/simple-orders")
    public  List<SimpleOrderDto> iordersV3(){
        List<Order> orders = orderRepository.finalAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }


    //엔티티를 먼저 조회하고 DTO로 변환하는 방식이 아닌 바로 DTO를 조회하는 방법
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4(){
        return orderSimpleQueryRepository.findOrderDtos();
    }


    //DTO객체(클라이언트에서 사용할 객체 -> 해당 객체 데이터는 서버인 orderV2()메서드에서 데이터를 API로 반환)
    //단점 : 레이지 로딩으로 인한 데이터베이스 쿼리 호출이 너무 많다..
    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; //배송 정보

        public SimpleOrderDto(Order order){ //엔티티를 받아 DTO를 채운다
            orderId = order.getId();
            name = order.getMember().getName(); //Lazy초기화 : 영속성 컨텍스트에서 orderid를 가지고 데이터를 찾는다 -> 없으면 DB에서 데이터를 가지고 온다.
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //Lazy초기화
        }
    }


}
