package ru.melnikov.springbatch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@Document
public class Comment {

//    @Id
    private String id;

//    @DBRef
    private Book book;

    private String comment;

    public Comment(Book book, String comment) {
        this.book = book;
        this.comment = comment;
    }
}
