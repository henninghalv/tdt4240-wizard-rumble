package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * Keeps track of the different states by using a private stack.
 */
public class GameStateManager {

    private Stack<State> states;

    public void GameStateManager(){
        this.states = new Stack<State>();
    }

    public void push(State state){
        this.states.push(state);
    }

    // Prints error message if stack is empty
    public void pop(){
        try {
            this.states.pop();
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
        // Use own pop method for error handling
        this.pop();
        this.states.push(state);
    }



    public void update(float dt){
        states.peek().update(dt);
    }

    public void render(SpriteBatch spriteBatch){
        states.peek().render(spriteBatch);
    }



}
