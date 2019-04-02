package com.progark.group2.wizardrumble.states;

public abstract class SettingsState extends State {

    public SettingsState(GameStateManager gameStateManager){
        super(gameStateManager);
    }

    public abstract void applyChanges();

}
