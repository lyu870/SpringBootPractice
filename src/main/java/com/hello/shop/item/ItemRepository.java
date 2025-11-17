package com.hello.shop.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // Page<Item> findPageBy(Pageable page);
    List<Item> findAllByTitleContains(String title);

    @Query(value = "SELECT * FROM shop.item WHERE MATCH(title) AGAINST(?1)",  nativeQuery = true)
    List<Item> fullTextSearch(String text);

    @Query(
            value = """
    SELECT * FROM item
    WHERE to_tsvector('simple', title) @@ plainto_tsquery(:q)
    ORDER BY ts_rank(to_tsvector('simple', title), plainto_tsquery(:q)) DESC
  """,
            countQuery = """
    SELECT COUNT(*) FROM item
    WHERE to_tsvector('simple', title) @@ plainto_tsquery(:q)
  """,
            nativeQuery = true
    )
    Page<Item> fullTextSearchPage(@Param("q") String q, Pageable pageable);
}