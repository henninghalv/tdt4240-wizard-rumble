package com.progark.group2.wizardrumble.controllers;

public class TouchPadAimInput1 extends TouchPadAimInput {

    private static TouchPadAimInput1 instance = null;

    private TouchPadAimInput1() {
        super();
    }

    public static TouchPadAimInput1 getInstance() {
        if (instance == null) {
            instance = new TouchPadAimInput1();
        }
        return instance;
    }
}
