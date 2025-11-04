package com.hello.shop.comment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String username;
    @Column(length = 1000)
    public String content;
    public Long parentId;
}
