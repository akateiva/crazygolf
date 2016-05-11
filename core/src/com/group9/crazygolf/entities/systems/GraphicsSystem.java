package com.group9.crazygolf.entities.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.group9.crazygolf.entities.components.GraphicsComponent;
import com.group9.crazygolf.entities.components.StateComponent;

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

    public Camera getCam() {
        return cam;
    }

    public void setCam(Camera cam) {
        this.cam = cam;
    }

    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(StateComponent.class, GraphicsComponent.class).get());
    }

    public void update(float deltaTime) {
        modelBatch.begin(cam);
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            StateComponent state = stateMap.get(entity);
            GraphicsComponent graphics = graphicsMap.get(entity);

            //TODO: Rotate by quaternion
            graphics.modelInstance.transform = new Matrix4().translate(state.position);
            modelBatch.render(graphics.modelInstance, env);
        }
        modelBatch.end();
    }
}
