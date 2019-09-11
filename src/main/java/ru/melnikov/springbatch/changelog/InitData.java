package ru.melnikov.springbatch.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import ru.melnikov.springbatch.model.mongo.Author;
import ru.melnikov.springbatch.model.mongo.Book;
import ru.melnikov.springbatch.model.mongo.Comment;
import ru.melnikov.springbatch.model.mongo.Genre;

import java.util.ArrayList;
import java.util.List;

@Component
@ChangeLog(order = "001")
public class InitData {


    private final List<Genre> genres = new ArrayList<>();
    private final List<Author> authors = new ArrayList<>();

    @ChangeSet(order = "000", id = "dropDB", author = "MelnikovAnton", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }


    @ChangeSet(order = "001", id = "initAuthors", author = "MelnikovAnton", runAlways = true)
    public void initAuthors(MongoTemplate template) {
        for (int i = 0; i < 100; i++) {
            template.save(new Author("Author" + i));
        }
        authors.addAll(template.findAll(Author.class));
    }

    @ChangeSet(order = "002", id = "initGenres", author = "MelnikovAnton", runAlways = true)
    public void initGenres(MongoTemplate template) {
        for (int i = 0; i < 100; i++) {
            template.save(new Genre("Genre" + i));
        }
        genres.addAll(template.findAll(Genre.class));
    }

    @ChangeSet(order = "003", id = "initBooks", author = "MelnikovAnton", runAlways = true)
    public void initBooks(MongoTemplate template) {
        for (int i = 0; i < 1000; i++) {
            Book book = new Book("Book"+i, "content");
            book.addAuthor(authors.get((int) (Math.random() * 100)));
            book.addGenre(genres.get((int) (Math.random() * 100)));
            template.save(book);
        }

    }

    @ChangeSet(order = "004", id = "initComments", author = "MelnikovAnton", runAlways = true)
    public void initComments(MongoTemplate template) {
        List<Book> books = template.findAll(Book.class);
        books.forEach(b -> {
            template.save(new Comment(b, "comment"));
            template.save(new Comment(b, "comment2"));
            template.save(new Comment(b, "comment3"));
        });
    }

}
