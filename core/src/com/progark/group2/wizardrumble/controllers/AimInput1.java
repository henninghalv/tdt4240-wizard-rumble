package com.progark.group2.wizardrumble.controllers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;


public class AimInput1 extends Touchpad {

    private static TouchpadStyle touchpadStyle;
    private static Skin touchpadSkin;
    private static Drawable touchBackground;
    private static Drawable touchKnob;
    // Adjust diameter to change size of joystick
    public static int diameter=200;

    public AimInput1(float x, float y) {

        super(10, getTouchpadStyle());

        setBounds(15, 15, diameter, diameter);
        setPosition(x, y);
        touchKnob.setMinWidth(diameter/3);
        touchKnob.setMinHeight(diameter/3);

    }

    private static TouchpadStyle getTouchpadStyle() {

        touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground", new Texture("touchBackground.png"));

        touchpadSkin.add("touchKnob", new Texture("touchKnob.png"));

        touchpadStyle = new TouchpadStyle();

        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");

        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;
        return touchpadStyle;
    }
}
