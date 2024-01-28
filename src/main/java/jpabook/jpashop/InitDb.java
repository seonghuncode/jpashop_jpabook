package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {
    //조회용 샘플 데이터를 입력하는 로직(예제 데이터 저장)
    /*
    총 주문 2개의 샘플 데이터
    1. userA
        JPA1 BOOK
        JPA2 BOOK
    2. userB
        SPRING1 BOOK
        SPRING2 BOOK
     */

    private final InitService initService;

    //애플리게이션 로딩시점에 doInit1을 호출 하여 데이터 등록
    @PostConstruct
    public void init(){
        initService.dbInit1();
        initService.dbInit2();
    }
    
    @Component //해당 어노테에션을 사용하면 spring에서 관리
    @Transactional
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;
        public void dbInit1(){
            Member member = createMember("userA", "서울", "1", "1111");
            em.persist(member); //영속상태가 된다.

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000,1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000,2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(name);
            book1.setPrice(price);
            book1.setStockQuantity(stockQuantity);
            return book1;
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }


        public void dbInit2(){
            Member member = createMember("userB", "대전", "2", "2222");
            em.persist(member); //영속상태가 된다.

            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000,3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000,4);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }





    }
    
    
}

