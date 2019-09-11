package ru.melnikov.springbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melnikov.springbatch.model.sql.GenreSql;

import java.util.Set;

@Repository
public interface GenreRepository extends JpaRepository<GenreSql, Long> {

   Set<GenreSql> findAllByMongoId(Set<String> mongoIds);
}
