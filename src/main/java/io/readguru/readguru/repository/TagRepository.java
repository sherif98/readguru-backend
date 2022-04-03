package io.readguru.readguru.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.readguru.readguru.domain.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {

    @Query(value = "SELECT t FROM tags t WHERE t.user.id = :userId")
    Set<Tag> findByUserId(String userId);

    @Query(value = "SELECT t FROM tags t WHERE t.id = :id AND t.user.id = :userId")
    Optional<Tag> findByIdAndUserId(String id, String userId);

    @Query(value = "DELETE FROM tags t WHERE t.id = :id AND t.user.id = :userId")
    @Modifying
    void deleteByIdAndUserId(String id, String userId);

}
