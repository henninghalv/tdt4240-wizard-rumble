package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Singelton that keeps track of the different states by using a private stack.
 */
public class GameStateManager {

    private static GameStateManager instance = null;

    //TODO Change from stack to some sort of list to enable game render while the in-game pause is active
    private Stack<State> states;

    private GameStateManager(){
        this.states = new Stack<State>();
    }

    public static GameStateManager getInstance(){
        if(instance == null){
            instance = new GameStateManager();
        }
        return instance;
    }

    public void push(State state){
        this.states.push(state);
    }

    /**
     * Prints error message if stack is empty. Also tries to activate
     * the previous state.
     */
    public void pop(){
        try {
            this.states.pop();
            this.states.peek().activate();
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

        try {
            this.states.pop();
        } catch(EmptyStackException e){
            System.out.println(e.getMessage());
        }

        this.states.push(state);
    }

    public void update(float dt){
        this.states.peek().update(dt);
    }

    public void render(SpriteBatch spriteBatch){
        this.states.peek().render(spriteBatch);
    }
}
