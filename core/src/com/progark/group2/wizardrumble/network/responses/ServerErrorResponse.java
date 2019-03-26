package com.progark.group2.wizardrumble.network.responses;

public class ServerErrorResponse extends Response {
    private String errorMsg;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
