package com.progark.group2.wizardrumble.states.ingamestate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.progark.group2.wizardrumble.Application;
import com.progark.group2.wizardrumble.entities.Wizard;

public class HealthBar extends Actor {
    private final int borderThickness = 2;
    private final int barWidthMultiplier = 2;
    private final int height = 25;
    private int width;
    private int totalBarWidth;
    private Texture texture;

    public HealthBar(){
        width = Wizard.DEFAULT_HEALTH * barWidthMultiplier;
        totalBarWidth = Wizard.DEFAULT_HEALTH * barWidthMultiplier;
        createTexture(width, Color.GREEN);
    }

    private void createTexture(int width, Color color){
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(Color.BLACK);
        pixmap.fillRectangle(0, 0, totalBarWidth, height);
        pixmap.setColor(color);
        pixmap.fillRectangle(borderThickness, borderThickness, width-(borderThickness*2),height-(borderThickness*2));
        texture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void updateHealth(int currentHealth){
        width = currentHealth / Wizard.DEFAULT_HEALTH * totalBarWidth;
        if(currentHealth > Wizard.DEFAULT_HEALTH / 2){
            createTexture(width, Color.GREEN);
        }
        else if(currentHealth <= Wizard.DEFAULT_HEALTH / 2){
            createTexture(width,  Color.ORANGE);
        }
        else if(currentHealth <= Wizard.DEFAULT_HEALTH / 4){
            createTexture(width,  Color.RED);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(texture, 50, Application.HEIGHT - 50, texture.getWidth(), texture.getHeight());
    }


}
