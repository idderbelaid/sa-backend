package bel.dev.sa_backend.service;

import org.springframework.stereotype.Service;

 
import bel.dev.sa_backend.dto.RoleDTO;
import bel.dev.sa_backend.entities.Role;

import bel.dev.sa_backend.mapper.RoleMapper;
import bel.dev.sa_backend.repository.RoleRepository;

@Service
public class RoleService {

    private RoleRepository roleRepository;
    private RoleMapper roleMapper;
    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
        this.roleMapper = new RoleMapper();
    }

    public RoleDTO creer(Role role){
       
        Role savedRole = roleRepository.save(role); // persisté en base
        return roleMapper.apply(savedRole);

    }

}
