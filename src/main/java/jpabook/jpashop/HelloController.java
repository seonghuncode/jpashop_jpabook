package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model){ // Model : controller에서 model을 활용해서 view로 넘길 수 있다.
        model.addAttribute("data", "hello!");
        return "hello"; //통상적으로 파일 이름이된다. (경로 : resources  > templates > view이름)
    }

}
