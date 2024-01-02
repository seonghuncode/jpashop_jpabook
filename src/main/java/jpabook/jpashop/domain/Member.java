package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded //내장 타입을 포함했다는 의미
    private Address address;

    @OneToMany(mappedBy = "member") //하나의 회원이 여러개의 상품 주문, mappedBy : Order의 member와 매핑하는 것이 아닌 매핑 된 것을 의미한다
    private List<Order> orders = new ArrayList<>();
}
