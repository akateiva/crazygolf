package com.group9.crazygolf.phys;

import java.util.LinkedList;

/**
 * Created by akateiva on 18/04/16.
 */
public class PhysicsManager {

    private LinkedList<CollisionEvent> events;

    private LinkedList<EntityStatic> staticEntities; // mostly the world and obstacles
    private LinkedList<EntityDynamic> dynamicEntities; // balls


    public PhysicsManager(){
        events = new LinkedList<CollisionEvent>();

        staticEntities = new LinkedList<EntityStatic>();
        dynamicEntities = new LinkedList<EntityDynamic>();
    }


    public void add(EntityStatic ent) {
        staticEntities.add(ent);
    }

    public void add(EntityDynamic ent) {
        dynamicEntities.add(ent);
    }


    public void update(float dt){
        for (EntityDynamic ent : dynamicEntities) {
            for (EntityStatic target : staticEntities) {
                CollisionEvent event = ent.check(target, dt);
                if (event == null) {
                    ent.setPosition(ent.getPosition().cpy().mulAdd(ent.getVelocity(), dt));
                } else {
                    /*
                    Reflect the velocity vector off the normal
                    V = V − 2 ( V ⋅ N ) N

                    Currently it is completely elastic, but inefficiencies can be introduced by further scaling the vector by a constant like 0.95 or something

                    Written in retarded chaining style, just because Java
                    */

                    System.out.println(event);
                    ent.getVelocity().sub(event.getNormal().cpy().scl(ent.getVelocity().dot(event.getNormal()) * 2));
                }
            }
            ent.update(dt);
        }
    }


}
