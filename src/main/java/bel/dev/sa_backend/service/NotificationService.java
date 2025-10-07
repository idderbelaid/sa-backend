package bel.dev.sa_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import bel.dev.sa_backend.entities.Validation;

@Service
public class NotificationService {

    @Autowired
    JavaMailSender mailSender;

    
    public void envoyer(Validation validation) {
       SimpleMailMessage message = new SimpleMailMessage();
       message.setFrom("iderbel@yahoo.com");
       message.setTo(validation.getUtilisateur().getEmail());
       message.setSubject("Votre code de validation");
       String text = "Bonjour "+validation.getUtilisateur().getPrenom()+",\n\n";
       text += "Voici votre code de validation : "+validation.getCode()+"\n\n"; 
       text += "Ce code est valide pendant 10 minutes.\n\n";
       text += "Cordialement,\nL'équipe SA Backend.";
       message.setText(text);
       mailSender.send(message); 
    }


}
