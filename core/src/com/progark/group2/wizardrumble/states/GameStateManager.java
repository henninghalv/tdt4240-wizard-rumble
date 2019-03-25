package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Singelton that keeps track of the different states by using a private stack.
 */
public class GameStateManager {

    private static GameStateManager instance;

    private Stack<State> states;

    private GameStateManager(){
        this.states = new Stack<State>();
    }

    public static GameStateManager getInstance(){
        if(instance == null){
            return new GameStateManager();
        }
        else{
            return instance;
        }
    }

    public void push(State state){
        this.states.push(state);
    }

    // Prints error message if stack is empty
    public void pop(){
        try {
            this.states.pop();
        } catch(EmptyStackException e){
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
