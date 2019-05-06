package com.progark.group2.wizardrumble.controllers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public abstract class TouchPadMovementInput extends Touchpad implements MovementInput {
    private static Touchpad.TouchpadStyle touchpadStyle;
    private static Skin touchpadSkin;
    private static Drawable touchBackground;
    private static Drawable touchKnob;

    // Adjust diameter to change size of joystick
    public static int diameter=200;

    public TouchPadMovementInput() {
        super(10, getTouchpadStyle());
        setBounds(15, 15, diameter, diameter);
        setPosition(15, 15);
        touchKnob.setMinWidth(diameter/3);
        touchKnob.setMinHeight(diameter/3);
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
    public Vector2 getMovementDirection() {
        return new Vector2(getKnobPercentX(), getKnobPercentY());
    }
}
