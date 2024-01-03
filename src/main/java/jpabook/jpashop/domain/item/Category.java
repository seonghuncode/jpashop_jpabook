package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    @ManyToMany
    @JoinTable(name = "category_item",
        joinColumns = @JoinColumn(name = "category_id"), //.중간 테이블에 있는 id
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    // 객체는 컬렉션이 있기 때문에 다대다 매핑이 가능하지만, 관계형 데이터베이스는 다대다 매핑이 불가능 하기 때문에 일대다/다대일로 변환하는 중간 테이블 필요
    //실무에서는 거의 사용하지 않는다. (필드 추가가 불가능 하기 때문)
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();


    //연관관계 메서드
    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(this);
    }

}
