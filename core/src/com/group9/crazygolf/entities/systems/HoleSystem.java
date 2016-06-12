package com.group9.crazygolf.entities.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.group9.crazygolf.entities.components.HoleComponent;
import com.group9.crazygolf.entities.components.PlayerComponent;
import com.group9.crazygolf.entities.components.StateComponent;
import com.group9.crazygolf.entities.components.VisibleComponent;

import java.util.ArrayList;

/**
 * Hole systems tracks holes on the maps and checks whether any balls have entered them.
 */
public class HoleSystem extends EntitySystem {
    final public float maxVelocitySquared = 6 * 6;
    private ImmutableArray<Entity> holes;
    private ImmutableArray<Entity> balls;
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);
    private ComponentMapper<PlayerComponent> pm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<HoleComponent> hm = ComponentMapper.getFor(HoleComponent.class);
    private ArrayList<EventListener> listeners = new ArrayList<EventListener>();

    public HoleSystem() {

    }

    /**
     * Adds an event listener
     *
     * @param listener the event listener
     */
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    public void addedToEngine(Engine engine) {
        holes = engine.getEntitiesFor(Family.all(HoleComponent.class, StateComponent.class).get());
        balls = engine.getEntitiesFor(Family.all(StateComponent.class, PlayerComponent.class, VisibleComponent.class).get());
    }

    public void update(float deltaTime) {
        for (int i = 0; i < holes.size(); i++) {
            for (int j = 0; j < balls.size(); j++) {
                if (sm.get(balls.get(j)).velocity.len2() >= maxVelocitySquared)
                    continue;
                if (sm.get(holes.get(i)).position.dst(sm.get(balls.get(j)).position) < hm.get(holes.get(i)).radius) {
                    for (EventListener listener : listeners) {
                        listener.ballInHole(balls.get(j));
                        pm.get(balls.get(i)).finished = true;
                    }
                }
            }
        }
    }

    public float dst2ClosestHole(Entity ply){
        float dst2 = Float.MAX_VALUE;
        for (int i = 0; i < holes.size(); i++) {
            float dst2Candidate = sm.get(ply).position.dst2(sm.get(holes.get(i)).position);
            if(dst2Candidate < dst2)
                dst2 = dst2Candidate;
        }
        return dst2;
    }

    public interface EventListener {
        void ballInHole(Entity ball);
    }
}
