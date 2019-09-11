package ru.melnikov.springbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melnikov.springbatch.model.sql.AuthorSql;

import java.util.List;
import java.util.Set;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorSql,Long> {

    Set<AuthorSql> findAllByMongoId(Set<String> mongoIds);
}
