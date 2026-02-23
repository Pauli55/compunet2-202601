package com.example.service;

import java.util.List;

import com.example.model.Permissions;
import com.example.repository.IPermissionRepository;

public class PermissionService {

    private IPermissionRepository permissionRepository;

    public PermissionService(IPermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<Permissions> findAll() { return permissionRepository.findAll(); }

    public Permissions findById(Integer id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado con id: " + id));
    }

    public Permissions save(Permissions permission) { return permissionRepository.save(permission); }

    public boolean delete(Integer id) { return permissionRepository.delete(id); }
}
