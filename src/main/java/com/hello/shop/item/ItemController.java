package com.hello.shop.item;

import com.hello.shop.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

// 데이터 및 html을 보내주는 역할 : ItemController
// 댓글 페이지네이션 미구현상태.
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final CommentService commentService;

    // list페이지
    @GetMapping("/list")
    String list(Model model){
        List<Item> result = itemService.getAllItems();
        model.addAttribute("items", result);
        return "redirect:/list/page/1";
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
            model.addAttribute("comments", commentService.getCommentsOfItem(id));
            return "detail.html";
        } else {
            // 예외 발생 시 아래 리턴.
            return "redirect:/list/page/1";
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
    String writePost(@RequestParam String title,
                     @RequestParam String price) { // 문자열로 받고 직접 검증
        try {
            int p = Integer.parseInt(price.trim());
            if (p < 0) {
                throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
            }
            itemService.saveItem(title, p);
            return "redirect:/list/page/1";
        } catch (NumberFormatException e) {
            // 숫자 아님
            // 에러 메시지 보여주고 다시 작성 화면으로 돌려보내거나, 쿼리스트링으로 표시
            return "redirect:/write?error=price";
        }
    }

    // 게시글 수정 요청
    @GetMapping("/edit/{id}")
    String edit(@PathVariable Long id, Model model) {
        Optional<Item> result = itemRepository.findById(id);
        if (result.isPresent()) {
            model.addAttribute("data", result.get());
            return "edit.html";
        } else {
            return "redirect:/list/page/1";
        }
    }

    // 게시글 수정 완료요청
    @PostMapping("/edit")
    public String editPost(@RequestParam Long id,
                           @RequestParam String title,
                           @RequestParam Integer price) {

        itemService.editItem(id, title, price);
        return "redirect:/list/page/1";
    }

    // 게시글 삭제 요청
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.status(200).body("삭제완료");
    }

    
    // === list페이지 ===
    @GetMapping("/list/page/{num}")
    String getListPage(Model model, @PathVariable Integer num){
        int pageSize = 5; // 페이지당 아이템 수
        int pageLinkCount = 5; // 화면에 보여줄 페이지 번호 최대 개수

        Page<Item> page = itemRepository.findAll(
                PageRequest.of(num - 1, pageSize)
        );

        int current = page.getNumber() + 1;        // 1-based
        int totalPages = page.getTotalPages();     // 전체 페이지 수

        // 안전장치: 페이지가 0개일 때 (아예 게시글이 없을 때)
        if (totalPages == 0) {
            totalPages = 1;
        }

        // 페이지네이션 번호 범위 계산
        // current를 기준으로 좌우로 펼치되 전체 5개 제한
        int half = pageLinkCount / 2; // 5라면 half=2
        int startPage = current - half;
        int endPage = current + half;

        // 범위 보정 1: start < 1이면 앞으로 부족한 만큼 뒤로 밀어줌
        if (startPage < 1) {
            endPage += (1 - startPage);
            startPage = 1;
        }

        // 범위 보정 2: end > totalPages면 start도 앞으로 당김
        if (endPage > totalPages) {
            startPage -= (endPage - totalPages);
            endPage = totalPages;
        }

        // 범위 보정 3: start가 다시 1보다 작아질 수도 있으므로 사전방지
        if (startPage < 1) {
            startPage = 1;
        }

        model.addAttribute("items", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("current", current);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        model.addAttribute("searchMode", false); //searchMode error 방지.

        return "list.html";
    }

    // === 검색 폼 제출 → 1페이지로 리다이렉트 ===
    @PostMapping("/search")
    String postSearch(@RequestParam String searchText) {
        // 빈 검색 방지
        if (searchText == null || searchText.isBlank()) {
            return "redirect:/list/page/1";
        }
        // UnsupportedEncodingException체크예외 회피. 정확히 UTF-8로 인코딩.
        String q = URLEncoder.encode(searchText.trim(), StandardCharsets.UTF_8);
        return "redirect:/search/page/1?q=" + q;
    }

    // === 검색 결과 페이지 (페이지네이션) ===
    @GetMapping("/search/page/{num}")
    String getSearchPage(@PathVariable Integer num,
                         @RequestParam(name = "q") String q,
                         Model model) {

        int pageSize = 5;        // 한 페이지 아이템 수 (목록과 동일)
        int pageLinkCount = 5;   // 페이지 버튼 최대 5개

        Page<Item> page =
                itemRepository.fullTextSearchPage(
                q, PageRequest.of(num - 1, pageSize)
        );

        int current = page.getNumber() + 1;
        int totalPages = page.getTotalPages();
        if (totalPages == 0) {
            totalPages = 1;          // 빈 결과 방어
        }

        // 페이지 번호 범위 계산(최대 5개)
        int half = pageLinkCount / 2;                 // 5 → 2
        int startPage = current - half;
        int endPage = current + half;

        if (startPage < 1) { endPage += (1 - startPage); startPage = 1; }
        if (endPage > totalPages) { startPage -= (endPage - totalPages); endPage = totalPages; }
        if (startPage < 1) startPage = 1;

        // 모델 채우기 (list.html을 그대로 재사용)
        model.addAttribute("items", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("current", current);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 검색 헤더
        model.addAttribute("searchMode", true);
        model.addAttribute("query", q);
        model.addAttribute("count", page.getTotalElements());

        return "list.html";
    }




    // list페이지 수정 전
//    @GetMapping("/list/page/{num}")
//    String getListPage(Model model, @PathVariable Integer num){
//        int pageSize = 5; // 페이지당 5개 게시물
//
//        Page<Item> page = itemRepository.findAll(PageRequest.of(num - 1, pageSize));
//        model.addAttribute("items", page.getContent());
//        model.addAttribute("page", page);
//        model.addAttribute("current", page.getNumber() + 1);
//        return "list.html";
//    }
}
