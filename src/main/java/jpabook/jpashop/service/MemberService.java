package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //데이터 변경은 트랜잭션 안에서 실행되어야 하기 때문에 어노테이션을 사용해준다.
// ((readOnly = true) : 조회와 같은 읽기 기능에서는 해당 옵션을 주면 JPA에서 성능을 최적화 해준다.)
@RequiredArgsConstructor
public class MemberService {

    /*
    // 일반 injection (단점 : 직접 값을 주입하는 것이 쉽지 않다.), 보통 많이 사용하는 방법
    @Autowired //spring이 spring bean에 들어있는 repository를 자동으로 인젝션 해준다.
    private MemberRepository memberRepository;
     */

    /*
    //setter injection,
    // 사용 이유 : 테스트 코드같은 코드를 작성할때 매개변수를 직접 작성이 가능하다.(가짜 repository를 주입 가능)
    //단점 : 실행중 누군가가 변경이 가능하다
    private MemberRepository memberRepository; // 바로 주입X
    @Autowired
    public  void setMemberRepository(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

     */

    /*
    //생성자 injection(최근 많이 사용하는 방법법)
    //spring에서 사용할때 생성자에서 주입을 해준다(생성시 만들어지기 때문에 중간에 변경될 일이 없다)
    private final MemberRepository memberRepository; // 바로 주입X(final은 권장 : 중간에 변경될 일이 없기 때문 예방 차원)
    @Autowired
    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

     */

    private final MemberRepository memberRepository; //@RequiredArgsConstructor해당 어노테이션을 작성하면 final에 대해 자동으로 어노테이션 생성 해준다.

    /*
    * 회원가입
    * */
    @Transactional //읽기가 아닌 경우 그냥 트랜잭션얼 어노테니션 사용
    public Long join(Member member){
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member){
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원 입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers(Long memberId){
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }



}
