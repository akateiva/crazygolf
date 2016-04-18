package com.group9.crazygolf.phys;


import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by akateiva on 18/04/16.
 */
public class Entity {

    enum COLLISION_BOUNDS {
        NO_BOUNDS,
        SPHERE_BOUNDS,
        MESH_BOUNDS
    }

    private Vector3 position = new Vector3();   //Position vector (m)
    private Vector3 velocity = new Vector3();   //Velocity vector (m/s)
    private Vector3 forces = new Vector3();     //Forces scheduled to act nex frame ( N )
    private float mass = 5.0f;                  //Mass of the object (kg)



    private ModelInstance modelInstance;

    public Entity( ModelInstance modelInstance){
        this.modelInstance = modelInstance;
    }

    /**
     * Apply a force on this object for one frame.
     * @param force the force in N
     */
    public void applyForce(Vector3 force){
        this.forces.add(force);
    }

    /**
     * Apply updates on the entity
     * @param dt
     */
    public void update(float dt){
        //Compute the velocity for this frame
        // dv = (F * dt) / m
        velocity.mulAdd(forces.scl(dt), 1f/mass);

        // Because the forces were scheduled for one frame only, clear them
        forces.set(0,0,0);


        //Add the velocity to the position vector
        this.position.mulAdd(velocity, dt);
        //Update the model instance to reflect the new position
        modelInstance.transform.translate(position);

    }

    /**
     * @return the position of the entity
     */
    public Vector3 getPosition() {
        return position;
    }

    /**
     * @param position the position of the entity
     */
    public void setPosition(Vector3 position) {
        this.position = position;
        this.modelInstance.transform.translate(position);
    }

    /**
     * @return modelInstance the model instance attached to this entity
     */
    public ModelInstance getModelInstance() {
        return modelInstance;
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
}
