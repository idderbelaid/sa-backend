package bel.dev.sa_backend.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.stripe.model.v2.core.Account.Defaults.Profile;

import bel.dev.sa_backend.dto.AddressDTO;
import bel.dev.sa_backend.dto.ProfileDTO;
import bel.dev.sa_backend.dto.UtilisateurResponseDTO;
import bel.dev.sa_backend.entities.Address;
import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.mapper.AddressMapper;
import bel.dev.sa_backend.mapper.UtilisateurMapper;
import bel.dev.sa_backend.repository.AddressRepository;
import bel.dev.sa_backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AddressService {

    private final UtilisateurRepository utilisateurRepository;
    private final AddressRepository addressRepository;



    public ProfileDTO findMyAddress(String userId){
        
        Utilisateur user = this.utilisateurRepository.findByEmail(userId).
            orElseThrow(()-> new UsernameNotFoundException("Aucun utilisateur à cet identifiant"));
        UtilisateurResponseDTO util = UtilisateurMapper.toResponseDTO(user);
        ProfileDTO profile = new ProfileDTO(util, null);
        Address adr = user.getAddresses().stream()
            .filter(Address::isDefault)
            .findFirst()
            .orElse(null);
        if(adr!= null){
            AddressDTO dto = new AddressDTO(
                adr.getId(),
                adr.getNumeroEtvoie(),
                adr.getComplementAddress(),
                adr.getVille(),
                adr.getCodePostal(),
                adr.getPays()
            );
            profile.setAddress(dto);
            
        }
        return profile;
    }



    public AddressDTO getAddressById(String id) {
        return this.addressRepository.findById(id)
            .map(adr -> new AddressDTO(
                adr.getId(),
                adr.getNumeroEtvoie(),
                adr.getComplementAddress(),
                adr.getVille(),
                adr.getCodePostal(),
                adr.getPays()
            ))
            .orElseThrow(() -> new RuntimeException("Adresse non trouvée avec l'ID : " + id));
    }



    public List<AddressDTO> getAllAddresses(String userId) {
        Utilisateur user = this.utilisateurRepository.findByEmail(userId)
            .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur à cet identifiant"));

        return user.getAddresses().stream()
            .map(adr -> new AddressDTO(
                adr.getId(),
                adr.getNumeroEtvoie(),
                adr.getComplementAddress(),
                adr.getVille(),
                adr.getCodePostal(),
                adr.getPays()
            ))
            .toList();
    }



    public AddressDTO updateAddress(String userId, String id, AddressDTO addressDTO) {
        Utilisateur user = this.utilisateurRepository.findByEmail(userId)
            .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur à cet identifiant"));
        
        Address adr = this.addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Adresse non trouvée avec l'ID : " + id));
        if (!user.getAddresses().contains(adr)) {
            throw new RuntimeException("Cette adresse n'appartient pas à l'utilisateur");
        }
        if(adr != null){
            adr.setNumeroEtvoie(addressDTO.numeroEtvoie());
            adr.setComplementAddress(addressDTO.complementAddress());
            adr.setVille(addressDTO.ville());
            adr.setCodePostal(addressDTO.codePostal());
            adr.setPays(addressDTO.pays());
            this.addressRepository.save(adr);
            return addressDTO;
        }
        return null;
    }



    public void deleteAddress(String userId, String id) {
         Utilisateur user = this.utilisateurRepository.findByEmail(userId)
            .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur à cet identifiant"));
        Address adr = this.addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Adresse non trouvée avec l'ID : " + id));
        if (!user.getAddresses().contains(adr)) {
            throw new RuntimeException("Cette adresse n'appartient pas à l'utilisateur");
        }
        if(adr != null){
            this.addressRepository.delete(adr);
        }

    }



    public AddressDTO createAddress(String userId, AddressDTO addressDTO) {
        Utilisateur user = this.utilisateurRepository.findByEmail(userId)
            .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur à cet identifiant"));
        
        Address adr = new Address();
        adr.setNumeroEtvoie(addressDTO.numeroEtvoie());
        adr.setComplementAddress(addressDTO.complementAddress());
        adr.setVille(addressDTO.ville());
        adr.setCodePostal(addressDTO.codePostal());
        adr.setPays(addressDTO.pays());
        adr.setUser(user);
        this.addressRepository.save(adr);
        return AddressMapper.toDTOAddress(adr);
    }
}
