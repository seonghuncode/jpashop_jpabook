package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController // ==> @Controller +  @ResponseBody(데이터를 json,xml로 바로 보내기 위한 용도)
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    //외부에서 해당 url로 member객체의 데이터가 넘어오면 회원가입을 하고 아래 응답값으로 id를 반환해 준다
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    //응답값
    @Data
    static class CreateMemberResponse{
        private Long id;
        public CreateMemberResponse(Long id){
            this.id = id;
        }
    }
}


