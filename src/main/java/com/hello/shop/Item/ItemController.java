package com.hello.shop.Item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// 데이터 및 html을 보내주는 역할 : ItemController
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final ItemService itemService;

    // list페이지
    @GetMapping("/list")
    String list(Model model){
        List<Item> result = itemService.getAllItems();
        model.addAttribute("items", result);

        return "list.html";
    }


    // 글작성페이지
    @GetMapping("/write")
    String wrtie(Model model) {
        return "write.html";
    }

    // detail페이지 컨트롤. model사용.
    @GetMapping("/detail/{id}")
    String detail(@PathVariable Long id, Model model) {

        Optional<Item> result = itemRepository.findById(id);
        if (result.isPresent()){
            model.addAttribute("data", result.get());
            return "detail.html";
        } else {
            // 예외 발생 시 아래 리턴.
            return "redirect:/list";
        }
    }

    // 모든 API에서 Exception발생 시 handler() 내부의 코드 실행.


    // ↓ Model미사용방식. html에 Item데이터를 넘겨주어 Thymeleaf문법을 쓸 것이기 때문에 Model사용방식 채택.
//    @GetMapping("/detail/{id}")
//    String detail(@PathVariable Long id) {
//        Optional<Item> result = itemRepository.findById(id);
//        if (result.isPresent()){
//            System.out.println(result.get());
//            return "detail.html";
//        } else {
//            return "list.html";
//        }
//    }

    // 게시글 작성 요청
    @PostMapping("/add")
    String writePost(String title, Integer price) {
        itemService.saveItem(title, price);
        return "redirect:/list";
    }

    // 게시글 수정 요청
    @GetMapping("/edit/{id}")
    String edit(@PathVariable Long id, Model model) {
        Optional<Item> result = itemRepository.findById(id);
        if (result.isPresent()) {
            model.addAttribute("data", result.get());
            return "edit.html";
        } else {
            return "redirect:/list";
        }
    }

    // 게시글 수정 완료요청
    @PostMapping("/edit")
    public String editPost(@RequestParam Long id,
                           @RequestParam String title,
                           @RequestParam Integer price) {

        itemService.editItem(id, title, price);
        return "redirect:/list";
    }

    // 게시글 삭제 요청
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.status(200).body("삭제완료");
    }

    @GetMapping("/test2")
    String testItem() {
        return "redirect:/list";
    }

}
