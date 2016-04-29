package com.group9.crazygolf.phys;

/**
 * Created by akateiva on 18/04/16.
 */

import com.badlogic.gdx.math.Vector3;

/**
 * Collision events are
 */
public class CollisionEvent {
    final private float time; // as part of a frame
    final private EntityBall origin; // first entity involved in the event
    final private Entity target; // second entity involved in the event
    final private Vector3 normal; // the direction entity Origin should take after collision
    final private Vector3 intersection;

    public CollisionEvent(float time, EntityBall origin, Entity target, Vector3 normal, Vector3 intersection) {
        this.time = time;
        this.origin = origin;
        this.target = target;
        this.normal = normal;
        this.intersection = intersection;
    }

    public float getTime() {
        return time;
    }

    public EntityBall getOrigin() {
        return origin;
    }

    public Entity getTarget() {
        return target;
    }

    public Vector3 getIntersection() {
        return intersection;
    }

    public Vector3 getNormal() {
        return normal;
    }

    @Override
    public String toString() {
        return "CollisionEvent{" +
                "time=" + time +
                ", origin=" + origin +
                ", target=" + target +
                ", normal=" + normal +
                '}';
    }
}
