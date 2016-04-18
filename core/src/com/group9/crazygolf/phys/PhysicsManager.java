package com.group9.crazygolf.phys;

import java.util.LinkedList;

/**
 * Created by akateiva on 18/04/16.
 */
public class PhysicsManager {

    private LinkedList<CollisionEvent> events;

    private LinkedList<Entity> staticEntities; // mostly the world and obstacles
    private LinkedList<Entity> dynamicEntities; // balls


    public PhysicsManager(){
        events = new LinkedList<CollisionEvent>();

        staticEntities = new LinkedList<Entity>();
        dynamicEntities = new LinkedList<Entity>();
    }



    public void update(float dt){
        for(Entity ent : dynamicEntities){
            //Do a swept sphere check. Segment length: velocity*dt
            boolean sweepCheck = false;
            if(sweepCheck == false){
                //
            }
        }
    }


}
