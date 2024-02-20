package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> { //Long : PK타입

    //Spring Data JPA에서 제공하지 않는 이름을 찾는 메서드만 직접 만들어 주면된다.
    //하지만 이때도 메서드 이름을 규칙에 맞게 작성해서 메서드만 만들면 나머지를 직접  구현하지 않아도 자동으로 Spring Data JPA에서 구현해준다.
    // -> select m from Member m where m.name = ?;
    List<Member> findByName(String name);
}
