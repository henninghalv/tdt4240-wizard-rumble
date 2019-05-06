package com.progark.group2.wizardrumble.controllers;

public class TouchPadMovementInput1 extends TouchPadMovementInput {

    private static TouchPadMovementInput1 instance = null;

    private TouchPadMovementInput1() {
        super();
    }

    public static TouchPadMovementInput1 getInstance() {
        if (instance == null) {
            instance = new TouchPadMovementInput1();
        }
        return instance;
    }
}
