package com.progark.group2.wizardrumble.controllers;

import com.badlogic.gdx.math.Vector2;

public class ControllerInputHandler {
    private Vector2 aimDirection;
    private Vector2 moveDirection;
    private int spellSelected;


    public Vector2 getMoveDirection(){
        return moveDirection;
    }

    public Vector2 getAimDirection(){
        return aimDirection;
    }

    public void updateSpell(int spell){

    }

}
