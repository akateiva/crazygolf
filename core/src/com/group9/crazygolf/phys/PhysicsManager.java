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

    /**
     * Update the physics.
     * While the game is running, time is constatly discretized into intervals between frames ( dt ). Because multiple events
     * can happen during one interval, there is a second time discretization used: relative time ( rt ). For every frame,
     * we look for collisions events in the interval [rt .. 1] ( relative time ), or [rt*dt, dt] in frame time. Dealing
     * with collisions in an orderly fashion allows for accurate updates for simulation ( where dt can be up to 5 seconds )
     *
     * @param dt delta time
     */
    public void update(float dt) {
        float rt = 0;
        for (Entity entities : ballEntities) {
            entities.update(dt);
        }
        while (rt < 1f) {
            findEvents(dt * (1f - rt));

            if (events.size() <= 0) {
                //No collisions ahead, integrate positions until the end of dt
                integrate(dt * (1f - rt));
                rt = 1;
            } else {
                CollisionEvent event = events.poll();

                float restitution = PhysMaterial.combineRestitution(event.getOrigin().getPhysMaterial(), event.getTarget().getPhysMaterial());

                //Integrate the position to the position of impact
                integrate(event.getTime() * dt);

                //Reflect the velocity vector
                event.getOrigin().getVelocity().sub(event.getNormal().cpy().scl(event.getOrigin().getVelocity().dot(event.getNormal()) * (1 + restitution)));

                //Continue searching for events until rt = 1;
                rt += event.getTime();
            }
        }
    }

    /**
     * Euler integration for position
     * @param dt time in seconds to integrate
     */
    private void integrate(float dt) {
        for (EntityBall ent : ballEntities) {
            ent.setPosition(ent.getPosition().cpy().mulAdd(ent.getVelocity(), dt));
        }
    }

    /**
     * Search for collision events
     *
     * @param dt how far in time should the search go ( seconds )
     */
    private void findEvents(float dt) {
        events.clear();
        for (EntityBall ent : ballEntities) {
            for (EntityStatic target : staticEntities) {
                CollisionEvent event = ent.check(target, dt);
                if (event != null && event.getTime() != Float.NaN) {
                    events.add(event);
                }
            }

        }
    }

}
