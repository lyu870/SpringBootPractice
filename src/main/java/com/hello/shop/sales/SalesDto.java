package com.hello.shop.sales;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;


@Getter
@Setter
@AllArgsConstructor
public class SalesDto {
    private String title;
    private Integer price;
    private Integer count;
}


