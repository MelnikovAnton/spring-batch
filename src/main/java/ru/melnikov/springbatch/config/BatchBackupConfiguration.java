package ru.melnikov.springbatch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
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
import ru.melnikov.springbatch.model.rdb.AuthorEntity;
import ru.melnikov.springbatch.model.rdb.BookEntity;
import ru.melnikov.springbatch.model.rdb.CommentEntity;
import ru.melnikov.springbatch.model.rdb.GenreEntity;
import ru.melnikov.springbatch.repository.AuthorRepository;
import ru.melnikov.springbatch.repository.BookRepository;
import ru.melnikov.springbatch.repository.CommentsRepository;
import ru.melnikov.springbatch.repository.GenreRepository;

import java.util.Collections;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Configuration
@RequiredArgsConstructor
public class BatchBackupConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final JobRegistry jobRegistry;

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
                .sorts(Collections.emptyMap())
                .build();
    }

    @Bean
    public ItemReader<Author> authorItemReader() {
        return new MongoItemReaderBuilder<Author>()
                .name("MongoAuthorReader")
                .template(mongoTemplate)
                .targetType(Author.class)
                .jsonQuery("{}")
                .sorts(Collections.emptyMap())
                .build();
    }

    @Bean
    public ItemReader<Genre> genreItemReader() {
        return new MongoItemReaderBuilder<Genre>()
                .name("MongoGenreReader")
                .template(mongoTemplate)
                .targetType(Genre.class)
                .jsonQuery("{}")
                .sorts(Collections.emptyMap())
                .build();
    }

    @Bean
    public ItemReader<Comment> commentItemReader() {
        return new MongoItemReaderBuilder<Comment>()
                .name("MongoCommentReader")
                .template(mongoTemplate)
                .targetType(Comment.class)
                .jsonQuery("{}")
                .sorts(Collections.emptyMap())
                .build();
    }

    @Bean("commentProcessor")
    public ItemProcessor<Comment, CommentEntity> saveCommentProcessor() {
        return comment -> {
            CommentEntity commentEntity = new CommentEntity();
            commentEntity.setMongoId(comment.getId());
            BookEntity book = bookRepository.findByMongoId(comment.getBook().getId());
            commentEntity.setBook(book);
            commentEntity.setComment(comment.getComment());
            return commentEntity;
        };
    }


    @Bean("bookProcessor")
    public ItemProcessor<Book, BookEntity> saveBookProcessor() {
        return book -> {
            BookEntity sqlBook = new BookEntity(book.getTitle(), book.getContentPath());
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
    public ItemProcessor<Author, AuthorEntity> saveAuthorProcessor() {
        return author -> {
            AuthorEntity authorEntity = new AuthorEntity(author.getName());
            authorEntity.setMongoId(author.getId());
            return authorEntity;
        };
    }

    @Bean("genreProcessor")
    public ItemProcessor<Genre, GenreEntity> saveGenreProcessor() {
        return genre -> {
            GenreEntity genreEntity = new GenreEntity(genre.getName());
            genreEntity.setMongoId(genre.getId());
            return genreEntity;
        };
    }


    @Bean
    public ItemWriter<BookEntity> bookSqlItemWriter() {
        return new RepositoryItemWriterBuilder<BookEntity>()
                .repository(bookRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public ItemWriter<AuthorEntity> authorSqlItemWriter() {
        return new RepositoryItemWriterBuilder<AuthorEntity>()
                .repository(authorRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public ItemWriter<GenreEntity> genreSqlItemWriter() {
        return new RepositoryItemWriterBuilder<GenreEntity>()
                .repository(genreRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public ItemWriter<CommentEntity> commentSqlItemWriter() {
        return new RepositoryItemWriterBuilder<CommentEntity>()
                .repository(commentsRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step saveAuthorStep(ItemWriter<AuthorEntity> writer, ItemReader<Author> reader, ItemProcessor authorProcessor) {
        return stepBuilderFactory.get("backupAuthor")
                .allowStartIfComplete(true)
                .chunk(5)
                .reader(reader)
                .processor(authorProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step saveGenreStep(ItemWriter<GenreEntity> writer, ItemReader<Genre> reader, ItemProcessor genreProcessor) {
        return stepBuilderFactory.get("backupGenre")
                .allowStartIfComplete(true)
                .chunk(5)
                .reader(reader)
                .processor(genreProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step saveBookStep(ItemWriter<BookEntity> writer, ItemReader<Book> reader, ItemProcessor bookProcessor) {
        return stepBuilderFactory.get("backupBook")
                .allowStartIfComplete(true)
                .chunk(5)
                .reader(reader)
                .processor(bookProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step saveCommentStep(ItemWriter<CommentEntity> writer, ItemReader<Comment> reader, ItemProcessor commentProcessor) {
        return stepBuilderFactory.get("backupComments")
                .allowStartIfComplete(true)
                .chunk(5)
                .reader(reader)
                .processor(commentProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job saveAllBooksToDB(Step saveBookStep, Step saveGenreStep, Step saveAuthorStep,Step saveCommentStep) throws DuplicateJobException {
        Job job = jobBuilderFactory.get("saveAllBooksToDB")
                .incrementer(new RunIdIncrementer())
                .start(saveAuthorStep)
                .next(saveGenreStep)
                .next(saveBookStep)
                .next(saveCommentStep)
                .build();

        ReferenceJobFactory referenceJobFactory = new ReferenceJobFactory(job);
        jobRegistry.register(referenceJobFactory);
        return job;
    }

}
