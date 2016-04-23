package com.group9.crazygolf;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.group9.crazygolf.phys.EntityDynamic;
import com.group9.crazygolf.phys.EntityStatic;
import com.group9.crazygolf.phys.PhysicsManager;

import static com.badlogic.gdx.Input.Keys;

/**
 * Created by akateiva on 17/04/16.
 */
public class GameScreen implements Screen, InputProcessor {
    Game game;

    PerspectiveCamera cam;
    ModelBatch modelBatch;
    Environment environment;

    PhysicsManager physicsManager;

    EntityStatic world;
    Model worldModel;
    ModelInstance worldModelInstance;

    EntityDynamic ball;
    Model ballModel;
    ModelInstance ballModelInstance;


    GameScreen(Game game){
        this.game = game;

        /* Set up the perspective camera */
        cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(25f, 15f, 25f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        /* Set up the environment */
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        /* Load the models */
        modelBatch = new ModelBatch();
        ModelBuilder modelBuilder = new ModelBuilder();

        worldModel = modelBuilder.createBox(30f, 1f, 30f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        worldModelInstance = new ModelInstance(worldModel);

        ballModel = modelBuilder.createSphere(1f, 1f, 1f, 24, 24,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        ballModelInstance = new ModelInstance(ballModel);

        /* Create and setup the entities */
        ball = new EntityDynamic(ballModelInstance);
        world = new EntityStatic(worldModelInstance);

        ball.setPosition(new Vector3(0, 10, 0));
        ball.applyForce(new Vector3(-100, -500, 0));

        /* Create and setup the physics manager */
        physicsManager = new PhysicsManager();
        physicsManager.add(ball);
        physicsManager.add(world);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {

        physicsManager.update(delta);

        /* Clear the screen */
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        /* Render the models */
        modelBatch.begin(cam);
        modelBatch.render(worldModelInstance, environment);
        modelBatch.render(ballModelInstance, environment);
        modelBatch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        worldModel.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Keys.ENTER){

        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
