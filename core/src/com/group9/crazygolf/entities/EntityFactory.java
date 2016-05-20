package com.group9.crazygolf.entities;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.group9.crazygolf.Utility;
import com.group9.crazygolf.entities.components.*;

public class EntityFactory {

    public Entity createBall(Vector3 position) {
        Entity ent = new Entity();

        //Create the transform component
        StateComponent transformComponent = new StateComponent();
        transformComponent.position = position;
        transformComponent.mass = 1;
        transformComponent.inverseMass = 1.0f / transformComponent.mass;
        transformComponent.position = position;
        transformComponent.velocity = new Vector3();
        transformComponent.momentum = new Vector3(0, 0, 0);
        transformComponent.orientation = new Quaternion();
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

        //Create a player component
        PlayerComponent playerComponent = new PlayerComponent();
        ent.add(playerComponent);

        return ent;
    }

    //IMPLEMENT PROPERLY ONCE WE FIGURE OUT HOW WE'LL DO COURSE DESIGNER
    public Entity createTerrain() {
        Entity ent = new Entity();

        //Create the transform component
        StateComponent transformComponent = new StateComponent();
        transformComponent.position = new Vector3();
        transformComponent.orientation = new Quaternion(new Vector3(0, 0, 1), 0);
        ent.add(transformComponent);

        //Creating a model builder every time is inefficient, but so is talking about this. (JUST WERKS)
        ModelBuilder modelBuilder = new ModelBuilder();
        Model box = modelBuilder.createBox(10f, 0.1f, 10f,
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

    public Entity createTerrain2() {
        Entity ent = new Entity();

        //Create the transform component
        StateComponent transformComponent = new StateComponent();
        transformComponent.position = new Vector3();
        transformComponent.orientation = new Quaternion(new Vector3(0, 0, 1), -5);
        ent.add(transformComponent);

        //Creating a model builder every time is inefficient, but so is talking about this. (JUST WERKS)
        ModelBuilder modelBuilder = new ModelBuilder();
        Model box = modelBuilder.createBox(4f, 0.1f, 1f,
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

    public Entity createHole() {
        Entity ent = new Entity();

        //Create the transform component
        StateComponent transformComponent = new StateComponent();
        transformComponent.position = new Vector3(0.5f, 0.05f, 0f);
        transformComponent.orientation = new Quaternion(new Vector3(0, 0, 1), 0);
        transformComponent.update();
        ent.add(transformComponent);

        HoleComponent holeComponent = new HoleComponent();
        ent.add(holeComponent);

        //Creating a model builder every time is inefficient, but so is talking about this. (JUST WERKS)
        ModelBuilder modelBuilder = new ModelBuilder();
        Model box = modelBuilder.createSphere(2 * holeComponent.radius, 0.25f * holeComponent.radius, 2 * holeComponent.radius, 24, 24,
                new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance boxInst = new ModelInstance(box);

        GraphicsComponent graphicsComponent = new GraphicsComponent();
        graphicsComponent.modelInstance = boxInst;
        ent.add(graphicsComponent);

        return ent;
    }

}
