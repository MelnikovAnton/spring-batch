package ru.melnikov.springbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melnikov.springbatch.model.sql.BookSql;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookSql,Long> {

   BookSql findByMongoId(String mongoId);
}
