package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B") //현재 부모 테이블에서 SingleTalbe전략을 사용하기 때문에  저장을 할때 DB입장에서 구분을 하기 위해 구분자 이름
@Getter
@Setter
public class Book extends  Item{

    private String author;
    private String isbn;

}
