package com.hello.shop.Item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Lombok 의존성주입
public class ItemService {
    private final ItemRepository itemRepository;

    // 저장 로직
    public void saveItem(String title, Integer price) {
        if (price == null || price < 0) {
            throw new IllegalArgumentException("가격은 음수가 될 수 없습니다.");
        }
        if (price > 10_000_000) {
            throw new IllegalArgumentException("가격이 너무 큽니다. (최대 10,000,000)");
        }

        Item item = new Item();
        item.setTitle(title);
        item.setPrice(price);
        itemRepository.save(item);
    }

    // 게시글 조회 로직
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }


    // 수정 로직
    public void editItem(Long id, String title, Integer price) {
        Optional<Item> result = itemRepository.findById(id);
        if (result.isPresent()) {
            Item item = result.get();
            item.setTitle(title);
            item.setPrice(price);
            itemRepository.save(item);
        } else {
            throw new IllegalArgumentException("해당 상품이 존재하지 않습니다.");
        }
    }

    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 상품이 존재하지 않습니다.");
        }
        itemRepository.deleteById(id);
    }
}