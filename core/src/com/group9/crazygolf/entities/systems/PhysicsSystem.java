package com.group9.crazygolf.entities.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.group9.crazygolf.entities.components.*;

import java.util.ArrayList;

/**
 * Created by akateiva on 08/05/16.
 */
public class PhysicsSystem extends EntitySystem {

    final float gravity = -9.81f;           //The acceleration of gravity
    final float stepSize = 1f / 60f;    //Timestep of physics simulation (1second/60frames = 60 fps)
    final float timeDillation = 1f;   //1 means actual equ-time, less means slowed down
    private ImmutableArray<Entity> entities;
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);
    private ComponentMapper<PhysicsComponent> pm = ComponentMapper.getFor(PhysicsComponent.class);
    private ComponentMapper<SphereColliderComponent> scm = ComponentMapper.getFor(SphereColliderComponent.class);
    private ComponentMapper<MeshColliderComponent> mcm = ComponentMapper.getFor(MeshColliderComponent.class);
    private EventQueue events = new EventQueue();

    private float timeAccumulator = 0f;

    public ImmutableArray<Entity> getEntities() {
        return entities;
    }

    public PhysicsSystem() {

    }

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(StateComponent.class, PhysicsComponent.class, VisibleComponent.class).one(SphereColliderComponent.class, MeshColliderComponent.class).get());
    }

    public void saveStates(){
        for(int i = 0; i < entities.size(); i++){
            sm.get(entities.get(i)).save();
        }
    }

    public void restoreStates(){
        for(int i = 0; i < entities.size(); i++){
            sm.get(entities.get(i)).restore();
        }
    }

    /**
     * Update
     *
     * This method uses the time accumulator in order to fix the timestep. Having a defined and enforced time step ( stepSize )
     * makes the whole physics simulation less dependant of real time fluctuations. If we integrate using a varying timestep,
     * the errors will not be consistent across many simulations. We ultimately want the physics simulation to be completely
     * replicable.
     *
     * @param deltaTime how far into the future shall the method simulate
     */
    public void update(float deltaTime){
        timeAccumulator += deltaTime*timeDillation;
        for(int i = 0; i < (int)(timeAccumulator/(stepSize)); i++){
            stepUpdate();
            timeAccumulator -= stepSize;
        }
    }

    /**
     * stepUpdate
     *
     * Performs the physics computations in one step time (see stepSize )
     */
    private void stepUpdate(){
        events.clear();
        applyConstantForces(stepSize);

        //Perform the initial search for the entire period of the step
        for(int i = 0; i < entities.size(); i++){
            //Balls are the only moving entities, so we only check their paths
            if(!scm.has(entities.get(i)))
                continue;

            search(entities.get(i), stepSize, 0);
        }

        if(events.size() == 0 ){
            //If no events were found in the initial sweep, we can integrate the position for all entities forward by stepSize
            integrate(stepSize);
        }else{
            float localTime = 0;

            while(localTime < stepSize && events.size() > 0){
                CollisionEvent curEvent = events.pop();
                //Integrate to time of collision
                integrate(curEvent.toi - localTime);

                //Solve the collision
                solve(curEvent);
                //Save the time difference
                localTime += (curEvent.toi-localTime);

                //Look for new events that might have been caused by this collision
                events.stateChanged(curEvent.a);
                search(curEvent.a, stepSize-localTime, localTime);
                if(scm.has(curEvent.b)){
                    events.stateChanged(curEvent.b);
                    search(curEvent.b, stepSize-localTime, localTime);
                }
            }
            //Once all events are solved, integrate the remaining local time
            integrate(stepSize - localTime);
        }
    }

    private void solve(CollisionEvent event){
        Entity a = event.a;
        Entity b = event.b;

        //ugly way to solve ballball and ballmesh collisions differently
        if(scm.has(a) && scm.has(b)){
            ///This might not be accurate. The idea is derived from 2nd newton's law
            // so the impulse generated in the collision is divided across both entities, since
            // the force of the impact is equal on both entities
            float impulse = -(2) * sm.get(a).momentum.dot(event.hitNormal) + 2 * sm.get(b).momentum.dot(event.hitNormal);
            sm.get(a).momentum.mulAdd(event.hitNormal, impulse*0.5f);
            sm.get(b).momentum.mulAdd(event.hitNormal, impulse*-0.5f);
            sm.get(a).update();
            sm.get(b).update();

        }else if(scm.has(a) && mcm.has(b)){
            Vector3 targetPos = event.contactPoint.cpy().mulAdd(event.hitNormal, scm.get(event.a).radius);
            sm.get(a).position.set(targetPos);
            float restitution = combineRestitution(pm.get(a).restitution, pm.get(b).restitution);

            //Calculate the impulse of the impact
            float impulse = -(1 + restitution) * sm.get(a).momentum.dot(event.hitNormal);

            //Applying the impulse ( which is a vector along the surface normal)
            sm.get(a).momentum.mulAdd(event.hitNormal, impulse);

            sm.get(a).update();
        }


    }

    private void integrate(float deltaTime){
        for(int i = 0; i < entities.size(); i++){
            Entity ent = entities.get(i);
            sm.get(ent).position.mulAdd(sm.get(ent).velocity, deltaTime);
            sm.get(ent).update();
        }

    }

    private void search(Entity ent, float deltaTime, float timeOffset){
        for(int i = 0; i < entities.size(); i++){

            if (ent == entities.get(i))
                continue;

            CollisionEvent event = null;
            if(scm.has(entities.get(i))){
                event = checkBallBall(ent, entities.get(i), deltaTime);
            }
            if(mcm.has(entities.get(i))){
                event = checkBallMesh(ent, entities.get(i), deltaTime);
            }

            //If there was a CollisionEvent, add time offset to time of impact and add it to the event queue
            if(event != null){
                event.toi += timeOffset;
                events.add(event);
            }

        }
    }


    private Ray l_ray = new Ray();              //ray
    private Vector3 l_relVel = new Vector3();   //relative velocity
    private Vector3 l_intersection = new Vector3();
    private Vector3 l_surfaceNormal = new Vector3();

    /**
     * Checks if the two objects will collide within deltaTime
     * @param a first entity that contains ball collider component
     * @param b second entity that contains mesh collider component
     * @param deltaTime time to check
     * @return CollisionEvent if there will be a collision, null otherwise
     */
    private CollisionEvent checkBallMesh(Entity a, Entity b, float deltaTime){
        //Relative velocity
        l_relVel.set(sm.get(a).velocity).sub(sm.get(b).velocity);

        float dst2BestIntersection = Float.MAX_VALUE;
        Vector3 bestIntersection = null;
        Vector3 bestNormal = null;

        int bestTriangle = -1;


        for(int i = 0; i < mcm.get(b).vertPosition.length/3; i++){
            Matrix4 meshTransform = sm.get(b).transform;

            //Might have to fix this
            l_surfaceNormal.set(mcm.get(b).vertNormal[i*3]).mul(mcm.get(b).trainvtransform);

            //Set the ray to be cast from impact position on A ball in the direction of relative velocity
            l_ray.origin.set(sm.get(a).position).mulAdd(l_surfaceNormal, -1f*scm.get(a).radius);
            l_ray.direction.set(l_relVel);

            if(l_surfaceNormal.dot(l_relVel) > 0)
                continue;

            MeshColliderComponent bmc = mcm.get(b);

            boolean hit = Intersector.intersectRayTriangle(l_ray,
                    bmc.vertPosition[i * 3].cpy().mul(meshTransform),
                    bmc.vertPosition[i * 3 + 1].cpy().mul(meshTransform),
                    bmc.vertPosition[i * 3 + 2].cpy().mul(meshTransform),
                    l_intersection);

            if(hit){
                float dst2Intersection = l_ray.origin.dst2(l_intersection);
                if(dst2Intersection < dst2BestIntersection){
                    bestIntersection = l_intersection.cpy();
                    dst2BestIntersection = dst2Intersection;
                    bestNormal = l_surfaceNormal.cpy();
                    bestTriangle = i;
                }
            }

        }
        if(bestTriangle > -1 && bestIntersection != null){
            CollisionEvent event = new CollisionEvent();
            event.a = a;
            event.b = b;
            event.hitNormal = bestNormal;
            event.toi = (float) Math.sqrt(dst2BestIntersection) / l_relVel.len();
            event.contactPoint = bestIntersection;
            if(event.toi <= deltaTime)
                return event;
        }
        return null;
    }


    /**
     * Checks if the two objects will collide within deltaTime
     * @param a first entity contains ball collider component
     * @param b second entity contains ball collider component
     * @param deltaTime time to check
     * @return CollisionEvent if there will be a collision, null otherwise
     */
    private CollisionEvent checkBallBall(Entity a, Entity b, float deltaTime){
        //Relative Velocity = A.Velocity - B.Velocity
        l_relVel.set(sm.get(a).velocity).sub(sm.get(b).velocity);

        //Cast ray from A's position in direction of relative velocity
        l_ray.set(sm.get(a).position, l_relVel);


        if (Intersector.intersectRaySphere(l_ray, sm.get(b).position, scm.get(a).radius + scm.get(b).radius, l_intersection)){
            CollisionEvent event = new CollisionEvent();
            event.a = a;
            event.b = b;
            event.hitNormal = l_intersection.cpy().sub(sm.get(b).position).nor();
            //If we are moving away from the object, scrap this
            if(event.hitNormal.dot(l_relVel) > 0)
                return null;
            event.toi = l_ray.origin.dst(l_intersection)/l_relVel.len();
            if(event.toi <= deltaTime) {
                return event;

            }
        }
        return null;
    }

    /**
     * Apply constant forces (i.e. gravity on all entities)
     * @param deltaTime for what time step should it be done?
     */
    private void applyConstantForces(float deltaTime){
        for(Entity ent : entities){
            sm.get(ent).momentum.add(new Vector3(0, gravity * sm.get(ent).mass * deltaTime, 0));

            //Ghetto friction
            sm.get(ent).momentum.scl(0.98f);

            sm.get(ent).update();
        }
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

}

/**
 * This is kind of a priority queue, since the events in this container are always in order.
 * The first event is going to be first ( lowest time of impact )
 */
class EventQueue {
    ArrayList<CollisionEvent> events;

    EventQueue() {
        events = new ArrayList<CollisionEvent>();
    }

    void add(CollisionEvent event){
        //If there's are no events in the list, add this one as the first element
        if(events.size() == 0){
            events.add(event);
            return;
        }

        //Perform a sorted insertion
        for(int i = 0; i < events.size(); i++) {
            //An event like this already exists
            if(events.get(i).equals(event))
                return;
            //Found the spot
            if (events.get(i).toi > event.toi) {
                events.add(i, event);
                return;
            }
        }

        //Insert at the end
        events.add(event);
    }

    void stateChanged(Entity e){
        for(int i = 0; i < events.size(); i++){
            if(events.get(i).involved(e)){
                events.remove(i);
                i--;
            }
        }
    }

    CollisionEvent pop(){
        CollisionEvent event = events.get(0);
        events.remove(0);
        return event;
    }

    void clear(){
        events.clear();
    }

    int size() { return events.size(); }

}

class CollisionEvent{
    Entity a;           // Entity involved in collision event
    Entity b;           // Second entity involved in collision event
    float toi;          // time of impact in seconds
    Vector3 hitNormal;  // the normal of the impact
    Vector3 contactPoint;

    /**
     * Checks if two events are equal ( by comparing the participants of the event and time of impact )
     * @param o the other col event
     * @return true if equal
     */
    boolean equals(CollisionEvent o) {
        //if the toi's are the same, and participants of the event are also the same ( doesn't matter which is a or b )
        //return true
        if(toi == o.toi && ((a == o.a && b == o.a) || (a == o.b && b == o.a)))
            return true;
        return false;
    }

    /**
     * Removes any events which involve entity e
     * @param e the entity
     */
    boolean involved(Entity e){
        return (a == e || b == e);
    }
}