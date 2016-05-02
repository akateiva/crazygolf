package com.group9.crazygolf.phys;

import java.util.LinkedList;

/**
 * Created by akateiva on 18/04/16.
 */
public class PhysicsManager {
    private LinkedList<CollisionEvent> events;

    private LinkedList<EntityStatic> staticEntities; // mostly the world and obstacles
    private LinkedList<EntityBall> ballEntities; // balls


    public PhysicsManager(){
        events = new LinkedList<CollisionEvent>();
        staticEntities = new LinkedList<EntityStatic>();
        ballEntities = new LinkedList<EntityBall>();
    }


    public void add(EntityStatic ent) {
        staticEntities.add(ent);
    }

    public void add(EntityBall ent) {
        ballEntities.add(ent);
    }

    public void update(float dt){
        events.clear();
        for (EntityBall ent : ballEntities) {
            ent.update(dt);
            ent.resetForces();
            for (EntityStatic target : staticEntities) {
                CollisionEvent event = ent.check(target, dt);
                if (event != null) {
                    events.add(event);
                }
            }

        }

        //TODO: Implement multiple event handling
        float t = 0; //Current time of the simulation [0...1] ( part of DT )
        CollisionEvent i;
        while (!events.isEmpty()) {
            i = events.poll();
            t += solve(i, dt);
        }

        for (EntityBall ent : ballEntities) {
            ent.setPosition(ent.getPosition().cpy().mulAdd(ent.getVelocity(), dt));
        }
    }

    /**
     * @param dt
     * @param rt
     */
    private void integrate(float dt, float rt) {

    }

    //Solve a collision event
    private float solve(CollisionEvent event, float dt) {
        float restitution = PhysMaterial.combineRestitution(event.getOrigin().getPhysMaterial(), event.getTarget().getPhysMaterial());

        //Removing this for some reason fixes occasional tunneling. Oh well, whatever.
        //event.getOrigin().setPosition(event.getIntersection().cpy().mulAdd(event.getNormal(), event.getOrigin().getRadius()));
        //Reflect the vector with restitution
        System.out.printf("Event: %s\nExpected position:%s\nCalculated position:%s", event, event.getIntersection().cpy().mulAdd(event.getNormal(), event.getOrigin().getRadius()),
                event.getOrigin().getPosition().cpy().mulAdd(event.getOrigin().getVelocity(), event.getTime() * dt));
        event.getOrigin().getVelocity().sub(event.getNormal().cpy().scl(event.getOrigin().getVelocity().dot(event.getNormal()) * (1 + restitution)));

        return event.getTime();
    }

}
