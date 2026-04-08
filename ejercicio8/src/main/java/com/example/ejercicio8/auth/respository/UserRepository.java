package com.example.ejercicio8.auth.respository;

import java.util.Optional;

import com.example.ejercicio8.auth.models.UserEntity;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}