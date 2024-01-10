package jpabook.jpashop.domain.item;

import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();


    /*
     * 비즈니스 로직 작성 (재고 증감 로직)
     * 엔티티 자체에서 해결할 수 있는 로직은 엔티티 안에 개발 하는 것이 좋다.
     *
     * 데이터를 가지고 있는쪽에 비즈니스 메서드를 가지고 있는 것이 유리
     * 외부에서 Setter를 통해 값을 변경하는 것보다 재고 증감 메서드 같이 특정 메서드를 통해 값을 변경하는 것이 객체지향적이다
     */
    //제고 증가
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    //재고 감소
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }


}