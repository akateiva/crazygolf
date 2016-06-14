package com.group9.crazygolf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

/**
 * A camera controller that can track position vectors automagically.
 */
public class TrackingCameraController implements InputProcessor {
    float cameraDistance = 3f;
    float cameraLerp = 0.05f;

    float maxCameraDistance = 10f;
    float minCameraDistance = 0.1f;
    float cameraSensitivity = 0.2f;
    private Camera cam;
    private Vector3 trackedPosition;
    private Vector3 targetDirection;
    private Vector3 targetPosition;


    private Vector3 tmpV1 = new Vector3();
    private Vector3 tmpV2 = new Vector3();

    private int mouseLastX = 0;
    private int mouseLastY = 0;


    public TrackingCameraController(Camera cam) {
        this.cam = cam;
        trackedPosition = new Vector3();
        targetDirection = cam.direction.cpy();
        targetPosition = cam.position.cpy();
    }

    //TODO: Add a swoopIn method that can shows an overview of the course

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
        mouseLastX = screenX;
        mouseLastY = screenY;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    /**
     * tmpVec.set(point);
     * tmpVec.sub(position);
     * translate(tmpVec);
     * rotate(axis, angle);
     * tmpVec.rotate(axis, angle);
     * translate(-tmpVec.x, -tmpVec.y, -tmpVec.z);
     *
     * @param screenX
     * @param screenY
     * @param pointer
     * @return
     */
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            tmpV1.set(cam.direction).crs(cam.up).y = 0f;

            cam.rotateAround(trackedPosition, tmpV1.nor(), mouseLastY - screenY);
            cam.rotateAround(trackedPosition, Vector3.Y, mouseLastX - screenX);

            mouseLastX = screenX;
            mouseLastY = screenY;
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
        cameraDistance = Math.max(minCameraDistance, Math.min(cameraDistance + amount, maxCameraDistance));
        return true;
    }

    public void update(float deltaTime) {
        if (trackedPosition != null) {
            targetPosition.set(trackedPosition).mulAdd(cam.direction, -1 * cameraDistance);

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
    public Vector3 getTrackedEntity() {
        return trackedPosition;
    }

    /**
     * setTrackedVector
     *
     * @param trackedPosition the entity that the camera will be tracking
     */
    public void setTrackedVector(Vector3 trackedPosition) {
        this.trackedPosition = trackedPosition;
    }
}
