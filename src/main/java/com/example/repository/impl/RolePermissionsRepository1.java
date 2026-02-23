package com.example.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.model.RolePermissions;
import com.example.repository.IRolePermissionRepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Repository
public class RolePermissionsRepository1 implements IRolePermissionRepository {

    private List<RolePermissions> list = new ArrayList<>();
    private int nextId = 1;
    private Logger logger = Logger.getLogger(IRolePermissionRepository.class.getName());

    @PostConstruct
    public void init() {
        logger.info("RolePermissionRepository initialized");
        // ADMIN (roleId=1) -> todos los permisos (1..9)
        for (int i = 1; i <= 9; i++) save(new RolePermissions(1, i));
        // USER (roleId=2) -> solo lectura de users y games
        save(new RolePermissions(2, 2)); // READ_USER
        save(new RolePermissions(2, 6)); // READ_GAME
        // MODERATOR (roleId=3) -> leer y editar games
        save(new RolePermissions(3, 6)); // READ_GAME
        save(new RolePermissions(3, 7)); // UPDATE_GAME
        save(new RolePermissions(3, 2)); // READ_USER
    }

    @Override
    public List<RolePermissions> findAll() { return new ArrayList<>(list); }

    @Override
    public Optional<RolePermissions> findById(Integer id) {
        if (id == null) return Optional.empty();
        return list.stream().filter(rp -> id.equals(rp.getId())).findFirst();
    }

    @Override
    public List<RolePermissions> findByRoleId(Integer roleId) {
        return list.stream().filter(rp -> roleId != null && roleId.equals(rp.getRoleId())).collect(Collectors.toList());
    }

    @Override
    public List<RolePermissions> findByPermissionId(Integer permissionId) {
        return list.stream().filter(rp -> permissionId != null && permissionId.equals(rp.getPermissionId())).collect(Collectors.toList());
    }

    @Override
    public RolePermissions save(RolePermissions rp) {
        if (rp == null) return null;
        if (rp.getId() == null) { rp.setId(nextId++); list.add(rp); return rp; }
        for (int i = 0; i < list.size(); i++) {
            if (rp.getId().equals(list.get(i).getId())) { list.set(i, rp); return rp; }
        }
        list.add(rp);
        return rp;
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) return false;
        return list.removeIf(rp -> id.equals(rp.getId()));
    }

    @Override
    public boolean deleteByRoleAndPermission(Integer roleId, Integer permissionId) {
        return list.removeIf(rp -> roleId != null && permissionId != null
                && roleId.equals(rp.getRoleId()) && permissionId.equals(rp.getPermissionId()));
    }

    @PreDestroy
    public void destroy() { logger.info("RolePermissionRepository destroyed"); list.clear(); }
}
