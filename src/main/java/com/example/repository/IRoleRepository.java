package com.example.repository;
import java.util.List;
import java.util.Optional;

import com.example.model.Roles;

public interface IRoleRepository {
    List<Roles> findAll();
    Optional<Roles> findById(Integer id);
    Roles save(Roles roles);
    boolean delete(Integer id);
}
