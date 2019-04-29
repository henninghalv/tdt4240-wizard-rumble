package com.progark.group2.wizardrumble.entities.spells;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.progark.group2.wizardrumble.tools.SoundManager;
import com.progark.group2.wizardrumble.tools.SoundType;

public class FireBall extends Spell {
    public final static Texture texture = new Texture("fireball_small.png");

    public FireBall(int spellOwnerID, Vector2 spawnPoint, float rotation, Vector2 velocity){
        super(spellOwnerID, spawnPoint, rotation, velocity, texture, 100, 60, "", 5, 1, SpellType.FIREBALL);
    }

    @Override
    public void playSound(float volume){
        System.out.println("Playing Fireball sound...");
        SoundManager.getInstance().playSound(SoundType.FIRE, volume);
    }

}
