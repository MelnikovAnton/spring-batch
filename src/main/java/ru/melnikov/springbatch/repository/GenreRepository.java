package ru.melnikov.springbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melnikov.springbatch.model.rdb.GenreEntity;

import java.util.Set;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Long> {

   Set<GenreEntity> findAllByMongoId(Set<String> mongoIds);
}
