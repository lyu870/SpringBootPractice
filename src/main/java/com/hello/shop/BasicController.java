package com.hello.shop;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BasicController {
    @GetMapping("/")
    String hello() {
        return "index.html";
    }

    @GetMapping("/about")
    @ResponseBody
    String about() {
        return "피싱사이트임";
    }

    @GetMapping("/mypage")
    @ResponseBody
    String mypage() {
        return "내 페이지에요";
    }
}
