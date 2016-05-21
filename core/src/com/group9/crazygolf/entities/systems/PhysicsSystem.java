package com.group9.crazygolf.entities.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.group9.crazygolf.entities.components.*;

/**
 * Created by akateiva on 08/05/16.
 */
public class PhysicsSystem extends EntitySystem {

    final float gravity = -9.81f;           //The acceleration of gravity
    final float stepSize = 1f / 200;    //Timestep of physics simulation (1second/60frames = 60 fps)
    private ImmutableArray<Entity> entities;
    private ComponentMapper<StateComponent> stateMap = ComponentMapper.getFor(StateComponent.class);
    private ComponentMapper<PhysicsComponent> physicsMap = ComponentMapper.getFor(PhysicsComponent.class);
    private ComponentMapper<SphereColliderComponent> sphereColliderMap = ComponentMapper.getFor(SphereColliderComponent.class);
    private ComponentMapper<MeshColliderComponent> meshColliderMap = ComponentMapper.getFor(MeshColliderComponent.class);
    private float timeAccumulator = 0f;


    public PhysicsSystem() {

    }

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(StateComponent.class, PhysicsComponent.class, VisibleComponent.class).one(SphereColliderComponent.class, MeshColliderComponent.class).get());
    }

    /**
     * Look for collisions, move objects accordingly.
     *
     * @param deltaTime
     */

    public void update(float deltaTime) {
        //timeAccumulator += deltaTime;

        //for (int step = (int) (timeAccumulator / stepSize); step > 0; step--) {
        //float stepTime = stepSize;
        //timeAccumulator -= stepSize;
        float stepTime = deltaTime;

            //Apply gravity momentum to all entities
            for (int i = 0; i < entities.size(); i++) {
                Entity ent = entities.get(i);
                stateMap.get(ent).momentum.add(new Vector3(0, gravity * stateMap.get(ent).mass * stepTime, 0));
                stateMap.get(ent).update();
            }

            while (stepTime > 0) {
                CollisionEvent bestEvent = search(stepTime);

                //There are no collisions in the next deltaTime seconds, therefore just integrate the positions
                if (bestEvent == null) {
                    integrate(stepTime);
                    stepTime = 0;
                } else {
                    float timeToBestEvent = solveEvent(bestEvent);

                    //Because solving an event updates the state of entities involved in the event by timeToBestEvent
                    //the state has to be updated by timeToBestEvent for all the other entities in the world
                    integrate(timeToBestEvent, bestEvent.a, bestEvent.b);

                    stepTime -= timeToBestEvent;

                }

            }
        //}

    }

    /**
     * Integrate the position of ALL entities by deltaTime
     *
     * @param deltaTime
     */

    private void integrate(float deltaTime) {
        //Integrate position with velocity ( Euler )
        for (int i = 0; i < entities.size(); i++) {
            Entity ent = entities.get(i);
            stateMap.get(ent).position.mulAdd(stateMap.get(ent).velocity, deltaTime);
            stateMap.get(ent).update();
        }
    }

    /**
     * Integrate the position of ALL EXCEPT SKIP1 AND SKIP2 entities by deltaTime
     *
     * @param deltaTime
     * @param skip1     an entity to not integrate
     * @param skip2     an entity to not integrate
     */

    private void integrate(float deltaTime, Entity skip1, Entity skip2) {
        //Integrate position with velocity ( Euler )
        for (int i = 0; i < entities.size(); i++) {
            Entity ent = entities.get(i);

            if (ent == skip1 || ent == skip2)
                continue;

            stateMap.get(ent).position.mulAdd(stateMap.get(ent).velocity, deltaTime);
            stateMap.get(ent).update();
        }
    }


    /**
     * Search for collisions
     * <p>
     * The only dynamic objects in the world are balls. The rest are static.
     * This means we can perform only ball-mesh and ball-ball checks and get away with it.
     *
     * @param deltaTime
     * @return the soonest CollisionEvent
     */
    private CollisionEvent search(float deltaTime) {
        //Store the soonest event
        CollisionEvent bestEvent = null;
        for (int i = 0; i < entities.size(); i++) {
            Entity a = entities.get(i);
            if (!sphereColliderMap.has(a)) //If the entity A does not have a sphere collider, we can avoid it, as it will definitely not be the cause of the collision.
                continue;

            for (int j = 0; j < entities.size(); j++) {
                //Store some of the temp variables here for perf reasons ( not even sure if this does shit at all )
                Ray ray = new Ray();
                Vector3 tempIntersection = new Vector3();
                Vector3 bestIntersection = new Vector3();
                float bestDist2 = Float.MAX_VALUE;
                int closestTriangle = Integer.MIN_VALUE;
                Entity b = entities.get(j);
                if (a == b)
                    continue;

                if (sphereColliderMap.has(b)) {
                    /*
                    ------------------------------------------------------------------------------------------
                    SPHERE-SPHERE COLLISION CHECKING BETWEEN A AND B
                    ------------------------------------------------------------------------------------------
                     */

                } else if (meshColliderMap.has(b)) {
                    /*
                    ------------------------------------------------------------------------------------------
                    SPHERE-MESH COLLISION CHECKING BETWEEN A AND B
                    ------------------------------------------------------------------------------------------
                     */

                    StateComponent atransform = stateMap.get(a);
                    StateComponent btransform = stateMap.get(b);

                    PhysicsComponent aphys = physicsMap.get(a);
                    PhysicsComponent bphys = physicsMap.get(b);

                    SphereColliderComponent acol = sphereColliderMap.get(a);
                    MeshColliderComponent bcol = meshColliderMap.get(b);

                    ray.set(atransform.position, atransform.velocity.cpy().add(btransform.velocity));

                    for (int k = 0; k < meshColliderMap.get(b).vertPosition.length / 3; k++) { //Iterate every triangle of the mesh
                        //TODO: Implement btransform
                        boolean hit = Intersector.intersectRayTriangle(ray,
                                bcol.vertPosition[k * 3].cpy().mul(btransform.transform),
                                bcol.vertPosition[k * 3 + 1].cpy().mul(btransform.transform),
                                bcol.vertPosition[k * 3 + 2].cpy().mul(btransform.transform),
                                tempIntersection);

                        if (hit) {
                            float dist2 = ray.origin.dst2(tempIntersection);
                            if (dist2 < bestDist2) {
                                bestIntersection = tempIntersection;
                                bestDist2 = dist2;
                                closestTriangle = k;
                            }
                        }
                    }

                    if (closestTriangle == Integer.MIN_VALUE)
                        continue; //No triangle is intercepted

                    float bestDist = (float) Math.sqrt(bestDist2); //distance to the closest intersection on the mesh
                    Vector3 surfaceNormal = bcol.vertNormal[closestTriangle * 3].cpy().mul(btransform.transform); //normal of the surface that the ball impacted
                    float dv = ray.direction.len() * deltaTime; //change of velocity

                    if (bestDist - acol.radius < dv) { //Check if this collision will happen within this time step ( since rays are unlimited )
                        if (surfaceNormal.dot(ray.direction) > 0)
                            continue;

                        //Calculate the time of impact
                        float toi = ((bestDist - acol.radius) / dv) * deltaTime;

                        CollisionEvent event = new CollisionEvent(a, b, toi, surfaceNormal);
                        if (bestEvent == null) {
                            bestEvent = event;
                        } else if (event.toi < bestEvent.toi) {
                            bestEvent = event;
                        }
                    }
                }
            }
        }
        return bestEvent;
    }

    /**
     * Solve a physics event.
     *
     * @param event the physics event to be solved
     * @return the time in seconds that the state of the simulation was advanced while solving
     */
    private float solveEvent(CollisionEvent event) {
        Entity a = event.a;
        Entity b = event.b;
        float toi = event.toi;

        //Combine the coefficients of restitution of both Entities materials
        float restitution = combineRestitution(physicsMap.get(a).restitution, physicsMap.get(b).restitution);

        //Calculate the impulse of the impact
        float impulse = -(1 + restitution) * stateMap.get(a).momentum.dot(event.hitNormal);

        //Move the object to the position of impact
        stateMap.get(a).position.mulAdd(stateMap.get(a).velocity, toi);

        //Applying the impulse ( which is a vector along the surface normal)
        stateMap.get(a).momentum.mulAdd(event.hitNormal, impulse);
        stateMap.get(a).momentum.scl(0.97f);
        stateMap.get(a).update();

        return toi;
    }

    /**
     * Combine the friction values of two materials into one coefficient of friction
     *
     * @param a float the friction of material a
     * @param b float the friction of material b
     * @return the combined friction
     */
    private float combineFriction(float a, float b) {
        //Using Weighted sum, weigth formula: √2 * (1-x) + 1;
        float weigthA = 1.414f * (1 - a) + 1;
        float weigthB = 1.414f * (1 - b) + 1;

        return ((weigthA * a) + (weigthB * b)) / (weigthA + weigthB);
    }

    /**
     * Combine the restitution values of two materials into one coefficient of restitution
     *
     * @param a float the restitution of material a
     * @param b float the restitution of material b
     * @return the combined restitution
     */
    private float combineRestitution(float a, float b) {
        //Using Weighted sum, weigth formula: √2 * |2x - 1| + 1
        float weigthA = 1.414f * Math.abs(2 * a - 1) + 1f;
        float weigthB = 1.414f * Math.abs(2 * b - 1) + 1f;

        return ((weigthA * a) + (weigthB * b)) / (weigthA + weigthB);
    }

    /**
     * A data class for storing the information about a collision event.
     */
    class CollisionEvent {
        public Entity a;           // Entity involved in collision event
        public Entity b;           // Second entity involved in collision event
        public float toi;          // time of impact in seconds
        public Vector3 hitNormal;  // the normal of the impact

        /**
         * @param a         an entity involved in the event
         * @param b         an entity involved in the event
         * @param toi       time to impact ( in seconds )
         * @param hitNormal the impact normal in relation to entity b
         */
        public CollisionEvent(Entity a, Entity b, float toi, Vector3 hitNormal) {
            this.a = a;
            this.b = b;
            this.toi = toi;
            this.hitNormal = hitNormal;
        }
    }

}
