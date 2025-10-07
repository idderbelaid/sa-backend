package bel.dev.sa_backend.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Component;


import bel.dev.sa_backend.dto.RoleDTO;

import bel.dev.sa_backend.entities.Role;

@Component
public class RoleMapper implements Function<Role, RoleDTO>{

    @Override
    public RoleDTO apply(Role t) {
       return new RoleDTO(t.getId(), t.getLibelle());
    }

  
 

}
