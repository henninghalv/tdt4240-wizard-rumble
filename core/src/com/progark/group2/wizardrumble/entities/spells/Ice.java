package com.progark.group2.wizardrumble.entities.spells;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.progark.group2.wizardrumble.entities.Spell;

public class Ice extends Spell {

    private Texture fireBallSprite;
    private TextureRegion region;
    private float scale;
    // velocity
    // rotation
    // position



    public Ice(Vector2 spawnPoint, float rotation, Vector2 velocity){
        this.velocity = velocity; // Use speed in spell abstract class in addition to this.
        this.rotation = rotation;
        this.position = spawnPoint;  // Need to offset this by size of wizard sprite
        fireBallSprite = new Texture("fireball.png");
        region = new TextureRegion(fireBallSprite);
        scale = 0.02f;
        name="Ice";
    }

    // There's a difference in screen coordinates and global coordinates. Where you draw each sprite
    // needs to be "local" while the global coordinates is useful for
    public Vector2 getGlobalPosition(){
        // TODO return global position, if that's even needed
        return null;
    }

    private void updatePosition(){
        position.x += velocity.x;
        position.y += velocity.y;
    }

    @Override
    public void onCollision() {

    }

    @Override
    public void update() {
        updatePosition();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.draw(region, getPosition().x, getPosition().y,
                fireBallSprite.getWidth()/2f,
                fireBallSprite.getHeight()/2f,
                fireBallSprite.getWidth(),fireBallSprite.getHeight(),scale,scale,rotation); // 0.2 is a random number. Tweak as necessary
    }

    @Override
    public void dispose() {

    }
}
