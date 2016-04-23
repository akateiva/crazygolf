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
            ent.update(dt);
            for (EntityStatic target : staticEntities) {
                CollisionEvent event = ent.check(target, dt);
                if (event == null) {
                    System.out.println(ent.getVelocity());
                    ent.setPosition(ent.getPosition().cpy().mulAdd(ent.getVelocity(), dt));
                } else {
                    //TODO: Proper collision response
                    ent.getVelocity().scl(-1);
                    System.out.println(event);
                }
            }
        }
    }


}
