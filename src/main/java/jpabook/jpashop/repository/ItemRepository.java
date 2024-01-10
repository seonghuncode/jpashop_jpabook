package jpabook.jpashop.repository;


import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        /*
        JPA은 DB에 저장하기 전까지 id값이 존재하지 않는다.
        id값이X = 새로 생성한 객체를 의미
         */
        if(item.getId() == null){
            em.persist(item); //처음 생성된 객체라면 DB에 저장
        }else{
            em.merge(item); //DB에 있는 데이터를 갱신
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i ", Item.class)
                .getResultList();
    }





}
