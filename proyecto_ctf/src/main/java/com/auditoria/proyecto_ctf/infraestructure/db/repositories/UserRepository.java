package com.auditoria.proyecto_ctf.infraestructure.db.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.auditoria.proyecto_ctf.domain.entities.users.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{

    public Optional<User> findByUsername(String username);

}
