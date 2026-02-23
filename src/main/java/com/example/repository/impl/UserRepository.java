package com.example.repository.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.repository.IUserRepository;
import org.springframework.stereotype.Repository;

import com.example.model.User;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Repository
public class UserRepository implements IUserRepository {
    
    private List<User> users = new ArrayList<User>();
    private int nextId = 1;

    @PostConstruct
    public void init() {
        save(new User("ana", "ana@example.com", "hash-ana", "Bio de Ana", new Timestamp(System.currentTimeMillis())));
        save(new User("carlos", "carlos@example.com", "hash-carlos", "Bio de Carlos", new Timestamp(System.currentTimeMillis())));
        save(new User("luisa", "luisa@example.com", "hash-luisa", "Bio de Luisa", new Timestamp(System.currentTimeMillis())));
    }

    public List<User> findAll() {
        return new ArrayList<User>(users);
    }

    public Optional<User> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        for (User user : users) {
            if (id.equals(user.getId())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    public User save(User user) {
        if (user == null) {
            return null;
        }

        if (user.getId() == null) {
            user.setId(nextId++);
            users.add(user);
            return user;
        }

        for (int i = 0; i < users.size(); i++) {
            if (user.getId().equals(users.get(i).getId())) {
                users.set(i, user);
                return user;
            }
        }

        users.add(user);
        return user;
    }

    public boolean delete(Integer id) {
        if (id == null) {
            return false;
        }

        for (int i = 0; i < users.size(); i++) {
            if (id.equals(users.get(i).getId())) {
                users.remove(i);
                return true;
            }
        }

        return false;
    }

    @PreDestroy
    public void destroy() {
        users.clear();
        nextId = 1;
    }

}
