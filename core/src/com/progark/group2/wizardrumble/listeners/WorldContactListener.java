package com.progark.group2.wizardrumble.listeners;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.progark.group2.wizardrumble.entities.Entity;
import com.progark.group2.wizardrumble.entities.spells.Spell;

import java.io.IOException;

public class WorldContactListener implements ContactListener {
    // Has control of all collisions.

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();


        // Checks if one and only one fixture is a spell
        if (!(fixA.getUserData().getClass().getSuperclass().equals(Spell.class) && fixB.getUserData().getClass().getSuperclass().equals(Spell.class)) && (fixA.getUserData().getClass().getSuperclass().equals(Spell.class) || fixB.getUserData().getClass().getSuperclass().equals(Spell.class))){
            Fixture spell = fixA.getUserData() == "spell" ? fixA : fixB;
            Fixture object = spell == fixA ? fixB : fixA;


            if(object.getUserData() != null && Entity.class.isAssignableFrom(object.getUserData().getClass())){
                Spell spellObject = ((Spell) spell.getUserData());
                ((Entity) object.getUserData()).onCollideWithSpell(spellObject.getDamage());
                try {
                    spellObject.destroySpell();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
