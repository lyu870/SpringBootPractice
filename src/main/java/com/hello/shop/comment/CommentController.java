package com.hello.shop.comment;

import com.hello.shop.member.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final CommentService commentService;


    @PostMapping("/comment")
    String postComment(@RequestParam String content,
                       @RequestParam Long parentId,
                       @AuthenticationPrincipal CustomUser user) { // 사용자 바로 주입
        if (user == null) { // 미로그인 방어
            return "redirect:/login?redirect=/detail/" + parentId;
        }
        commentService.addComment(parentId, user.getUsername(), content);
        return "redirect:/detail/" + parentId;
    }
}
