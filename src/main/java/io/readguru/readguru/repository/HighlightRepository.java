package io.readguru.readguru.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.readguru.readguru.domain.Highlight;

@Repository
public interface HighlightRepository extends JpaRepository<Highlight, Integer> {

    // @Query(value = "SELECT h FROM highlights h JOIN FETCH h.title")
    // public List<Highlight> findAll();

    List<Highlight> findByUserId(String userId);

    List<Highlight> findByTitleIdAndUserId(int titleId, String userId);

    Optional<Highlight> findByIdAndUserId(int id, String userId);

    void deleteByIdAndUserId(int id, String userId);
}
