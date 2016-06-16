package com.group9.crazygolf.entities;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.group9.crazygolf.Utility;
import com.group9.crazygolf.course.BoundInfo;
import com.group9.crazygolf.entities.components.*;

import static com.badlogic.gdx.graphics.GL20.GL_TRIANGLES;

/**
 * This class is responsible for providing hard-coded methods to create assemble entities and their components.
 */

public class EntityFactory {
    static EntityFactory instance;
    Texture texture;

    static EntityFactory getInstance() {
        if (instance != null)
            return instance;
        return instance = new EntityFactory();
    }

    public Entity createPlayer(String name) {
        Entity ent = new Entity();

        //Set texture stuff
        FileHandle img = Gdx.files.internal("grass.jpg");
        texture = new Texture(img, Pixmap.Format.RGB565, false);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        texture.setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);

        //Create the transform component
        StateComponent transformComponent = new StateComponent();
        transformComponent.position = new Vector3();
        transformComponent.mass = 1;
        transformComponent.inverseMass = 1.0f / transformComponent.mass;
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
        playerComponent.name = name;
        ent.add(playerComponent);

        return ent;
    }

    //IMPLEMENT PROPERLY ONCE WE FIGURE OUT HOW WE'LL DO COURSE DESIGNER
    public Entity createTerrain(Mesh mesh) {
        Entity ent = new Entity();

        //Create the transform component
        StateComponent transformComponent = new StateComponent();
        transformComponent.position = new Vector3();
        transformComponent.orientation = new Quaternion(new Vector3(0, 0, 0), 0);
        ent.add(transformComponent);

        //Creating a model builder every time is inefficient, but so is talking about this. (JUST WERKS)
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.part("1", mesh, GL_TRIANGLES, new Material(new TextureAttribute(TextureAttribute.Diffuse, texture)));
        Model model = modelBuilder.end();

        ModelInstance boxInst = new ModelInstance(model);

        GraphicsComponent graphicsComponent = new GraphicsComponent();
        graphicsComponent.modelInstance = boxInst;
        ent.add(graphicsComponent);

        //Create a physics component
        PhysicsComponent physicsComponent = new PhysicsComponent();
        ent.add(physicsComponent);

        //Make it visible
        VisibleComponent visibleComponent = new VisibleComponent();
        ent.add(visibleComponent);

        //Create a mesh collider component from the Model mesh
        //(all of this is so fucking bad i cry every time)
        ent.add(Utility.createMeshColliderComponent(model.meshes.first(), transformComponent.transform));

        return ent;
    }

    public Entity createHole(Vector3 pos, Vector3 normal) {
        Entity ent = new Entity();

        //Create the transform component
        StateComponent transformComponent = new StateComponent();
        transformComponent.position = pos;
        transformComponent.autoTransformUpdate = false; // becuase orientation state just fucks my shit up
        ent.add(transformComponent);

        HoleComponent holeComponent = new HoleComponent();
        ent.add(holeComponent);

        //Creating a model builder every time is inefficient, but so is talking about this. (JUST WERKS)
        ModelBuilder modelBuilder = new ModelBuilder();
        Model box = modelBuilder.createSphere(2 * holeComponent.radius, 0.25f * holeComponent.radius, 2 * holeComponent.radius, 24, 24,
                new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance boxInst = new ModelInstance(box, pos);

        GraphicsComponent graphicsComponent = new GraphicsComponent();
        graphicsComponent.modelInstance = boxInst;

        boxInst.transform.rotate(new Vector3(0, 1, 0), normal);

        ent.add(graphicsComponent);

        //Make it visible
        VisibleComponent visibleComponent = new VisibleComponent();
        ent.add(visibleComponent);

        return ent;
    }

    /**
     * jokes on you, this is a skysphere
     *
     * @return entity skysphere
     */
    public Entity createSkybox() {
        Entity ent = new Entity();

        //Create the transform component
        StateComponent transformComponent = new StateComponent();
        transformComponent.position = new Vector3(0f, 0f, 0f);
        transformComponent.orientation = new Quaternion(new Vector3(0, 0, 0), 0);
        transformComponent.update();
        ent.add(transformComponent);

        //Creating a model builder every time is inefficient, but so is talking about this. (JUST WERKS)
        ModelBuilder modelBuilder = new ModelBuilder();
        Model sphere = modelBuilder.createSphere(128, 64, 128, 64, 64,
                new Material(new TextureAttribute(TextureAttribute.Diffuse, new Texture(Gdx.files.local("skybox.jpg"))), new IntAttribute(IntAttribute.CullFace, 0)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);
        ModelInstance skysphere = new ModelInstance(sphere);

        GraphicsComponent graphicsComponent = new GraphicsComponent();
        graphicsComponent.modelInstance = skysphere;
        ent.add(graphicsComponent);

        //Make it visible
        VisibleComponent visibleComponent = new VisibleComponent();
        ent.add(visibleComponent);

        return ent;
    }

    public Entity createBound(BoundInfo bdInfo) {
        Entity ent = new Entity();

        StateComponent transformComponent = new StateComponent();
        transformComponent.transform.setToTranslation(bdInfo.position);
        transformComponent.transform.rotateRad(new Vector3(0, 1, 0), bdInfo.rotAngle);
        transformComponent.autoTransformUpdate = false; // becuase orientation state just fucks my shit up
        ent.add(transformComponent);

        ModelBuilder modelBuilder = new ModelBuilder();
        Model wall = modelBuilder.createBox(bdInfo.length, bdInfo.height, 0.08f, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance wallInstance = new ModelInstance(wall);
        wallInstance.transform = transformComponent.transform;

        PhysicsComponent physicsComponent = new PhysicsComponent();
        ent.add(physicsComponent);

        //transformComponent.transform = wallInstance.transform;

        GraphicsComponent graphicsComponent = new GraphicsComponent();
        graphicsComponent.modelInstance = wallInstance;
        ent.add(graphicsComponent);

        VisibleComponent visibleComponent = new VisibleComponent();
        ent.add(visibleComponent);

        ent.add(Utility.createMeshColliderComponent(wall.meshes.first(), transformComponent.transform));
        return ent;
    }

    public Entity createDummyBall(Vector3 pos){
        Entity ent = new Entity();

        //Create the transform component
        StateComponent transformComponent = new StateComponent();
        transformComponent.position = pos;
        transformComponent.mass = 1;
        transformComponent.inverseMass = 1.0f / transformComponent.mass;
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

        ent.add(new VisibleComponent());
        return ent;
    }

    public Entity createGhostBall(Vector3 pos){
        Entity ent = new Entity();

        //Create the transform component
        StateComponent transformComponent = new StateComponent();
        transformComponent.position = pos;
        transformComponent.mass = 1;
        transformComponent.inverseMass = 1.0f / transformComponent.mass;
        transformComponent.velocity = new Vector3();
        transformComponent.momentum = new Vector3(0, 0, 0);
        transformComponent.orientation = new Quaternion();
        transformComponent.update();
        ent.add(transformComponent);


        //Create a graphics component
        ModelBuilder modelBuilder = new ModelBuilder();
        Model ball = modelBuilder.createSphere(2 * 0.02135f, 0.02135f, 0.02135f, 24, 24,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        GraphicsComponent graphicsComponent = new GraphicsComponent();
        graphicsComponent.modelInstance = new ModelInstance(ball);
        ent.add(graphicsComponent);

        ent.add(new VisibleComponent());
        return ent;
    }

    public Entity createDebugVector(Vector3 from, Vector3 to) {
        Entity ent = new Entity();

        //Create the transform component
        StateComponent transformComponent = new StateComponent();
        //transformComponent.position = position;
        transformComponent.orientation = new Quaternion();
        transformComponent.autoTransformUpdate = false;
        //transformComponent.update();
        ent.add(transformComponent);


        //Create a graphics component
        ModelBuilder modelBuilder = new ModelBuilder();
        Model arrow = modelBuilder.createArrow(from, to, new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        GraphicsComponent graphicsComponent = new GraphicsComponent();
        graphicsComponent.modelInstance = new ModelInstance(arrow);
        ent.add(graphicsComponent);

        ent.add(new VisibleComponent());


        return ent;

    }

}
