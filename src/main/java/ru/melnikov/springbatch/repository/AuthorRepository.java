package ru.melnikov.springbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melnikov.springbatch.model.rdb.AuthorEntity;

import java.util.Set;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity,Long> {

    Set<AuthorEntity> findAllByMongoId(Set<String> mongoIds);
}
