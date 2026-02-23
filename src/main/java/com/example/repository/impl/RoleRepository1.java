package com.example.repository.impl;
import com.example.model.Games;
import com.example.model.Roles;
import com.example.model.User;
import com.example.repository.IRoleRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.example.model.Roles;
import com.example.repository.IRoleRepository;
// PENDIENTE: averiguar para que sirve esta monda de a continuacion
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepository1 implements IRoleRepository {
    private List<Roles> roles = new ArrayList<Roles>();
    private int nextId = 1; //Es el contador para asignar IDs automáticamente, simulando lo que haría un AUTO_INCREMENT
    private Logger logger = Logger.getLogger(IRoleRepository.class.getName());
 //No es necesario, sirve para reemplazar el system.aot

    @PostConstruct
    public void init() {
        save(new Roles("ADMIN", "administrador del sistema"));
        save(new Roles("PLAYER", "Pa todo el que quiera jugar"));
    }

    @Override
    public List<Roles> findAll() {
        return new ArrayList<Roles>(roles);
    }

    @Override
    public Optional<Roles> findById(Integer id) {
        if (id == null) return Optional.empty();
        for (Roles role : roles) {
            if (id.equals(role.getId())) return Optional.of(role);
        }
        return Optional.empty();
    }

    @Override
    public Roles save(Roles role) {
        if (role == null) return null;
        if (role.getId() == null) {
            role.setId(nextId++);
            roles.add(role);
            return role;
        }
        for (int i = 0; i < roles.size(); i++) {
            if (role.getId().equals(roles.get(i).getId())) {
                roles.set(i, role);
                return role;
            }
        }
        roles.add(role);
        return role;
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) return false;
        for (int i = 0; i < roles.size(); i++) {
            if (id.equals(roles.get(i).getId())) {
                roles.remove(i);
                return true;
            }
        }
        return false;
    }

    @PreDestroy
    public void destroy() {
        logger.info("RoleRepository destroyed");
        roles.clear();
        nextId = 1;
    }
}
