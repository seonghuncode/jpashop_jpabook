package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model){

        //DB에 모든 멤버를 가지고 온다
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems(); // -> 모든 아이템을 DB에서 가지고 온다

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm"; //주문 페이지로 이동동
   }


   //상품 주문 페이지에서 주문 데이터를 입력하고 주문할 경우
   @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count){
        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
   }



}
