package ru.melnikov.springbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melnikov.springbatch.model.sql.CommentSql;

@Repository
public interface CommentsRepository extends JpaRepository<CommentSql, Long> {
}
