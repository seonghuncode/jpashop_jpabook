package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

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

    //엔티티를 파라미터로 받지 않고 DTO를 만들어 사용하는 방법(api 스펙에 영향을 주지 않는다)
    //DTO로 받을 경우(1. api스펙에 영향X 2. 파라미터로 무엇을 받는지 명확하게 알 수 있다.)
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName()); //엔티티 이름이 변경되어도 파라미터로 받은 값을 해당 라인에서 매핑을 하기 때문에 api에는 영향을 주지 않는다

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }


    //수정 API
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id, //업데이트용 응답 DTO
            @RequestBody @Valid UpdateMemberRequest request){ //업데이트용 요청 DTO

        //수정시에는 변경 감지를 사용!
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());

    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }


    //api로 받는 값
    @Data
    static class CreateMemberRequest{
        //엔티티에 예외를 하지 않고 api스펙에 맞게 직접 해주는 것이 좋다
        //엔티티에 할 경우 요청하는 api마다 필요로 하지 않는 벨리데이션이 발생
        @NotEmpty
        private String name;
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


