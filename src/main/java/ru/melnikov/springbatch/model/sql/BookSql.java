package ru.melnikov.springbatch.model.sql;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Book")
public class BookSql {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "author_book",
            joinColumns = {@JoinColumn(name = "book_id")},
            inverseJoinColumns = {@JoinColumn(name = "author_id")}
    )
    @Fetch(FetchMode.SUBSELECT)
    private Set<AuthorSql> authors = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "genre_book",
            joinColumns = {@JoinColumn(name = "book_id")},
            inverseJoinColumns = {@JoinColumn(name = "genre_id")}
    )
    @Fetch(FetchMode.JOIN)
    private Set<GenreSql> genres = new HashSet<>();

    @Column
    private String contentPath;

    @Column
    private String mongoId;

    public BookSql(long id, String title, String contentPath) {
        this.id = id;
        this.title = title;
        this.contentPath = contentPath;
    }

    public BookSql(String title, String contentPath) {
        this.title = title;
        this.contentPath = contentPath;
    }

    public void addAuthor(AuthorSql author) {
        this.authors.add(author);
    }

    public void addGenre(GenreSql genre) {
        this.genres.add(genre);
    }
}