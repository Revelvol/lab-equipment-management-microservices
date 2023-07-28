package com.revelvol.JWT.repository;

import com.revelvol.JWT.model.Role;
import com.revelvol.JWT.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashSet;
import java.util.Optional;

public interface TestH2RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByName(String name);

    default Role getOrCreateByName(String name){
        Optional<Role> roleOptional = findByName(name);
        return roleOptional.orElseGet(()->{
            Role newRole = new Role(name, new HashSet<User>());
            return save(newRole);
        });
    }
}
