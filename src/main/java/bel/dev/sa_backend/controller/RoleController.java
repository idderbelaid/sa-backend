package bel.dev.sa_backend.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import bel.dev.sa_backend.dto.RoleDTO;
 
import bel.dev.sa_backend.entities.Role;
import bel.dev.sa_backend.service.RoleService;

@RestController
public class RoleController {

    private RoleService roleService;

    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }
    @PostMapping(path = "/creer", consumes="application/json")
    public ResponseEntity<RoleDTO> creerRole(@Valid @RequestBody Role role) {
        RoleDTO reponse = this.roleService.creer(role);
        return  ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }
}
