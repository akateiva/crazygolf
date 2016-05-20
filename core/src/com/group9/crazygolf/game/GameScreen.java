package com.group9.crazygolf.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.group9.crazygolf.entities.EntityFactory;
import com.group9.crazygolf.entities.systems.GraphicsSystem;
import com.group9.crazygolf.entities.systems.PhysicsSystem;
import com.group9.crazygolf.entities.systems.PlayerSystem;


public class GameScreen implements Screen {
    Game game;
    Engine engine;
    EntityFactory entityFactory;
    PerspectiveCamera cam;
    GameUI gameUI;
    public GameScreen(Game game) {
        /* Set up the camera */
        cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 2f, 2f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        /* Set up the environment */
        Environment env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        //Save the reference to Game object
        this.game = game;

        gameUI = new GameUI();

        //Initialize the entity-component-system
        engine = new Engine();
        engine.addSystem(new GraphicsSystem(cam, env));
        engine.addSystem(new PhysicsSystem());
        engine.addSystem(new PlayerSystem(cam, gameUI));
        //Use the entity factory to create entities that we will need
        entityFactory = new EntityFactory();
        engine.addEntity(entityFactory.createBall(new Vector3(0.1f, 0.4f, 0.01f)));
        engine.addEntity(entityFactory.createBall(new Vector3(-0.1f, 0.6f, 0)));
        engine.addEntity(entityFactory.createBall(new Vector3(-0.3f, 0.6f, 0)));
        engine.addEntity(entityFactory.createTerrain());
        engine.addEntity(entityFactory.createTerrain2());


        engine.getSystem(PlayerSystem.class).startGame();
        Gdx.input.setInputProcessor(engine.getSystem(PlayerSystem.class));


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        engine.update(delta);
        gameUI.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        Gdx.gl.glViewport(0, 0, width, height);
        cam.viewportHeight = height;
        cam.viewportWidth = width;
        cam.update();
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

    }
}
