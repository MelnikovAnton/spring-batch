package ru.melnikov.springbatch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.melnikov.springbatch.model.mongo.Author;
import ru.melnikov.springbatch.model.mongo.Book;
import ru.melnikov.springbatch.model.mongo.Comment;
import ru.melnikov.springbatch.model.mongo.Genre;
import ru.melnikov.springbatch.model.sql.AuthorSql;
import ru.melnikov.springbatch.model.sql.BookSql;
import ru.melnikov.springbatch.model.sql.CommentSql;
import ru.melnikov.springbatch.model.sql.GenreSql;
import ru.melnikov.springbatch.repository.AuthorRepository;
import ru.melnikov.springbatch.repository.BookRepository;
import ru.melnikov.springbatch.repository.CommentsRepository;
import ru.melnikov.springbatch.repository.GenreRepository;

import java.util.HashMap;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Configuration
@RequiredArgsConstructor
public class BatchBackupConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final MongoTemplate mongoTemplate;

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final CommentsRepository commentsRepository;


    @Bean
    public ItemReader<Book> bookItemReader() {
        return new MongoItemReaderBuilder<Book>()
                .name("MongoBookReader")
                .template(mongoTemplate)
                .targetType(Book.class)
                .jsonQuery("{}")
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public ItemReader<Author> authorItemReader() {
        return new MongoItemReaderBuilder<Author>()
                .name("MongoAuthorReader")
                .template(mongoTemplate)
                .targetType(Author.class)
                .jsonQuery("{}")
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public ItemReader<Genre> genreItemReader() {
        return new MongoItemReaderBuilder<Genre>()
                .name("MongoGenreReader")
                .template(mongoTemplate)
                .targetType(Genre.class)
                .jsonQuery("{}")
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public ItemReader<Comment> commentItemReader() {
        return new MongoItemReaderBuilder<Comment>()
                .name("MongoCommentReader")
                .template(mongoTemplate)
                .targetType(Comment.class)
                .jsonQuery("{}")
                .sorts(new HashMap<>())
                .build();
    }

    @Bean("commentProcessor")
    public ItemProcessor saveCommentProcessor() {
        return (ItemProcessor<Comment, CommentSql>) comment -> {
            CommentSql commentSql = new CommentSql();
            commentSql.setMongoId(comment.getId());
            BookSql book = bookRepository.findByMongoId(comment.getBook().getId());
            commentSql.setBook(book);
            commentSql.setComment(comment.getComment());
            return commentSql;
        };
    }


    @Bean("bookProcessor")
    public ItemProcessor saveBookProcessor() {
        return (ItemProcessor<Book, BookSql>) book -> {
            BookSql sqlBook = new BookSql(book.getTitle(), book.getContentPath());
            sqlBook.setMongoId(book.getId());
            sqlBook.setAuthors(authorRepository.findAllByMongoId(book.getAuthors().stream()
                    .map(Author::getId)
                    .collect(Collectors.toSet())));
            sqlBook.setGenres(genreRepository.findAllByMongoId(book.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet())));
            return sqlBook;
        };
    }

    @Bean("authorProcessor")
    public ItemProcessor saveAuthorProcessor() {
        return (ItemProcessor<Author, AuthorSql>) author -> {
            AuthorSql authorSql = new AuthorSql(author.getName());
            authorSql.setMongoId(author.getId());
            return authorSql;
        };
    }

    @Bean("genreProcessor")
    public ItemProcessor saveGenreProcessor() {
        return (ItemProcessor<Genre, GenreSql>) genre -> {
            GenreSql genreSql = new GenreSql(genre.getName());
            genreSql.setMongoId(genre.getId());
            return genreSql;
        };
    }


    @Bean
    public ItemWriter<BookSql> bookSqlItemWriter() {
        return new RepositoryItemWriterBuilder<BookSql>()
                .repository(bookRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public ItemWriter<AuthorSql> authorSqlItemWriter() {
        return new RepositoryItemWriterBuilder<AuthorSql>()
                .repository(authorRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public ItemWriter<GenreSql> genreSqlItemWriter() {
        return new RepositoryItemWriterBuilder<GenreSql>()
                .repository(genreRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public ItemWriter<CommentSql> commentSqlItemWriter() {
        return new RepositoryItemWriterBuilder<CommentSql>()
                .repository(commentsRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step saveAuthorStep(ItemWriter<AuthorSql> writer, ItemReader<Author> reader, ItemProcessor authorProcessor) {
        return stepBuilderFactory.get("backupAuthor")
                .allowStartIfComplete(true)
                .chunk(5)
                .reader(reader)
                .processor(authorProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step saveGenreStep(ItemWriter<GenreSql> writer, ItemReader<Genre> reader, ItemProcessor genreProcessor) {
        return stepBuilderFactory.get("backupGenre")
                .allowStartIfComplete(true)
                .chunk(5)
                .reader(reader)
                .processor(genreProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step saveBookStep(ItemWriter<BookSql> writer, ItemReader<Book> reader, ItemProcessor bookProcessor) {
        return stepBuilderFactory.get("backupBook")
                .allowStartIfComplete(true)
                .chunk(5)
                .reader(reader)
                .processor(bookProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step saveCommentStep(ItemWriter<CommentSql> writer, ItemReader<Comment> reader, ItemProcessor commentProcessor) {
        return stepBuilderFactory.get("backupComments")
                .allowStartIfComplete(true)
                .chunk(5)
                .reader(reader)
                .processor(commentProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job saveAllBooksToDB(Step saveBookStep, Step saveGenreStep, Step saveAuthorStep,Step saveCommentStep) {
        return jobBuilderFactory.get("saveAllBooksToDB")
                .incrementer(new RunIdIncrementer())
                .start(saveAuthorStep)
                .next(saveGenreStep)
                .next(saveBookStep)
                .next(saveCommentStep)
                .build();
    }

}
