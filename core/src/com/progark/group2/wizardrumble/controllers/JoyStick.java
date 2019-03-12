package com.progark.group2.wizardrumble.controllers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;


public class JoyStick extends Touchpad {

    private static Touchpad.TouchpadStyle touchpadStyle;
    private static Skin touchpadSkin;
    private static Drawable touchBackground;
    private static Drawable touchKnob;
    public static int diameter;

    public JoyStick(float x, float y) {

        super(10, getTouchpadStyle());

        //TODO: set knob size based on diameter
        // Adjust diameter to change size of joysticks
        diameter=200;

        setBounds(15, 15, diameter, diameter);
        setPosition(x,y);

    }

    private static Touchpad.TouchpadStyle getTouchpadStyle() {

        touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground", new Texture("touchBackground.png"));

        touchpadSkin.add("touchKnob", new Texture("touchKnob.png"));

        touchpadStyle = new Touchpad.TouchpadStyle();

        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");

        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;
        return touchpadStyle;
    }
}
