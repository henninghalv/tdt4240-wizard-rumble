package com.progark.group2.wizardrumble.states;

<<<<<<< HEAD
=======

import com.badlogic.gdx.graphics.Color;
>>>>>>> a6062b1f77fc89646bfe8ba90b1a46b44b14619d
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.progark.group2.wizardrumble.states.resources.UIButton;


/**
 * An abstract menu state that takes care of loading the image and style of the buttons
 */
abstract class MenuState extends State{

    MenuState(GameStateManager gameStateManager){
        super(gameStateManager);
<<<<<<< HEAD
=======

        // Button styling
        this.buttonTexture = new Texture("UI/blue_button00.png");

        // Button text styling
        BitmapFont font = new BitmapFont();
        font.setColor(Color.BLACK);
        this.labelStyle = new Label.LabelStyle(font, font.getColor());
>>>>>>> a6062b1f77fc89646bfe8ba90b1a46b44b14619d
    }

    /**
     * The method used for creating a menu button
     * @return  a stack consisting of a button image and a text overlay
     */
<<<<<<< HEAD
    protected Stack menuButton(String text){
        return new UIButton(new Texture("UI/blue_button00.png"), text).getButton();
=======
    protected Stack menuButton(String buttonText){
        Label label = new Label(buttonText, labelStyle);
        label.setAlignment(Align.center);

        Image buttonImage = new Image(this.buttonTexture);

        Stack button = new Stack();
        button.add(buttonImage);
        button.add(label);

        return button;
>>>>>>> a6062b1f77fc89646bfe8ba90b1a46b44b14619d
    }
}
