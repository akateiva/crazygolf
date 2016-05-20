package com.group9.crazygolf.entities.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.group9.crazygolf.entities.components.PlayerComponent;
import com.group9.crazygolf.entities.components.StateComponent;
import com.group9.crazygolf.game.GameUI;

/**
 * GraphicsSystem
 * <p>
 * Handles the drawing of objects
 */
public class PlayerSystem extends EntitySystem implements InputProcessor {
    //Adjustable constants
    final float maxHitVelocity = 10; // how fast can your boy swiNG THE CLUB EH? M/S
    private ImmutableArray<Entity> players;
    //Using ComponentMapper allows us to fetch entity components in linear time. Doing otherwise would not.
    private ComponentMapper<StateComponent> stateMap = ComponentMapper.getFor(StateComponent.class);
    private ComponentMapper<PlayerComponent> playerMap = ComponentMapper.getFor(PlayerComponent.class);
    private Camera cam;
    private Entity turn = null; //The entity whose turn it is right now
    private boolean awaitingInput = true; //If we are not awaiting player input, we are waiting for the balls to stop moving
    private GameUI gameUI;

    public PlayerSystem(Camera cam, GameUI gameUI) {
        this.cam = cam;
        this.gameUI = gameUI;
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
     *
     * @param cam
     */
    public void setCam(Camera cam) {
        this.cam = cam;
    }

    public void addedToEngine(Engine engine) {
        players = engine.getEntitiesFor(Family.all(StateComponent.class, PlayerComponent.class).get());
    }

    /**
     * Draw all components with GraphicsComponent
     *
     * @param deltaTime
     */
    public void update(float deltaTime) {
        boolean allBallsStoppedMoving = true;
        if (!awaitingInput) {
            for (int i = 0; i < players.size(); i++) {
                if (stateMap.get(players.get(i)).momentum.len2() > 0.01f) {
                    allBallsStoppedMoving = false;
                }
            }
            if (allBallsStoppedMoving) {
                advanceTurn();
            }
        }
    }

    /**
     * Change turn to the next available player.
     */
    public void advanceTurn() {
        if (turn == null) {
            try {
                turn = players.first();
            } catch (IllegalStateException e) {
                System.out.println("No players exist in the engine. Can't advance turn.");
                return;
            }
        }
        awaitingInput = true;
        gameUI.addFlashMessage(playerMap.get(turn).name + "'s turn.", 2);
        int currentIndex = players.indexOf(turn, true);

        if (currentIndex < players.size() - 1) {
            turn = players.get(currentIndex + 1);
        } else {
            turn = players.first();
        }
    }

    /**
     * Start a game and assign the first player to turn
     */
    public void startGame() {
        if (turn != null) {
            return; //Can't start a game twice
        }
        advanceTurn();
    }

    /**
     * Stop a game.
     */
    public void stopGame() {
        turn = null;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (turn != null && awaitingInput) {
            gameUI.setPowerBarVisible(true);
            updatePowerBar(screenX, screenY);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (turn != null && awaitingInput && gameUI.isPowerBarVisible()) {
            Vector3 aimVector = computeAimVector(screenX, screenY);
            float aimStrength = computeAimStrength(aimVector);
            stateMap.get(turn).momentum.mulAdd(aimVector.nor(), -aimStrength);
            gameUI.setPowerBarVisible(false);
            awaitingInput = false;
            return true;
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (awaitingInput) {
            updatePowerBar(screenX, screenY);
            return true;
        }
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

    private void updatePowerBar(int screenX, int screenY) {
        Vector3 aimVector = computeAimVector(screenX, screenY);
        float aimStr = computeAimStrength(aimVector);
        gameUI.setPowerBarLevel(aimStr / maxHitVelocity);

    }


    /**
     * Compute the hit vector if player hits from the given on-screen coordinates.
     *
     * @param screenX screen space coordinates
     * @param screenY screen space coordinates
     * @return hit vector
     */
    private Vector3 computeAimVector(int screenX, int screenY) {
        Vector3 aim = new Vector3();
        Intersector.intersectRayPlane(cam.getPickRay(screenX, screenY), new Plane(new Vector3(0, 1, 0), stateMap.get(turn).position), aim);
        aim.sub(stateMap.get(turn).position);

        return aim;
    }

    /**
     * Compute the aim strength for a given aim vector
     *
     * @param aim the aim vector
     * @return Constant C by which normalised aim should be applied to the ball ( C * aim.nor() )
     */
    private float computeAimStrength(Vector3 aim) {
        return Math.min(aim.len() * 8, maxHitVelocity);
    }
}
/*
        float hitLen = hit.len();
        stateMap.get(turn).momentum.mulAdd(hit.nor(), -1 * Math.min(hitLen*hitLen, maxHitVelocity) * stateMap.get(turn).mass);
        System.out.println(stateMap.get(turn).momentum.len());
        stateMap.get(turn).update();

        //Now we are waiting for the balls to stop moving before changing the turn.
        awaitingInput = false;
 */