package com.group9.crazygolf.phys;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * EntityDynamic represents balls that can move and interact with the world and static objects.
 * The collision bounds of EntityDynamic are represented by a sphere
 */
public class EntityDynamic extends Entity {
    private Vector3 velocity = new Vector3();   //Velocity vector (m/s)
    private Vector3 forces = new Vector3();     //Forces scheduled to act nex frame ( N )
    private float mass = 5.0f;                  //Mass of the object (kg)
    private float radius = 2f;


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


    /**
     * Update the velocity based on forces
     *
     * @param dt time delta
     */
    @Override
    public void update(float dt) {
        super.update(dt);

        velocity.scl(0.95f);

        //Compute the velocity for this frame
        // dv = (F * dt) / m
        velocity.mulAdd(forces.cpy().scl(dt), 1f/mass);


        // Because the forces were scheduled for one frame only, clear them
        //forces.set(0,0,0);
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

        Ray ray = new Ray();
        Vector3 lastIntersection = new Vector3();
        float dst2LastIntersection = Float.MAX_VALUE;

        //Cast a ray from the position of the ball in the direction of the velocity vector
        for (int i = 0; i < target.getTriangleCount(); i++) {
            ray.set(position, velocity);
            if (target.intersectRayTriangle(ray, i, lastIntersection)) {
                //If this intersection is closer, save it.
                dst2LastIntersection = lastIntersection.dst2(position);
                if (dst2LastIntersection < dst2ClosestIntersection) {
                    closestIntersection = lastIntersection;
                    dst2ClosestIntersection = dst2LastIntersection;
                    closestTriangle = i;
                }
            }
        }

        //Collision detected
        if (closestTriangle >= 0) {
            float len2Velocity = velocity.cpy().scl(dt).len2();
            //Make sure that the distance to the intersection is less than the delta-time scaled velocity vector length
            if (len2Velocity - dst2ClosestIntersection + radius * radius >= 0) {
                return new CollisionEvent((float) Math.sqrt(len2Velocity / dst2ClosestIntersection), this, target, target.getVertexNormal(closestTriangle * 3).scl(-1));
            }
        }

        return null;
    }


    public void resetForces(){
        forces.set(0,0,0);
    }
}
