package ru.melnikov.springbatch;

import org.h2.tools.Console;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.melnikov.springbatch.model.sql.BookSql;
import ru.melnikov.springbatch.model.sql.CommentSql;
import ru.melnikov.springbatch.repository.BookRepository;
import ru.melnikov.springbatch.repository.CommentsRepository;

import java.sql.SQLException;
import java.util.List;


@SpringBootApplication
@EnableBatchProcessing(modular=true)
public class SpringBatchApplication {

	public static void main(String[] args) throws SQLException {
		ConfigurableApplicationContext ctx = SpringApplication.run(SpringBatchApplication.class, args);
//        Console.main(args);
//
//		BookRepository bookRepo = ctx.getBean(BookRepository.class);
//		List<BookSql> books = bookRepo.findAll();
//		CommentsRepository commentRepo = ctx.getBean(CommentsRepository.class);
//		List<CommentSql> comments = commentRepo.findAll();
//
//		System.out.println("Books="+books.size() + " Comments="+comments.size());
	}

}
