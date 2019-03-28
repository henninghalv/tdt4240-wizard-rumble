package com.progark.group2.wizardrumble.entities;

public abstract class Spell extends Entity{
    protected int damage;
    protected float speed;
    protected String statusEffect;
    protected int cooldown;
    protected int castTime;
    protected String name;

    public String getName() {
        return name;
    }
}
