package com.group9.crazygolf.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.Gson;
import com.group9.crazygolf.TrackingCameraController;
import com.group9.crazygolf.ai.*;
import com.group9.crazygolf.course.Course;
import com.group9.crazygolf.crazygolf;
import com.group9.crazygolf.entities.EntityFactory;
import com.group9.crazygolf.entities.components.PlayerComponent;
import com.group9.crazygolf.entities.components.StateComponent;
import com.group9.crazygolf.entities.components.VisibleComponent;
import com.group9.crazygolf.entities.systems.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreen implements Screen, InputProcessor {
    final private crazygolf game;
    private Engine engine;
    private EntityFactory entityFactory;
    private PerspectiveCamera cam;
    private GameUI gameUI;
    private InputMultiplexer inputMultiplexer;
    private TrackingCameraController trackingCameraController;
    private Course course;
    private SimulationEngine simulationEngine;
    List<Node> path;
    ArrayList<Vector3> pathVec = new ArrayList<Vector3>();
    int index;

    public GameScreen(crazygolf game, NewGameData newGameData, FileHandle courseFile) {

        //Load the course from a file
        Gson gson = new Gson();
        course = gson.fromJson(courseFile.readString(), Course.class);

        /* Set up the camera */
        cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(-10f, 10, 10f);
        cam.lookAt(0,0,0);
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

        /* Set up the environment */
        Environment env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.7f, 1f));
        env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0, -0.8f, 0));

        //Save the reference to Game object
        this.game = game;

        gameUI = new GameUI();
        gameUI.addFlashMessage("Game started.", 5);

        trackingCameraController = new TrackingCameraController(cam);

        engine = new Engine();
        engine.addSystem(new GraphicsSystem(cam, env));
        engine.addSystem(new PhysicsSystem());
        engine.addSystem(new PlayerSystem(cam));
        engine.addSystem(new HoleSystem());
        engine.addSystem(new BoundsSystem(-1.5f));
        setupSystemListeners();
        calcPath();
        //optimizePath();
        simulationEngine = new SimulationEngine(engine.getSystem(PhysicsSystem.class), engine.getSystem(HoleSystem.class), pathVec);

        //Use the entity factory to create entities that we will need
        entityFactory = new EntityFactory();

        for (NewGameData.Player player : newGameData.getPlayerList()) {
            engine.addEntity(entityFactory.createPlayer(player));
        }

        for (NewGameData.Bot bot : newGameData.getBotList()) {
            engine.addEntity(entityFactory.createAIPlayer(bot));
        }

        engine.addEntity(entityFactory.createTerrain(course.getTerrainMesh()));
        engine.addEntity(entityFactory.createHole(course.getEndPosition(), course.getEndNormal()));
        engine.addEntity(entityFactory.createSkybox());

        //Create the walls
        for (int i = 0; i < course.getbI().length; i++) {
            engine.addEntity(entityFactory.createBound(course.getbI()[i]));
        }

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(engine.getSystem(PlayerSystem.class));
        inputMultiplexer.addProcessor(trackingCameraController);

        engine.getSystem(PlayerSystem.class).startGame();

    }

    public void calcPath(){
        PathTest pt = new PathTest(course);
        path = pt.getPath();
        for(int i =0;i<path.size();i++){
            Node currentNode = path.get(i);
            Vector3 vec = new Vector3(currentNode.worldX,currentNode.worldY,currentNode.worldZ);
            pathVec.add(vec);
            //System.out.println(vec.toString());
            //(-2.3731039,-1.9073486E-6,0.11643142);
        }
        System.out.println(path.size()+"   Path Size      "+pathVec.size()+"      Vec Size");
        optimizePath();
    }

    public void optimizePath() {
        ArrayList<Vector3> pvec = new ArrayList<Vector3>();
        float xx = path.get(0).worldX;
        float yy = path.get(0).worldY;
        float zz = path.get(0).worldZ;
        Vector3 start = new Vector3(xx, yy, zz);
        pvec.add(start);

        for (int i = 0; i < path.size() - 2; i++) {
            float x = path.get(i).worldX;
            float y = path.get(i).worldY;
            float z = path.get(i).worldZ;
            Vector3 nodeVec = new Vector3(x, y, z);
            float nx = path.get(i + 1).worldX;
            float ny = path.get(i + 1).worldY;
            float nz = path.get(i + 1).worldZ;
            Vector3 nextVec = new Vector3(nx, ny, nz);
            float nnx = path.get(i + 2).worldX;
            float nny = path.get(i + 2).worldY;
            float nnz = path.get(i + 2).worldZ;
            Vector3 nnextVec = new Vector3(nnx, nny, nnz);

            if ((path.get(i).normal.x == path.get(i + 1).normal.x && path.get(i).normal.y == path.get(i + 1).normal.y &&
                    path.get(i).normal.z == path.get(i + 1).normal.z) && (path.get(i+2).normal.x == path.get(i + 1).normal.x && path.get(i+2).normal.y == path.get(i + 1).normal.y &&
                    path.get(i+2).normal.z == path.get(i + 1).normal.z)) {
                if(!((path.get(i).worldX == path.get(i+1).worldX || path.get(i).worldZ == path.get(i+1).worldZ)&&
                        (path.get(i+2).worldX == path.get(i+1).worldX || path.get(i+2).worldZ == path.get(i+1).worldZ))){
                    pvec.add(nextVec);
                }
            }
        }
        float xxx = path.get(path.size()-1).worldX;
        float yyy = path.get(path.size()-1).worldY;
        float zzz = path.get(path.size()-1).worldZ;
        Vector3 end = new Vector3(xxx, yyy, zzz);
        pvec.add(end);
        pathVec = pvec;
        System.out.println(path.size()+"   Path Size      "+pathVec.size()+"      Vec Size");

    }

    public Vector3 getClosestVec(Vector3 ballPos){
        float distance = Float.MAX_VALUE;
        for(int i=index;i<pathVec.size();i++){
            if(pathVec.get(i).dst2(ballPos)<distance){
                distance = pathVec.get(i).dst2(ballPos);
                index = i;
            }
        }
        return pathVec.get(index);
    }
    public Vector3 getBallDir(Vector3 Pos){
        Vector3 next = new Vector3();
        if(index == pathVec.size()-1){
            //System.out.println("11");
            next = pathVec.get(index);
        }
        else {
            //System.out.println("22");
            next = pathVec.get(index + 1);
        }
        Vector3 currentPos = Pos.cpy();
        Vector3 nextPos = next.cpy();
        currentPos.y = 0;
        nextPos.y = 0;
        Vector3 dir = nextPos.sub(currentPos);
        return dir;
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
                trackingCameraController.setTrackedVector(player.getComponent(StateComponent.class).position);

                //If a player's ball is not visible. It means it has been de-spawned by bound detection or it wasn't spawned into the world in the first place.
                //In that case, move to the start position and set visible.

                StateComponent stateComponent = player.getComponent(StateComponent.class);
                if (player.getComponent(VisibleComponent.class) == null) {
                    stateComponent.position.set(course.getStartPosition()).mulAdd(course.getStartNormal(), 0.1f);
                    player.add(new VisibleComponent());
                }

                //Kill all momentum for safety's sake
                stateComponent.velocity.set(0, 0, 0);
                stateComponent.momentum.set(0, 0, 0);
                stateComponent.update();

                if(!player.getComponent(PlayerComponent.class).ai)
                    return;

                ArrayList<Shot> shots = new ArrayList<Shot>();
                Random rand = new Random();

                for(int i = 0; i < 720; i++){
                    Vector3 close = getClosestVec(engine.getSystem(PlayerSystem.class).getTurn().getComponent(StateComponent.class).position);
                    //shots.add(new Shot(new Vector3(rand.nextFloat() - 0.5f, 0f, rand.nextFloat() - 0.5f).nor(), rand.nextFloat()*10f));
                    Vector3 balldir = getBallDir(close);
                    //if youre close to the whole dont introduce randomness
                    if(index!=pathVec.size()-1) {
                        float x = (rand.nextFloat() * 0.1f);
                        float y = (rand.nextFloat() * 0.1f);
                        //balldir.x += x;
                        //balldir.z += y;
                        //System.out.print(x+"   "+y);
                        shots.add(new Shot(balldir.nor(), rand.nextFloat()*10f));
                        //System.out.println("1");
                    }
                    if(index ==pathVec.size()-1){
                        //System.out.println(balldir.toString());
                        shots.add(new Shot(new Vector3(rand.nextFloat() - 0.5f, 0f, rand.nextFloat() - 0.5f).nor(), rand.nextFloat()*10f));
                        //System.out.println("2");
                    }
                }

                final Entity ply = player;
                SimulationRequest request = new SimulationRequest(new SimulationEngine.SimulationListener() {
                    @Override
                    public void finished(Shot bestShot) {
                        engine.getSystem(PlayerSystem.class).setAwaitingInput(false);
                        ply.getComponent(StateComponent.class).momentum.mulAdd(bestShot.getDirection(), bestShot.getPower());
                        ply.getComponent(StateComponent.class).update();
                    }
                }, shots, player);

                simulationEngine.addRequest(request);
            }
        });

        engine.getSystem(BoundsSystem.class).addListener(new BoundsSystem.EventListener() {
            @Override
            public void ballLeftBounds(Entity ball) {
                gameUI.addFlashMessage(ball.getComponent(PlayerComponent.class).name + "'s ball left the course!", 2.5f);

                //De-spawn (make invisible) the player's ball.
                ball.remove(VisibleComponent.class);

                //If this ball is the one we were waiting for, advance turn
                if (engine.getSystem(PlayerSystem.class).getTurn() == ball) {
                    engine.getSystem(PlayerSystem.class).advanceTurn();
                }
            }
        });

        engine.getSystem(HoleSystem.class).addListener(new HoleSystem.EventListener() {
            @Override
            public void ballInHole(Entity ball) {
                gameUI.addFlashMessage(ball.getComponent(PlayerComponent.class).name + " finished!", 2.5f);
                //Remove the player's ball from the game
                engine.removeEntity(ball);
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        trackingCameraController.update(delta);

        //If simulation engine has requests, update only the graphics system
        if(simulationEngine.hasRequests()){
            engine.getSystem(GraphicsSystem.class).update(delta);
            simulationEngine.update(delta);
        }else {
            engine.update(delta);
        }

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
        switch(keycode){
            case Input.Keys.ESCAPE:
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
