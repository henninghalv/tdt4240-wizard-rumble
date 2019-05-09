package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.EmptyStackException;
import java.util.LinkedList;


/**
 * Singelton that keeps track of the different states by using a private stack.
 */
public class GameStateManager {

    private static GameStateManager instance = null;

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

    // Deny pushing of states that are already at the top of the stack
    public void push(State state){
        if(this.states.peekFirst() == null || !(state.getClass() == this.states.peekFirst().getClass())){
            System.out.println("Added " + state.getClass());
            this.states.addFirst(state);
            this.states.peek().activate();
        }
    }

    /**
     * Prints error message if stack is empty. Also tries to activate
     * the previous state.
     */
    public void pop(){
        try {
            this.states.removeFirst();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        this.states.peek().activate();
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
        state.activate();
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
