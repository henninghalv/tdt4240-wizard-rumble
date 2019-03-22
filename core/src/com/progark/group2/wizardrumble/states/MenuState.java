package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;

abstract class MenuState extends State{

    private Label.LabelStyle labelStyle;
    private Texture buttonTexture;

    MenuState(GameStateManager gameStateManager){
        super(gameStateManager);
        initializeVariables();
    }

    /**
     * Mek button
     *
     * @param buttonText
     * @return
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
     * Initialize stuff
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
