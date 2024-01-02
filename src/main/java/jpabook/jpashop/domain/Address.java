package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // JPA의 내장타입 이기 때문에 사용
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;
}
