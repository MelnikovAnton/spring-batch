package ru.melnikov.springbatch;

import org.h2.tools.Console;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class SpringBatchApplication {

	public static void main(String[] args) throws SQLException {
		SpringApplication.run(SpringBatchApplication.class, args);
        Console.main(args);
	}

}
