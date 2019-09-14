package ru.melnikov.springbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melnikov.springbatch.model.rdb.BookEntity;

@Repository
public interface BookRepository extends JpaRepository<BookEntity,Long> {

   BookEntity findByMongoId(String mongoId);
}
