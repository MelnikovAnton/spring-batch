package ru.melnikov.springbatch.model.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Comment")
public class CommentSql {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookSql book;
    @Column
    private String comment;

    @Column
    private String mongoId;

    public CommentSql(BookSql book, String comment) {
        this.book = book;
        this.comment = comment;
    }
}
