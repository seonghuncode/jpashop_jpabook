package jpabook.jpashop;


import jpabook.jpashop.domain.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import javax.transaction.Transactional;
import static org.assertj.core.api.Assertions.assertThat;





@ExtendWith(SpringExtension.class)
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    //테스트 케이스 작성
    @Test
    @Transactional //어노테이션 사용 이유 : 엔티티의 변경은 모두 트랜잭션 안에서 이루어 져야 한다.
    // (해당 어노테이션이 Test에 있으면 실행하고 바로 Rollback을 하기 때문에 DB에 반영되지 않는다. / @Rollvack(false)를 통해 무력화 가능)
    @Rollback(false)
    public void testMember() throws Exception{

        //given
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);

        //then (검증)
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); //True : 같은 트핸잭션 안에서 저장, 조회시 사용하는 영속성 컨텍스트가 동일하다
        // -> 동일한 영속성 컨텍스트에서 같은 키값으로 조회하기 때문에 일치치
        }

}