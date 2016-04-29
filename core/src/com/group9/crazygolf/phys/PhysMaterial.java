package com.group9.crazygolf.phys;

/**
 * PhysMaterial stores the data about the surfaces of Entities
 * friction is kind of the rough
 */
public class PhysMaterial {
    private float friction;
    private float restitution;

    /**
     * Creates a default material with pre-set values.
     */
    public PhysMaterial() {
        this(0.5f, 0.5f);
    }

    /**
     * Create a new physics material.
     *
     * @param friction    the friction coefficient.
     * @param restitution the restitution coefficient.
     */
    public PhysMaterial(float friction, float restitution) {
        this.friction = friction;
        this.restitution = restitution;
    }

    public static float combineFriction(PhysMaterial a, PhysMaterial b) {
        //Using Weighted sum, weigth formula: √2 * (1-x) + 1;
        float weigthA = 1.414f * (1 - a.friction) + 1;
        float weigthB = 1.414f * (1 - b.friction) + 1;

        return ((weigthA * a.friction) + (weigthB * b.friction)) / (weigthA + weigthB);
    }

    public static float combineRestitution(PhysMaterial a, PhysMaterial b) {
        //Using Weighted sum, weigth formula: √2 * |2x - 1| + 1
        float weigthA = 1.414f * Math.abs(2 * a.restitution - 1) + 1f;
        float weigthB = 1.414f * Math.abs(2 * b.restitution - 1) + 1f;

        return ((weigthA * a.restitution) + (weigthB * b.restitution)) / (weigthA + weigthB);
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getRestitution() {
        return restitution;
    }

    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }
}
