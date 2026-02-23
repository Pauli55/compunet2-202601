package com.example.repository.impl;
import com.example.model.Games;
import com.example.model.Roles;
import com.example.model.User;
import com.example.repository.IRoleRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.example.model.Roles;
import com.example.repository.IRoleRepository;
// PENDIENTE: averiguar para que sirve esta monda de a continuacion
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;


public class RoleRepository1 implements IRoleRepository {
    private List<Roles> roles = new ArrayList<Roles>();
    private int nextId = 1; //y esto que hace?

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
        return Optional.empty();
    }

    @Override
    public Roles save(Roles roles) {
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        return false;
    }
}
