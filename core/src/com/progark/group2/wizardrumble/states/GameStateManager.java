package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.progark.group2.wizardrumble.network.NetworkController;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Singelton that keeps track of the different states by using a private stack.
 */
public class GameStateManager {

    private static GameStateManager instance = null;

    //TODO Change from stack to some sort of list to enable game render while the in-game pause is active
    private LinkedList<State> states;

    private GameStateManager(){
        this.states = new LinkedList<State>();
    }

    public static GameStateManager getInstance(){
        if(instance == null){
            instance = new GameStateManager();
        }
        return instance;
    }

    public void push(State state){
        this.states.addFirst(state);
    }

    /**
     * Prints error message if stack is empty. Also tries to activate
     * the previous state.
     */
    public void pop(){
        try {
            this.states.removeFirst();
            this.states.peek().activate();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Changes the top state by popping it, then pushing the new state.
     *
     * @param state The new state to be pushed
     */
    public void set(State state){

        try {
            this.states.removeFirst();
        } catch(EmptyStackException e){
            System.out.println(e.getMessage());
        }

        this.states.addFirst(state);
    }

    public void update(float dt){
        if(this.isOverlay(this.states.peek())){
            this.states.get(1).update(dt);
        }
        this.states.peek().update(dt);
    }

    public void render(SpriteBatch spriteBatch){
        if(this.isOverlay(this.states.peek())){
            this.states.get(1).render(spriteBatch);
        }
        this.states.peek().render(spriteBatch);
    }

    private boolean isOverlay(State state){
        return state instanceof InGameMenuState || state instanceof InGameSettings;
    }
}
