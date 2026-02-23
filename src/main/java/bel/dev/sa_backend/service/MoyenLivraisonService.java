package bel.dev.sa_backend.service;


import org.springframework.stereotype.Service;

import bel.dev.sa_backend.entities.MoyenLivraison;
import bel.dev.sa_backend.repository.MoyenLivraisonRepository;

import java.util.List;

@Service
public class MoyenLivraisonService {

    private final MoyenLivraisonRepository repository;

    public MoyenLivraisonService(MoyenLivraisonRepository repository) {
        this.repository = repository;
    }

    public List<MoyenLivraison> findAll() {
        return repository.findAll();
    }

    public MoyenLivraison save(MoyenLivraison m) {
        return repository.save(m);
    }

    public MoyenLivraison findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moyen de livraison introuvable"));
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}