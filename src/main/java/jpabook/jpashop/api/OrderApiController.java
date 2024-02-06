package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

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
}
