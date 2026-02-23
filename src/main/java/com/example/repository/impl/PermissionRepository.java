package com.example.repository.impl;

import com.example.model.Permissions;
import com.example.repository.IPermissionRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class PermissionRepository implements IPermissionRepository {

    private List<Permissions> permissions = new ArrayList<>();
    private int nextId = 1;
    private Logger logger = Logger.getLogger(PermissionRepository.class.getName());

    @PostConstruct
    public void init() {
        logger.info("PermissionRepository initialized");
        save(new Permissions("CREATE_USER",   "Puede crear usuarios"));
        save(new Permissions("READ_USER",     "Puede ver usuarios"));
        save(new Permissions("UPDATE_USER",   "Puede editar usuarios"));
        save(new Permissions("DELETE_USER",   "Puede eliminar usuarios"));
        save(new Permissions("CREATE_GAME",   "Puede crear juegos"));
        save(new Permissions("READ_GAME",     "Puede ver juegos"));
        save(new Permissions("UPDATE_GAME",   "Puede editar juegos"));
        save(new Permissions("DELETE_GAME",   "Puede eliminar juegos"));
        save(new Permissions("MANAGE_ROLES",  "Puede gestionar roles y permisos"));
    }

    @Override
    public List<Permissions> findAll() { return new ArrayList<>(permissions); }

    @Override
    public Optional<Permissions> findById(Integer id) {
        if (id == null) return Optional.empty();
        return permissions.stream().filter(p -> id.equals(p.getId())).findFirst();
    }

    @Override
    public Permissions save(Permissions p) {
        if (p == null) return null;
        if (p.getId() == null) { p.setId(nextId++); permissions.add(p); return p; }
        for (int i = 0; i < permissions.size(); i++) {
            if (p.getId().equals(permissions.get(i).getId())) { permissions.set(i, p); return p; }
        }
        permissions.add(p);
        return p;
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) return false;
        return permissions.removeIf(p -> id.equals(p.getId()));
    }

    @PreDestroy
    public void destroy() { logger.info("PermissionRepository destroyed"); permissions.clear(); }
}
