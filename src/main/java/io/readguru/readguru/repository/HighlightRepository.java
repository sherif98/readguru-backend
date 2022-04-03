package io.readguru.readguru.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.readguru.readguru.domain.Highlight;

@Repository
public interface HighlightRepository extends JpaRepository<Highlight, Integer> {

    @Query(value = "SELECT h FROM highlights h LEFT JOIN FETCH h.tags JOIN FETCH h.title WHERE h.user.id = :userId")
    Set<Highlight> findByUserId(String userId);

    @Query(value = "SELECT h FROM highlights h LEFT JOIN FETCH h.tags JOIN FETCH h.title WHERE h.title.id = :titleId AND h.user.id = :userId")
    Set<Highlight> findByTitleIdAndUserId(int titleId, String userId);

    @Query(value = "SELECT h FROM highlights h LEFT JOIN FETCH h.tags JOIN FETCH h.title WHERE h.id = :id AND h.user.id = :userId")
    Optional<Highlight> findByIdAndUserId(int id, String userId);

    @Query(value = "DELETE FROM highlights h WHERE h.id = :id AND h.user.id = :userId")
    @Modifying
    void deleteByIdAndUserId(int id, String userId);
}
