package bel.dev.sa_backend.controller.requestDTO;

import bel.dev.sa_backend.Enums.CommandeStatus;

// DTO d'entrée
public class UpdateStatusRequest {
    private CommandeStatus status;

    public CommandeStatus getStatus() { return status; }
    public void setStatus(CommandeStatus status) { this.status = status; }
}

