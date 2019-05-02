package com.progark.group2.wizardrumble.entities.spells;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class FireBall extends Spell {
    public final static Texture texture = new Texture("fireball_small.png");
    public final static long cooldown = 3000;

    public FireBall(int spellOwnerID, Vector2 spawnPoint, float rotation, Vector2 velocity){
        super(spellOwnerID, spawnPoint, rotation, velocity,  new Vector2(texture.getWidth()*0.8f,texture.getHeight()*0.8f), texture,100, 60, cooldown, 5, 1, SpellType.FIREBALL);
    }

    @Override
    public void cast(ArrayList<Spell> spells, NetworkController network) {
        spells.add(this); // Add to list of casted spells
        network.castSpell(this);
    }


    @Override
    public void playSound(float volume){
        SoundManager.getInstance().playSound(SoundType.FIRE, volume);
    }
}
