package com.example.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.example.book.entity.Book;
import com.example.book.entity.QBook;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>, QuerydslPredicateExecutor<Book> {

    public default Predicate makePredicate(String type, String keyword) {
        QBook book = QBook.book;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(book.code.gt(0));

        if (type == null) {
            return builder;
        }
        if (type.equals("t")) {
            builder.and(book.title.contains(keyword));
        } else {
            builder.and(book.author.contains(keyword));
        }
        return builder;
    }
}
