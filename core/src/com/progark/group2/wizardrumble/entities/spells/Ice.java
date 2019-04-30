package com.progark.group2.wizardrumble.entities.spells;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.progark.group2.wizardrumble.network.NetworkController;

import java.util.ArrayList;

import static com.progark.group2.wizardrumble.Application.SCALE;

public class Ice extends Spell {

    public final static Texture texture = new Texture("fireball_small.png");
    private final float ANGLE_OFFSET = 25;
    private Vector2 pivot;

    public Ice(int spellOwnerID, Vector2 spawnPoint, float rotation, Vector2 velocity, Vector2 pivot){
        super(spellOwnerID, spawnPoint, rotation, velocity, new Vector2(texture.getWidth()*0.4f, texture.getHeight()*0.4f), texture,10, 60f, "", 3, 1);
        this.pivot = pivot;
    }

    public Ice(int spellOwnerID, Vector2 spawnPoint, float rotation, Vector2 velocity){
        super(spellOwnerID, spawnPoint, rotation, velocity, new Vector2(texture.getWidth()*0.4f, texture.getHeight()*0.4f), texture,10, 60f, "", 3, 1);
    }

    @Override
    public void cast(ArrayList<Spell> spells, NetworkController network) {
        Vector2 posTransposed = new Vector2(position.x - pivot.x, position.y - pivot.y);

        // Right Ice rotation calculation
        posTransposed.rotate(ANGLE_OFFSET);
        Ice rightIce = new Ice(
                spellOwnerID,
                new Vector2(pivot.x + posTransposed.x, pivot.y + posTransposed.y),
                rotation + ANGLE_OFFSET,
                new Vector2(velocity).rotate(ANGLE_OFFSET),
                pivot
        );

        // Left Ice rotation calculation
        posTransposed.rotate(-2*ANGLE_OFFSET);
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
}
