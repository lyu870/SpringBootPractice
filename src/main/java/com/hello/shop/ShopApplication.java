package com.hello.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopApplication.class, args);
		var test = new Test();
		System.out.println(test.start);
	}

}

class Test {
	String start = "서버 정상작동.";
}