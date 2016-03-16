/**
 * Created by akateiva on 14/03/16.
 */

import org.joml.Vector3f;

public abstract class Entity {

    //The position of this entity in world space expressed in a 3D vector
    private Vector3f position;
    //The angle of top-down rotation on the entity in radians
    private float angle;
    //Whether the entity should be saved in the course file or not
    private boolean persistent;

    private boolean visible;

    Entity(){
        position = new Vector3f();
        angle = 0;
        persistent = false;
        visible = true;
    }

    /**
     *
     * @return the position of the entity in world space
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     *
     * @param position the position of the entity in world space
     */
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     *
     * @return the angle of rotation on the Z axis in radians
     */
    public float getAngle() {
        return angle;
    }

    /**
     *
     * @param angle the angle of rotation on the Z axis in radians
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    /**
     * An object is persistent if information about it has to be stored in the course file. Entities such as Obstacles, StartPosition are persistent, but Ball is not.
     * @return whether the object is a part of the course or just the game
     */
    public boolean isPersistent() {
        return persistent;
    }

    /**
     *
     * @return whether the object is a part of the course or just the game
     */
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }
    public abstract void update(long dt);

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public abstract void draw();


}
