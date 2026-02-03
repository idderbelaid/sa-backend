package bel.dev.sa_backend.controller;


import javax.validation.constraints.NotBlank;

public class MergeRequest {

    @NotBlank(message = "sessionId est obligatoire")
    private String sessionId;

    public MergeRequest() {}

    public MergeRequest(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
