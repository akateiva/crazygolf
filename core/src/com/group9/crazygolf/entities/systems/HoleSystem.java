package com.group9.crazygolf.entities.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.group9.crazygolf.entities.components.HoleComponent;
import com.group9.crazygolf.entities.components.PlayerComponent;
import com.group9.crazygolf.entities.components.StateComponent;

/**
 * Hole systems tracks holes on the maps and checks whether any balls have entered them.
 */
public class HoleSystem extends EntitySystem {
    private ImmutableArray<Entity> holes;
    private ImmutableArray<Entity> balls;

    public HoleSystem() {

    }

    public void addedToEngine(Engine engine) {
        holes = engine.getEntitiesFor(Family.all(HoleComponent.class, StateComponent.class).get());
        balls = engine.getEntitiesFor(Family.all(StateComponent.class, PlayerComponent.class).get());
    }

    public void update(float deltaTime) {
        for (int i = 0; i < holes.size(); i++) {
            for (int j = 0; j < balls.size(); j++) {

            }
        }
    }
}
