package com.group9.crazygolf.phys;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * EntityBall represents balls that can move and interact with the world and static objects.
 * The collision bounds of EntityBall are represented by a sphere
 */
public class EntityBall extends Entity {
    private Vector3 velocity = new Vector3();   //Velocity vector (m/s)
    private Vector3 forces = new Vector3();     //Forces scheduled to act nex frame ( N )
    private float mass = 5.0f;                  //Mass of the object (kg)
    private float radius = 0.5f;


    public EntityBall(ModelInstance modelInstance) {
        super(modelInstance);
    }

    public EntityBall(ModelInstance modelInstance, PhysMaterial physMaterial) {
        super(modelInstance, physMaterial);
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


    /**
     * Update the velocity based on forces
     *
     * @param dt time delta
     */
    @Override
    public void update(float dt) {
        super.update(dt);

        getVelocity().scl(0.95f);

        //Compute the velocity for this frame
        // dv = (F * dt) / m
        getVelocity().mulAdd(forces.cpy().scl(dt), 1f / getMass());
    }

    /**
     * This method is responsible only for collision detection and is not involved in collision response,
     * therefore it should not modify any of the entities values
     *
     * @param target the EntityStatic against a check for collisions will happen
     * @param dt     how far into the future should be checked ( in seconds )
     * @return CollisionEvent if a collision was detected or null if not
     */
    CollisionEvent check(EntityStatic target, float dt) {
        //Save the closest intersection
        Vector3 closestIntersection = null;
        int closestTriangle = Integer.MIN_VALUE;
        float dst2ClosestIntersection = Float.MAX_VALUE;
        //Vector3 positionHit = this.getPosition();

        Ray ray = new Ray();
        Vector3 lastIntersection = new Vector3();
        float dst2LastIntersection = Float.MAX_VALUE;

        //Cast a ray from the position of the ball in the direction of the velocity vector
        for (int i = 0; i < target.getTriangleCount(); i++) {
            ray.set(getPosition(), getVelocity());
            if (target.intersectRayTriangle(ray, i, lastIntersection)) {
                //If this intersection is closer, save it.
                dst2LastIntersection = lastIntersection.dst2(getPosition());
                if (dst2LastIntersection < dst2ClosestIntersection) {
                    closestIntersection = lastIntersection;
                    dst2ClosestIntersection = dst2LastIntersection;
                    closestTriangle = i;
                }
            }
        }

        //Collision detected
        if (closestTriangle >= 0) {
            float len2Velocity = getVelocity().cpy().scl(dt).len2();
            //Make sure that the distance to the intersection is less than the delta-time scaled velocity vector length
            if (len2Velocity - dst2ClosestIntersection + getRadius() * getRadius() >= 0) {
                //Since a collision happens only so often, we can afford to use square root here
                return new CollisionEvent((float) Math.sqrt(dst2ClosestIntersection) - getRadius() / (float) Math.sqrt(len2Velocity), this, target, target.getVertexNormal(closestTriangle * 3), closestIntersection);
            }
        }

        return null;
    }


    public void resetForces(){
        forces.set(0,0,0);
    }
}
