package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MainMenuState extends State {

    public void startGame(){

    }

    public void openSettings(){

    }

    public void openPlayerSettings(){

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {
        Texture img = new Texture("wizard.jpg");
        TextureRegion region = new TextureRegion(img);
        sb.begin();
        sb.draw(region, 200,200, 0,0, img.getWidth(),img.getHeight(),1,1,0);
        sb.end();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onBackButtonPress() {

    }
}
