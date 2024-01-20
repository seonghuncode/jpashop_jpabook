package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class MemberForm {

    @NotEmpty(message = "회원 이름은 필수 입니다.")//spring에서 해당 값을 validation해서 반드시 받아주도록 한다.
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
