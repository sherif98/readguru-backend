package io.readguru.readguru.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.readguru.readguru.domain.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {

}
