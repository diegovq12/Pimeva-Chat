package com.pimeva.pimevachat.interfaces;

import com.pimeva.pimevachat.modelos.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    User findByUsernameAndPassword(String username, String password);
}
