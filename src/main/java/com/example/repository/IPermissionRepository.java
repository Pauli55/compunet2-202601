package com.example.repository;
import java.util.List;
import java.util.Optional;

import com.example.model.Permissions;

public interface IPermissionRepository {
    List<Permissions> findAll();
    Optional<Permissions> findById(Integer id);
    Permissions save(Permissions permission);
    boolean delete(Integer id);
}
