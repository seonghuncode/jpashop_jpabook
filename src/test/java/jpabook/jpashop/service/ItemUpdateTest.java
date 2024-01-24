package jpabook.jpashop.service;


import jpabook.jpashop.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@ExtendWith(SpringExtension.class) //Junit4사용시 :  @RunWith(SpringRunner.class)
@SpringBootTest //실제 Spring 올려서 테스트
//@Transactional //데이터를 연동해서 테스트 (해당 어노테이션이 있어야 롤백이 가능) / 테스트 케이스에서 롤백을 시키기 때문에 inset쿼리가 나오지 않는다.
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception{
        Book book = em.find(Book.class, 1L);

        //트랜잭션 내에서
        book.setName("이름 변경");

        //트랜잭션 커밋 -> 이 시점에서 JPA가 변경된 것을 찾는다 -> update쿼리를 자동으로 만들어서 DB에 저장한다. (=변경감지 or dirty checking)


    }

}
