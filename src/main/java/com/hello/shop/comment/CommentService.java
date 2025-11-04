package com.hello.shop.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;

    public List<Comment> getCommentsOfItem(Long itemId) {
        return commentRepository.findAllByParentId(itemId);
    }

    @Transactional // (readOnly=false)
    public void addComment(Long parentId, String username, String content) {
        if (username == null) throw new IllegalStateException("로그인이 필요합니다.");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("댓글 내용을 입력하세요.");
        if (content.length() > 1000) throw new IllegalArgumentException("댓글은 1000자 이하로 입력하세요.");

        Comment c = new Comment();
        c.setParentId(parentId);
        c.setUsername(username);
        c.setContent(content);
        commentRepository.save(c);
    }
}


