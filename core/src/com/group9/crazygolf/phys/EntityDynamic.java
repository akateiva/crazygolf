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
    public CollisionEvent check(EntityStatic target, float dt) {
        //Store the closestIntersection vector and the triangle which caused this
        Vector3 closestIntersection = null;

        //A triangle index cannot be -1, therefore by the end of the iteration this variable remains -1, no intersections were detected
        int closestTriangle = -1;


        //Temporary stores for iteration
        Ray ray = new Ray();
        Vector3 lastIntersection = new Vector3();


        /*
        For every triangle in the target mesh, cast a ray from this object's position in the direction of the velocity vector
            If the ray intersected a triangle, the intersection point is saved in lastIntersection
                If the distance from our position towards lastIntersection is greater than the distance to closestIntersection
                    lastIntersection becomes the closestIntersection
        */

        //TODO: Add a check to see whether the intersection was detected behind the origin of the ray, or in front
        //TODO: Trace ray not from the position vector of the ball, but from the position vector + inverted triangle normal
        for (int i = 0; i < target.getTriangleCount(); i++) {
            if (target.intersectRayTriangle(ray.set(this.position, this.velocity), i, lastIntersection))
                if (closestIntersection == null || lastIntersection.dst2(this.position) < closestIntersection.dst2(this.position)) {
                    closestIntersection = lastIntersection;
                    closestTriangle = i;
                }
        }


        /*
        Check if there was an intersection
            If there was, we have to make sure that it will happen within this frame ( since a ray is not limited by the velocity's length. Ray's length is infinite )
         */


        //The length's/distance lengths are squared, in order to avoid computing square roots
        if (closestTriangle > -1) {
            float velocityDistance2 = velocity.cpy().scl(dt).len2();
            float intersectionDistance2 = position.dst2(closestIntersection);

            if (velocityDistance2 - intersectionDistance2 + radius * radius >= 0) {
                return new CollisionEvent((float) Math.sqrt(velocityDistance2 / intersectionDistance2), this, target, target.getVertexNormal(closestTriangle * 3).scl(-1));
            }
        }

        return null;
    }

    //public CollisionEvent check(EntityDynamic target){
    //  return null;
    //}

    public void resetForces(){
        forces.set(0,0,0);
    }
}
