package com.group9.crazygolf.ai;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by akateiva on 6/16/16.
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