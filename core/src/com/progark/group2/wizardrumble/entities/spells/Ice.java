package com.progark.group2.wizardrumble.entities.spells;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.progark.group2.wizardrumble.network.NetworkController;
import com.progark.group2.wizardrumble.tools.SoundManager;
import com.progark.group2.wizardrumble.tools.SoundType;

import java.util.ArrayList;


public class Ice extends Spell {

    public final static Texture texture = new Texture("fireball_small.png");
    private final float ANGLE_OFFSET = 25;
    private Vector2 pivot;

    public Ice(int spellOwnerID, Vector2 spawnPoint, float rotation, Vector2 velocity, Vector2 pivot){
        super(spellOwnerID, spawnPoint, rotation, velocity, new Vector2(texture.getWidth()*0.4f, texture.getHeight()*0.4f), texture,10, 60f, "", 3, 1, SpellType.ICE);
        this.pivot = pivot;
    }

    public Ice(int spellOwnerID, Vector2 spawnPoint, float rotation, Vector2 velocity){
        super(spellOwnerID, spawnPoint, rotation, velocity, new Vector2(texture.getWidth()*0.4f, texture.getHeight()*0.4f), texture,10, 60f, "", 3, 1, SpellType.ICE);
    }

    @Override
    public void cast(ArrayList<Spell> spells, NetworkController network) {
        //Vector2 posTransposed = new Vector2(position.x - pivot.x, position.y - pivot.y);
        Vector2 posTransposed = new Vector2(position.x - pivot.x, position.y - pivot.y).rotate(ANGLE_OFFSET);
        // Right Ice rotation calculation
        Ice rightIce = new Ice(
                spellOwnerID,
                new Vector2(pivot.x + posTransposed.x, pivot.y + posTransposed.y),
                rotation + ANGLE_OFFSET,
                new Vector2(velocity).rotate(ANGLE_OFFSET),
                pivot
        );

        // Left Ice rotation calculation
        posTransposed = new Vector2(position.x - pivot.x, position.y - pivot.y).rotate(-ANGLE_OFFSET);
        Ice leftIce = new Ice(
                spellOwnerID,
                new Vector2(pivot.x + posTransposed.x, pivot.y + posTransposed.y),
                rotation - ANGLE_OFFSET,
                new Vector2(velocity).rotate(-ANGLE_OFFSET),
                pivot
        );

        spells.add(leftIce);
        spells.add(this);
        spells.add(rightIce);
        network.castSpell(rightIce);
        network.castSpell(this);
        network.castSpell(leftIce);

    }

    @Override
    public void playSound(float volume) {
        System.out.println("Playing Ice sound...");
        SoundManager.getInstance().playSound(SoundType.ICE, volume);
    }

}
