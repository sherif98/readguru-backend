package io.readguru.readguru.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.readguru.readguru.domain.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {

    List<Tag> findByUserId(String userId);

    Optional<Tag> findByIdAndUserId(String id, String userId);

    void deleteByIdAndUserId(String id, String userId);

}
