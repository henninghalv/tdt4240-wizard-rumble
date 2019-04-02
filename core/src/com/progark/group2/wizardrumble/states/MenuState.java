package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.progark.group2.wizardrumble.states.resources.UIButton;


/**
 * An abstract menu state that takes care of loading the image and style of the buttons
 */
abstract class MenuState extends State{

    MenuState(GameStateManager gameStateManager){
        super(gameStateManager);
    }

    /**
     * The method used for creating a menu button
     * @return  a stack consisting of a button image and a text overlay
     */
    protected Stack menuButton(String text){
        return new UIButton(new Texture("UI/blue_button00.png"), text).getButton();
    }
}
