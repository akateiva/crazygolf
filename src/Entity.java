/**
 * Created by akateiva on 14/03/16.
 */

import org.joml.Vector3f;

public abstract class Entity {
    Vector3f position;
    Vector3f angle;

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getAngle() {
        return angle;
    }

    public void setAngle(Vector3f angle) {
        this.angle = angle;
    }

    public abstract void update(long dt);

    public abstract void draw();
}
