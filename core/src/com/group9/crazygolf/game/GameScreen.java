package com.group9.crazygolf.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.group9.crazygolf.crazygolf;
import com.group9.crazygolf.entities.EntityFactory;
import com.group9.crazygolf.entities.components.PlayerComponent;
import com.group9.crazygolf.entities.components.StateComponent;
import com.group9.crazygolf.entities.components.VisibleComponent;
import com.group9.crazygolf.entities.systems.BoundsSystem;
import com.group9.crazygolf.entities.systems.GraphicsSystem;
import com.group9.crazygolf.entities.systems.PhysicsSystem;
import com.group9.crazygolf.entities.systems.PlayerSystem;


public class GameScreen implements Screen, InputProcessor {
    final private crazygolf game;
    private Engine engine;
    private EntityFactory entityFactory;
    private PerspectiveCamera cam;
    private GameUI gameUI;
    private InputMultiplexer inputMultiplexer;
    private TrackingCameraController trackingCameraController;

    public GameScreen(crazygolf game, NewGameData newGameData) {
        /* Set up the camera */
        cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 0.1f, 4f);
        cam.lookAt(0,0,0);
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

        /* Set up the environment */
        Environment env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        //Save the reference to Game object
        this.game = game;

        //Game user interface
        gameUI = new GameUI();
        gameUI.addFlashMessage("Game started.", 5);

        //Camera control
        trackingCameraController = new TrackingCameraController(cam);

        //Initialize the entity-component-system
        engine = new Engine();
        engine.addSystem(new GraphicsSystem(cam, env));
        engine.addSystem(new PhysicsSystem());
        engine.addSystem(new PlayerSystem(cam));
        engine.addSystem(new BoundsSystem(-1.5f));
        setupSystemListeners();


        //Use the entity factory to create entities that we will need
        entityFactory = new EntityFactory();

        for (NewGameData.Player player : newGameData.getPlayerList()) {
            engine.addEntity(entityFactory.createPlayer(player.name));
        }

        engine.addEntity(entityFactory.createTerrain());
        engine.addEntity(entityFactory.createTerrain2());
        engine.addEntity(entityFactory.createHole());


        engine.getSystem(PlayerSystem.class).startGame();


        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(engine.getSystem(PlayerSystem.class));
        inputMultiplexer.addProcessor(trackingCameraController);




    }

    private void setupSystemListeners() {
        engine.getSystem(PlayerSystem.class).addListener(new PlayerSystem.EventListener() {
            @Override
            public void startedAiming(Vector3 aimVector, float aimStrength) {
                gameUI.setPowerBarVisible(true);
                gameUI.setPowerBarLevel(aimStrength / 10);
            }

            @Override
            public void changedAim(Vector3 aimVector, float aimStrength) {
                gameUI.setPowerBarLevel(aimStrength / 10);
            }

            @Override
            public void struckBall(Vector3 aimVector, float aimStrength) {
                gameUI.setPowerBarVisible(false);
            }

            @Override
            public void turnChanged(Entity player) {
                gameUI.addFlashMessage(player.getComponent(PlayerComponent.class).name + "'s turn!", 2.5f);
                trackingCameraController.setTrackedEntity(player.getComponent(StateComponent.class).position);

                //If a player's ball is not visible. It means it has been de-spawned by bound detection or it wasn't spawned into the world in the first place.
                //In that case, move to the start position and set visible.
                if (player.getComponent(VisibleComponent.class) == null) {
                    StateComponent stateComponent = player.getComponent(StateComponent.class);
                    stateComponent.position.set(Vector3.Y.cpy());
                    stateComponent.velocity.set(0, 0, 0);
                    stateComponent.momentum.set(0, 0, 0);
                    player.add(new VisibleComponent());
                }
            }
        });

        engine.getSystem(BoundsSystem.class).addListener(new BoundsSystem.EventListener() {
            @Override
            public void ballLeftBounds(Entity ball) {
                gameUI.addFlashMessage(ball.getComponent(PlayerComponent.class).name + "'s ball went out of the bounds!", 2.5f);

                //De-spawn (make invisible) the player's ball.
                ball.remove(VisibleComponent.class);

                //If this ball is the one we were waiting for, advance turn
                if (engine.getSystem(PlayerSystem.class).getTurn() == ball) {
                    engine.getSystem(PlayerSystem.class).advanceTurn();
                }
            }
        });
    }
    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        trackingCameraController.update(delta);
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

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            game.showPauseMenu();
            return true;
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
