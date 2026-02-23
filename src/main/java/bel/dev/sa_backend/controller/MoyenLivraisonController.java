package bel.dev.sa_backend.controller;

import org.springframework.web.bind.annotation.*;

import bel.dev.sa_backend.entities.MoyenLivraison;
import bel.dev.sa_backend.service.MoyenLivraisonService;

import java.util.List;

@RestController
@RequestMapping("/api/livraisons")
public class MoyenLivraisonController {

    private final MoyenLivraisonService service;

    public MoyenLivraisonController(MoyenLivraisonService service) {
        this.service = service;
    }

    @GetMapping
    public List<MoyenLivraison> getAll() {
        return service.findAll();
    }

    @PostMapping
    public MoyenLivraison create(@RequestBody MoyenLivraison m) {
        return service.save(m);
    }

    @GetMapping("/{id}")
    public MoyenLivraison getById(@PathVariable String id) {
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}