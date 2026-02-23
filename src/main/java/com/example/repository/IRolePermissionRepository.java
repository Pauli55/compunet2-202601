package com.example.repository;

import com.example.model.RolePermissions;

import java.util.List;
import java.util.Optional;

public interface IRolePermissionRepository {
    List<RolePermissions> findAll();
    Optional<RolePermissions> findById(Integer id);
    //Cuando es una clase intermedia también tengo que tener en cuenta esa relación y que se puede acceser por el id de las dos clases
    List<RolePermissions> findByRoleId(Integer roleId);
    List<RolePermissions> findByPermissionId(Integer permissionId);
    //
    RolePermissions save(RolePermissions roleP);
    boolean delete(Integer id);
    boolean deleteByRoleAndPermission(Integer roleId, Integer permissionId);
}
