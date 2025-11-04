package com.hello.shop.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto {
    String username;
    String displayName;
    Long id;

    MemberDto(String a, String b){
        this.username = a;
        this.displayName = b;
    }
    MemberDto(String a, String b, Long id){
        this.username = a;
        this.displayName = b;
        this.id = id;
    }
}

// object하나를 유저에게 보내줄 때 JSON으로 변환해서 보내줌.
// 데이터에 public을 해주거나 getter를 붙여줘야 spring이 JSON으로 마음대로 변환가능.