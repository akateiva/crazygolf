package com.group9.crazygolf.phys;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Created by akateiva on 18/04/16.
 */
public class PhysicsManager {

    private PriorityQueue<CollisionEvent> events;

    private LinkedList<EntityStatic> staticEntities; // mostly the world and obstacles
    private LinkedList<EntityBall> ballEntities; // balls


    public PhysicsManager(){
        events = new PriorityQueue<CollisionEvent>(new Comparator<CollisionEvent>() {
            @Override
            public int compare(CollisionEvent o1, CollisionEvent o2) {
                if (o1.getTime() > o2.getTime()) {
                    return 1;
                } else if (o1.getTime() == o2.getTime()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

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
            for (EntityStatic target : staticEntities) {
                CollisionEvent event = ent.check(target, dt);
                if (event != null) {
                    System.out.println(event);
                    events.add(event);
                    solve(event, dt);
                }
            }
            ent.setPosition(ent.getPosition().cpy().mulAdd(ent.getVelocity(), dt));
            ent.update(dt);
            ent.resetForces();
        }
        for (CollisionEvent event : events) {
            //solve(event, dt);
        }
    }


    //Solve a collision event
    public void solve(CollisionEvent event, float dt) {
        float restitution = PhysMaterial.combineRestitution(event.getOrigin().getPhysMaterial(), event.getTarget().getPhysMaterial());

        //Reflect the vector with restitution
        event.getOrigin().getVelocity().sub(event.getNormal().cpy().scl(event.getOrigin().getVelocity().dot(event.getNormal()) * (1 + restitution)));
    }

}
