package com.group9.crazygolf.phys;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * EntityDynamic represents balls that can move and interact with the world and static objects.
 * The collision bounds of EntityDynamic are represented by a sphere
 */
public class EntityDynamic extends Entity {
    private Vector3 velocity = new Vector3();   //Velocity vector (m/s)
    private Vector3 forces = new Vector3();     //Forces scheduled to act nex frame ( N )
    private float mass = 5.0f;                  //Mass of the object (kg)
    private float radius = 1.0f;


    public EntityDynamic(ModelInstance modelInstance) {
        super(modelInstance);
    }

    /**
     * Apply a force on this object for one frame.
     * @param force the force in N
     */
    public void applyForce(Vector3 force){
        this.forces.add(force);
    }

    /**
     * @return the velocity of the entity
     */
    public Vector3 getVelocity() {
        return velocity;
    }

    /**
     * You should use this method if you want to interfere with the velocity in some weird way, otherwise use {@link #applyForce(Vector3)}
     * @param velocity the mass of the entity
     */
    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    /**
     * @return the mass of the entity
     */
    public float getMass() {
        return mass;
    }

    /**
     * @param mass the mass of the entity
     */
    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }



    @Override
    public void update(float dt) {
        super.update(dt);

        //Compute the velocity for this frame
        // dv = (F * dt) / m
        velocity.mulAdd(forces.scl(dt), 1f/mass);

        // Because the forces were scheduled for one frame only, clear them
        forces.set(0,0,0);
    }


    public CollisionEvent check(EntityStatic target, float localdt){
        return null;
    }

    public CollisionEvent check(EntityDynamic target){
        return null;
    }

    public void resetForces(){
        forces.set(0,0,0);
    }
}
