package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // JPA의 내장타입 이기 때문에 사용
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }
    //값 타입의 경우 변경이 불가능 해야 하기 때문에 Getter만 허용하고 생성자를 통해 초기 에만 만들어지도록 한다.
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

}
