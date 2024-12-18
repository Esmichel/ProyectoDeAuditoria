package com.auditoria.proyecto_ctf.infraestructure.db.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.auditoria.proyecto_ctf.domain.entities.users.Role;
import com.auditoria.proyecto_ctf.domain.entities.users.User;

import java.util.Optional;



@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    public Optional<User> findByName(String name);

}


