package com.group9.crazygolf.entities.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.group9.crazygolf.entities.components.GraphicsComponent;
import com.group9.crazygolf.entities.components.StateComponent;
import com.group9.crazygolf.entities.components.VisibleComponent;

/**
 * GraphicsSystem
 * <p>
 * Handles the drawing of objects
 */
public class GraphicsSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private ComponentMapper<StateComponent> stateMap = ComponentMapper.getFor(StateComponent.class);
    private ComponentMapper<GraphicsComponent> graphicsMap = ComponentMapper.getFor(GraphicsComponent.class);
    private ModelBatch modelBatch = new ModelBatch();

    private Camera cam;
    private Environment env;

    public GraphicsSystem(Camera cam, Environment env) {
        this.cam = cam;
        this.env = env;
    }

    /**
     * Get the camera
     *
     * @returns env
     */
    public Camera getCam() {
        return cam;
    }

    /**
     * Set the camera
     * @param cam
     */
    public void setCam(Camera cam) {
        this.cam = cam;
    }

    /**
     * Get the environment
     * @returns env
     */
    public Environment getEnv() {
        return env;
    }

    /**
     * Set the environment
     * @param env
     */
    public void setEnv(Environment env) {
        this.env = env;
    }

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(StateComponent.class, GraphicsComponent.class, VisibleComponent.class).get());
    }

    /**
     * Draw all components with GraphicsComponent
     * @param deltaTime
     */
    public void update(float deltaTime) {
        modelBatch.begin(cam);
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            StateComponent state = stateMap.get(entity);
            GraphicsComponent graphics = graphicsMap.get(entity);

            graphics.modelInstance.transform = state.transform;
            modelBatch.render(graphics.modelInstance, env);
        }
        modelBatch.end();
    }
}
