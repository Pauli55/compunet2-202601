package com.example.repository;

import com.example.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface IUserRepository {

    List<User> findAll();
    Optional<User> findById(Integer id);
    User save(User user);
    boolean delete(Integer id);


}
