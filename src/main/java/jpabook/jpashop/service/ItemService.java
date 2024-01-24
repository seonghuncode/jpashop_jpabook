package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    /*
    클래스에 Transaction은 readOnly이기 때문에 수정이 불가능 하다.
    메서드 위에 Transactional 어노테이션을 사용하면 메서드에 붙어 있는것이 우선권이 높기 때문에 수정 가능
     */
    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    //준영속성 엔티티를 변경하기 위해 변경 감지를 사용하는 방법
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity){
        Item findItem = itemRepository.findOne(itemId); //DB로 부터 영속 상태의 엔티티를 찾아온다 (findItem은 영속상태)
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
        //해당 로직이 끝나묜 Transaction어노테이션으로 인해 해당 트랜잭션이 commit된다. -> JPA는 flush한다(JPA가 변경된 데이터를 찾는다) -> 변경된 데이터를 DB에 반영
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }

}
