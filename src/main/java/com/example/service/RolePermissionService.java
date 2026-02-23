package com.example.service;

import java.util.List;

import com.example.model.Permissions;
import com.example.model.RolePermissions;
import com.example.model.Roles;
import com.example.repository.IRolePermissionRepository;

public class RolePermissionService {

    private IRolePermissionRepository rolePermissionRepository;

    public RolePermissionService(IRolePermissionRepository rolePermissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
    }

    public List<RolePermissions> findAll() { return rolePermissionRepository.findAll(); }

    public RolePermissions findById(Integer id) {
        return rolePermissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RolePermission no encontrado con id: " + id));
    }

    public List<RolePermissions> findByRoleId(Integer roleId) {
        return rolePermissionRepository.findByRoleId(roleId);
    }

    public List<RolePermissions> findByPermissionId(Integer permissionId) {
        return rolePermissionRepository.findByPermissionId(permissionId);
    }

    public RolePermissions assign(Integer roleId, Integer permissionId) {
        // Evitar duplicados
        boolean exists = rolePermissionRepository.findByRoleId(roleId).stream()
                .anyMatch(rp -> permissionId.equals(rp.getPermissionId()));
        if (exists) throw new IllegalArgumentException("El rol ya tiene ese permiso asignado.");
        return rolePermissionRepository.save(new RolePermissions(roleId, permissionId));
    }

    public boolean revoke(Integer roleId, Integer permissionId) {
        return rolePermissionRepository.deleteByRoleAndPermission(roleId, permissionId);
    }

    public boolean delete(Integer id) { return rolePermissionRepository.delete(id); }
}
