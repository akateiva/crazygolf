package com.group9.crazygolf.ai;

import com.badlogic.gdx.math.Vector3;

/**
 * crazygolf
 * 2016
 *
 * Aleksas Kateiva
 * Eric Chang
 * Adeline Mekic
 * Florian Kok
 * Roger Sijben
 */
public class Shot{

    public Shot(Vector3 direction, float power) {
        this.direction = direction;
        this.power = power;
    }

    Vector3 direction;
    float power;

    public Vector3 getDirection() {
        return direction;
    }

    public void setDirection(Vector3 direction) {
        this.direction = direction;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }
}