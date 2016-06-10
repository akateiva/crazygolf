package com.group9.crazygolf.entities.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.group9.crazygolf.entities.components.PlayerComponent;
import com.group9.crazygolf.entities.components.StateComponent;
import com.group9.crazygolf.entities.components.VisibleComponent;

import java.util.ArrayList;

/**
 * PlayerSystem
 * <p>
 * Handles the player controls
 */
public class PlayerSystem extends EntitySystem implements InputProcessor {
    //Adjustable constants
    final float maxHitVelocity = 10; // how fast can your boy swiNG THE CLUB EH? M/S

    private ArrayList<EventListener> listeners = new ArrayList<EventListener>();
    private ImmutableArray<Entity> players;

    //Using ComponentMapper allows us to fetch entity components in linear time. Doing otherwise would not.
    private ComponentMapper<StateComponent> stateMap = ComponentMapper.getFor(StateComponent.class);
    private ComponentMapper<PlayerComponent> playerMap = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<VisibleComponent> visibleMap = ComponentMapper.getFor(VisibleComponent.class);
    private Camera cam;
    private Entity turn = null; //The entity whose turn it is right now
    private boolean awaitingInput = true; //If we are not awaiting player input, we are waiting for the balls to stop moving

    public PlayerSystem(Camera cam) {
        this.cam = cam;
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
     * Run continuous tasks.
     * @param deltaTime
     */
    public void update(float deltaTime) {
        boolean allBallsStoppedMoving = true;
        if (!awaitingInput) {
            for (int i = 0; i < players.size(); i++) {
                if (visibleMap.has(players.get(i)) && stateMap.get(players.get(i)).momentum.len2() > 0.05f) {
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
        if (players.size() < 1) {
            turn = null;
            return;
        }
        if (turn == null) {
            try {
                turn = players.first();
            } catch (IllegalStateException e) {
                System.out.println("No players exist in the engine. Can't advance turn.");
                return;
            }
        } else {
            int currentIndex = players.indexOf(turn, true);

            if (currentIndex < players.size() - 1) {
                turn = players.get(currentIndex + 1);
            } else {
                turn = players.first();
            }
        }

        awaitingInput = true;

        for (EventListener listener : listeners) {
            listener.turnChanged(turn);
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
        if (turn != null && awaitingInput && button == Input.Buttons.LEFT) {
            for (EventListener listener : listeners) {
                Vector3 aimVector = computeAimVector(screenX, screenY);
                listener.startedAiming(aimVector, computeAimStrength(aimVector));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (turn != null && awaitingInput && button == Input.Buttons.LEFT) {
            //Apply the hit impulse
            Vector3 aimVector = computeAimVector(screenX, screenY);
            float aimStrength = computeAimStrength(aimVector);
            stateMap.get(turn).momentum.mulAdd(aimVector.nor(), -aimStrength);

            //Send the hit event to listeners
            for (EventListener listener : listeners) {
                listener.struckBall(aimVector, aimStrength);
            }

            //Wait for the balls to stop moving
            awaitingInput = false;
            return true;
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (awaitingInput && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            for (EventListener listener : listeners) {
                Vector3 aimVector = computeAimVector(screenX, screenY);
                listener.changedAim(aimVector, computeAimStrength(aimVector));
            }
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

    public Entity getTurn() {
        return turn;
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

    /**
     * Adds an event listener
     *
     * @param listener the event listener
     */
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    /**
     * Sometimes we want a way to notify other classes about some events that happened in this system.
     */
    public interface EventListener {
        /**
         * Invoked when a player starts aiming
         *
         * @param aimVector
         * @param aimStrength the velocity of the hit in m/s
         */
        void startedAiming(Vector3 aimVector, float aimStrength);

        /**
         * Invoked when aimVector or aimStrength changes
         *
         * @param aimVector
         * @param aimStrength the velocity of the hit in m/s
         */
        void changedAim(Vector3 aimVector, float aimStrength);

        /**
         * Invoked when a player strikes a ball
         *
         * @param aimVector
         * @param aimStrength the velocity of the hit in m/s
         */
        void struckBall(Vector3 aimVector, float aimStrength);

        /**
         * Invoked when a turn change has occured.
         *
         * @param player the new player whose turn is now
         */
        void turnChanged(Entity player);
    }
}