package com.progark.group2.wizardrumble.network.responses;

public class ServerSuccessResponse extends Response {
    private String successMessage;

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
}

