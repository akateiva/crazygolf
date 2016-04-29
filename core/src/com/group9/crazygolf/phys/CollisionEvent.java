package com.group9.crazygolf.phys;

/**
 * Created by akateiva on 18/04/16.
 */

import com.badlogic.gdx.math.Vector3;

/**
 * Collision events are
 */
public class CollisionEvent {
    float time; // as part of a frame

    Entity origin; // first entity involved in the event
    Entity target; // second entity involved in the event

    Vector3 normal; // the direction entity Origin should take after collision
    Vector3 intersection;

    public CollisionEvent(float time, Entity origin, Entity target, Vector3 normal, Vector3 intersection) {
        this.time = time;
        this.origin = origin;
        this.target = target;
        this.normal = normal;
        this.intersection = intersection;

    }

    public float getTime() {
        return time;
    }

    public Entity getOrigin() {
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
