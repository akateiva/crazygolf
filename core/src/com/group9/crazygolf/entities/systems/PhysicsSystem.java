package com.group9.crazygolf.entities.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.group9.crazygolf.entities.components.MeshColliderComponent;
import com.group9.crazygolf.entities.components.PhysicsComponent;
import com.group9.crazygolf.entities.components.SphereColliderComponent;
import com.group9.crazygolf.entities.components.StateComponent;

/**
 * Created by akateiva on 08/05/16.
 */
public class PhysicsSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private ComponentMapper<StateComponent> stateMap = ComponentMapper.getFor(StateComponent.class);
    private ComponentMapper<PhysicsComponent> physicsMap = ComponentMapper.getFor(PhysicsComponent.class);
    private ComponentMapper<SphereColliderComponent> sphereColliderMap = ComponentMapper.getFor(SphereColliderComponent.class);
    private ComponentMapper<MeshColliderComponent> meshColliderMap = ComponentMapper.getFor(MeshColliderComponent.class);

    public PhysicsSystem() {

    }

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(StateComponent.class, PhysicsComponent.class).one(SphereColliderComponent.class, MeshColliderComponent.class).get());
    }

    /**
     * Look for collisions, move objects accordingly.
     *
     * @param deltaTime
     */

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            //Apply gravity
            stateMap.get(entity).momentum.add(new Vector3(0, -9.81f * stateMap.get(entity).mass * deltaTime, 0));
            stateMap.get(entity).update();

            //Update the position
            stateMap.get(entity).position.mulAdd(stateMap.get(entity).velocity, deltaTime);


        }
        search(deltaTime);
    }

    /**
     * Search for collisions
     * <p>
     * The only dynamic objects in the world are balls. The rest are static.
     * This means we can perform only ball-mesh and ball-ball checks and get away with it.
     *
     * @param deltaTime
     */
    private void search(float deltaTime) {
        //Store some of the temp variables here for perf reasons ( not even sure if this does shit at all )
        Ray ray = new Ray();
        Vector3 tempIntersection = new Vector3();
        Vector3 bestIntersection = new Vector3();
        float bestDist2 = Float.MAX_VALUE;
        int closestTriangle = Integer.MIN_VALUE;

        for (int i = 0; i < entities.size(); i++) {
            Entity a = entities.get(i);
            if (!sphereColliderMap.has(a)) //If the entity A does not have a sphere collider, we can avoid it, as it will definitely not be the cause of the collision.
                continue;

            for (int j = 0; j < entities.size(); j++) {
                Entity b = entities.get(j);
                if (a == b)
                    continue;

                if (sphereColliderMap.has(b)) { //BALL-BALL COLLISION CHECK

                } else if (meshColliderMap.has(b)) { //BALL-MESH COLLISION CHECK
                    StateComponent atransform = stateMap.get(a);
                    StateComponent btransform = stateMap.get(b);

                    PhysicsComponent aphys = physicsMap.get(a);
                    PhysicsComponent bphys = physicsMap.get(b);

                    SphereColliderComponent acol = sphereColliderMap.get(a);
                    MeshColliderComponent bcol = meshColliderMap.get(b);

                    ray.set(atransform.position, atransform.velocity);

                    for (int k = 0; k < meshColliderMap.get(b).vertPosition.length / 3; k++) { //Iterate every triangle of the mesh
                        //TODO: Implement btransform
                        boolean hit = Intersector.intersectRayTriangle(ray,
                                bcol.vertPosition[k * 3],
                                bcol.vertPosition[k * 3 + 1],
                                bcol.vertPosition[k * 3 + 2],
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
                    Vector3 surfaceNormal = bcol.vertNormal[closestTriangle * 3]; //normal of the surface that the ball impacted
                    float dv = ray.direction.len() * deltaTime; //change of velocity

                    if (bestDist - acol.radius < dv) { //Check if this collision will happen within this time step ( since rays are unlimited )
                        if (surfaceNormal.dot(ray.direction) > 0)
                            continue;

                        //System.out.printf("Distance: %s m, DT: %s s, Velocity: %s m/s, acol.radius: %s m, Best int %s, Penetration : %s m, Seperation force: %s N\n", bestDist, deltaTime, ray.direction.len(), acol.radius, bestIntersection, "?", "?");

                        //Combine the coefficients of restitution of both Entities materials
                        float restitution = combineRestitution(aphys.restitution, bphys.restitution);

                        //Calculate the impulse of the impact
                        float impulse = -(1 + restitution) * stateMap.get(a).momentum.dot(surfaceNormal);

                        //Calculate the time of impact
                        float toi = ((bestDist - acol.radius) / dv) * deltaTime;

                        //Move the object to the position of impact
                        stateMap.get(a).position.mulAdd(stateMap.get(a).velocity, toi);

                        //Applying the impulse ( which is a vector along the surface normal)
                        stateMap.get(a).momentum.mulAdd(surfaceNormal, impulse);

                    }
                }
            }
        }
    }

    private float combineFriction(float a, float b) {
        //Using Weighted sum, weigth formula: √2 * (1-x) + 1;
        float weigthA = 1.414f * (1 - a) + 1;
        float weigthB = 1.414f * (1 - b) + 1;

        return ((weigthA * a) + (weigthB * b)) / (weigthA + weigthB);
    }

    private float combineRestitution(float a, float b) {
        //Using Weighted sum, weigth formula: √2 * |2x - 1| + 1
        float weigthA = 1.414f * Math.abs(2 * a - 1) + 1f;
        float weigthB = 1.414f * Math.abs(2 * b - 1) + 1f;

        return ((weigthA * a) + (weigthB * b)) / (weigthA + weigthB);
    }

    private class CollisionEvent {

    }


}
