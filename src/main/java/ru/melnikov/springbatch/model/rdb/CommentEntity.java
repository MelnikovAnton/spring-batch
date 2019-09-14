package ru.melnikov.springbatch.model.rdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Comment")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookEntity book;
    @Column
    private String comment;

    @Column
    private String mongoId;

    public CommentEntity(BookEntity book, String comment) {
        this.book = book;
        this.comment = comment;
    }
}
