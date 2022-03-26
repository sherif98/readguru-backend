package io.readguru.readguru.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.readguru.readguru.domain.Title;

@Repository
public interface TitleRepository extends JpaRepository<Title, Integer> {

    @Query(value = "SELECT * FROM titles WHERE user_id = ?1", nativeQuery = true)
    List<Title> findByUserId(int userId);

}
