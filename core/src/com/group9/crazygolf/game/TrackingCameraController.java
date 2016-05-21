package com.group9.crazygolf.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.group9.crazygolf.entities.components.StateComponent;

/**
 * A camera controller that can track entities.
 */
public class TrackingCameraController implements InputProcessor {
    float cameraDistance = 3f;
    float cameraLerp = 0.1f;

    float maxCameraDistance = 10f;
    float minCameraDistance = 2f;
    private Camera cam;
    private Entity trackedEntity;
    private Vector3 targetDirection;
    private Vector3 targetPosition;


    public TrackingCameraController(Camera cam) {
        this.cam = cam;
        targetDirection = cam.direction.cpy();
        targetPosition = cam.position.cpy();

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
        cameraDistance = Math.max(minCameraDistance, Math.min(cameraDistance + amount, maxCameraDistance));
        return true;
    }

    public void update(float deltaTime) {
        if (trackedEntity != null) {
            targetPosition.set(getTrackedEntity().getComponent(StateComponent.class).position).mulAdd(cam.direction, -1 * cameraDistance);

            //Insead of jumping straight to the new position, interpolate into target, so we get that nice swooshy camera feel
            cam.position.lerp(targetPosition, cameraLerp);
            cam.update();
        }
    }

    /**
     * getTrackedEntity
     *
     * @return returns the entity that is currently tracked by the camera
     */
    public Entity getTrackedEntity() {
        return trackedEntity;
    }

    /**
     * setTrackedEntity
     *
     * @param trackedEntity the entity that the camera will be tracking
     */
    public void setTrackedEntity(Entity trackedEntity) {
        this.trackedEntity = trackedEntity;
    }
}
