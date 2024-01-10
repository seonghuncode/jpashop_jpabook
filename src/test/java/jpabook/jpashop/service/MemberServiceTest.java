package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class) //Junit4사용시 :  @RunWith(SpringRunner.class)
@SpringBootTest //실제 Spring 올려서 테스트
@Transactional //데이터를 연동해서 테스트 (해당 어노테이션이 있어야 롤백이 가능) / 테스트 케이스에서 롤백을 시키기 때문에 inset쿼리가 나오지 않는다.
class MemberServiceTest {


    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;


    @Test
    //@Rollback(false) // 롤백을 하지 않고 DB에 데이터가 들어가는 것을 확인하고 싶을 경우 사용
    public void 회원가입()throws Exception{
        /*
        * 테스트 given(어떠한 상황이 주어 줬을때) when(이렇게 하면) then(이렇게 된다) -> 기본적인 테스트 스타일
        * */

        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long saveId = memberService.join(member);

        //then
        assertEquals(member, memberRepository.findOne(saveId));

    }



    @Test
    public void 중복_회원_예외()throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        try{
            memberService.join(member2); //증복 발생
        }catch (IllegalStateException e){
            return; // 해당 예외가 발생하고 return하기 때문에 아래 then로직은 실행되지 않는다
        }

        //then
        fail("예외가 발생해야 한다.");
        /*
         * 위에 when부분에서 중복 검사 부분에서 예외사 발생해 해당 예외가 실행이 되어야 하기 때문에 여기까지 실행이 되면 안된다
         * 해당 코드는 실행이 되면 안되는 것으로 해당 코드가 실행되면 코드에 문제가 있는 것!
         * */
    }

}