package com.example.config;

import com.example.repository.IPermissionRepository;
import com.example.repository.IRolePermissionRepository;
import com.example.repository.IRoleRepository;
import com.example.service.PermissionService;
import com.example.service.RolePermissionService;
import com.example.service.RoleService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.example.repository.IGameRepository;
import com.example.repository.impl.GameRepository1;
import com.example.service.GameService;

@Configuration
@ComponentScan(basePackages = "com.example")
@PropertySource("classpath:application.properties")
public class AppConfig {
    
    @Bean
    public GameService gameService(@Qualifier("gameRepository1") IGameRepository gameRepository) {
        return new GameService(gameRepository);
    }


    @Bean
    public RoleService roleService(@Qualifier("roleRepository1") IRoleRepository roleRepository) {
        return new RoleService(roleRepository);
    }

    @Bean
    public PermissionService permissionService(IPermissionRepository permissionRepository) {
        return new PermissionService(permissionRepository);
    }

    @Bean
    public RolePermissionService rolePermissionService(IRolePermissionRepository rolePermissionRepository) {
        return new RolePermissionService(rolePermissionRepository);
    }
}
