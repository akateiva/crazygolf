package com.group9.crazygolf.entities.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.group9.crazygolf.entities.components.PlayerComponent;
import com.group9.crazygolf.entities.components.StateComponent;

import java.util.ArrayList;

/**
 * Bounds system issues events whenever a ball leaves the playing area.
 * The playing area is limited by how far down into the world balls can go.
 * If a ball position's Y component moves below minimumY, a ballLeftBounds event is invoked in event the listeners.
 */
public class BoundsSystem extends EntitySystem {
    private float minimumY;

    private ArrayList<EventListener> listeners = new ArrayList<EventListener>();

    private ImmutableArray<Entity> balls;
    private ComponentMapper<StateComponent> stateMap = ComponentMapper.getFor(StateComponent.class);

    /**
     * Create a BoundsSystem
     *
     * @param minimumY how deep into the world can the balls go
     */
    public BoundsSystem(float minimumY) {
        this.minimumY = minimumY;
    }

    public void addedToEngine(Engine engine) {
        balls = engine.getEntitiesFor(Family.all(StateComponent.class, PlayerComponent.class).get());
    }

    public void update(float deltaTime) {
        for (int i = 0; i < balls.size(); i++) {
            if (stateMap.get(balls.get(i)).position.y <= minimumY) {
                for (EventListener listener : listeners) {
                    listener.ballLeftBounds(balls.get(i));
                }
            }
        }
    }

    /**
     * Adds an event listener
     *
     * @param listener the event listener
     */
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    public interface EventListener {
        void ballLeftBounds(Entity ball);
    }
}
