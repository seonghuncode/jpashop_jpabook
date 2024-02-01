package jpabook.jpashop.api;


import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}
