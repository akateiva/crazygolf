package com.group9.crazygolf.entities;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.group9.crazygolf.Utility;
import com.group9.crazygolf.entities.components.GraphicsComponent;
import com.group9.crazygolf.entities.components.PhysicsComponent;
import com.group9.crazygolf.entities.components.SphereColliderComponent;
import com.group9.crazygolf.entities.components.StateComponent;

public class EntityFactory {

    public Entity createBall(Vector3 position) {
        Entity ent = new Entity();

        //Create the transform component
        StateComponent transformComponent = new StateComponent();
        transformComponent.position = position;
        transformComponent.mass = 1;
        transformComponent.inverseMass = 1.0f / transformComponent.mass;
        transformComponent.position = new Vector3(0, 2, 0);
        transformComponent.velocity = new Vector3();
        transformComponent.momentum = new Vector3(0, 0, 0);
        transformComponent.orientation = new Quaternion();
        transformComponent.angularMomentum = new Vector3(0, 0, 0);
        transformComponent.inertiaTensor = transformComponent.mass * 1 * 1.0f / 6.0f;//ADD SIZE
        transformComponent.inverseInertiaTensor = 1.0f / transformComponent.inertiaTensor;
        transformComponent.update();
        ent.add(transformComponent);

        //Create a physics component
        PhysicsComponent physicsComponent = new PhysicsComponent();
        ent.add(physicsComponent);

        //Create a sphere collider component
        SphereColliderComponent sphereColliderComponent = new SphereColliderComponent();
        sphereColliderComponent.radius = 0.02135f;
        ent.add(sphereColliderComponent);

        //Create a graphics component
        ModelBuilder modelBuilder = new ModelBuilder();
        Model ball = modelBuilder.createSphere(2 * sphereColliderComponent.radius, 2 * sphereColliderComponent.radius, 2 * sphereColliderComponent.radius, 24, 24,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        GraphicsComponent graphicsComponent = new GraphicsComponent();
        graphicsComponent.modelInstance = new ModelInstance(ball);
        ent.add(graphicsComponent);

        return ent;
    }

    //IMPLEMENT PROPERLY ONCE WE FIGURE OUT HOW WE'LL DO COURSE DESIGNER
    public Entity createTerrain() {
        Entity ent = new Entity();

        //Create the transform component
        StateComponent transformComponent = new StateComponent();
        transformComponent.position = new Vector3();
        ent.add(transformComponent);

        //Creating a model builder every time is inefficient, but so is talking about this. (JUST WERKS)
        ModelBuilder modelBuilder = new ModelBuilder();
        Model box = modelBuilder.createBox(1f, 0.1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance boxInst = new ModelInstance(box);

        GraphicsComponent graphicsComponent = new GraphicsComponent();
        graphicsComponent.modelInstance = boxInst;
        ent.add(graphicsComponent);

        //Create a physics component
        PhysicsComponent physicsComponent = new PhysicsComponent();
        ent.add(physicsComponent);

        //Create a mesh collider component from the Model mesh
        //(all of this is so fucking bad i cry every time)
        ent.add(Utility.createMeshColliderComponent(box.meshes.get(0)));

        return ent;
    }

    public Entity createArrow() {
        Entity ent = new Entity();

        //Create the transform component
        StateComponent transformComponent = new StateComponent();
        transformComponent.position = new Vector3(0f, 1f, 0f);
        ent.add(transformComponent);

        //Creating a model builder every time is inefficient, but so is talking about this. (JUST WERKS)
        ModelBuilder modelBuilder = new ModelBuilder();
        Model box = modelBuilder.createArrow(0, 1f, 0, 0, 0, 0, 0.1f, 0.1f, 24, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance boxInst = new ModelInstance(box);

        GraphicsComponent graphicsComponent = new GraphicsComponent();
        graphicsComponent.modelInstance = boxInst;
        ent.add(graphicsComponent);
        return ent;
    }
}
