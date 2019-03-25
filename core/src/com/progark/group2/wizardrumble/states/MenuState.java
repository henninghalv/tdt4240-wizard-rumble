package com.progark.group2.wizardrumble.states;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;


/**
 * An abstract menu state that takes care of loading the image and style of the buttons
 */
abstract class MenuState extends State{

    private Label.LabelStyle labelStyle;
    private Texture buttonTexture;

    MenuState(GameStateManager gameStateManager){
        super(gameStateManager);
        initializeVariables();
    }

    /**
     * The method used for creating a button with a desired text on it
     *
     * @param buttonText    The desired text
     * @return  a stack consisting of a button image and a text overlay
     */
    protected Stack menuButton(String buttonText){
        Label label = new Label(buttonText, labelStyle);
        label.setAlignment(Align.center);

        Image buttonImage = new Image(this.buttonTexture);

        Stack button = new Stack();
        button.add(buttonImage);
        button.add(label);

        return button;
    }

    /**
     * Initialize button and text styling
     */
    protected void initializeVariables(){
        // Button styling
        this.buttonTexture = new Texture("UI/blue_button00.png");

        // Button text styling
        BitmapFont font = new BitmapFont();
        font.setColor(Color.BLACK);
        this.labelStyle = new Label.LabelStyle(font, font.getColor());
    }
}
