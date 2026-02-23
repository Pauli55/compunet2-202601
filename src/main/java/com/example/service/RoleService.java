package com.example.service;

import java.util.List;

import com.example.model.Roles;
import com.example.repository.IRoleRepository;

public class RoleService {

    private IRoleRepository roleRepository;

    public RoleService(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Roles> findAll() {
        return roleRepository.findAll();
    }

    public Roles findById(Integer id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + id));
    }

    public Roles save(Roles role) {
        return roleRepository.save(role);
    }

    public boolean delete(Integer id) {
        return roleRepository.delete(id);
    }
}
