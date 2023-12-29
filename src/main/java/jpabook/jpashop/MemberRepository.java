package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {
    //Repository : Entity를 찾아주는 역할 (DAO랑 비슷)

    @PersistenceContext
    private EntityManager em;

    //저장
    public Long save(Member member){
        em.persist(member);
        return member.getId();
    }

    //조회
    public Member find(Long id){
        return em.find(Member.class, id);
    }

}
