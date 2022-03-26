package io.readguru.readguru.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.readguru.readguru.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
