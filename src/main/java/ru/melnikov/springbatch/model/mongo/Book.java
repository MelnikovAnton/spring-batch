package ru.melnikov.springbatch.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Book {

    @Id
    private String id;

    private String title;
    private Set<Author> authors = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private String contentPath;


    public Book(String id, String title, String contentPath) {
        this.id = id;
        this.title = title;
        this.contentPath = contentPath;
    }

    public Book(String title, String contentPath) {
        this.title = title;
        this.contentPath = contentPath;
    }

    public void addAuthor(Author author) {
        this.authors.add(author);
    }

    public void addGenre(Genre genre) {
        this.genres.add(genre);
    }
}
