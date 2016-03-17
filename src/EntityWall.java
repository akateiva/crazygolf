import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Vector;

/**
 * Created by akateiva on 16/03/16.
 */
public class EntityWall extends EntityPlainDrawable {
    private Vector3f endPosition;
    EntityWall(Vector3f startPosition, Vector3f endPosition){
        super(new Mesh(new Vector2f(endPosition.x-startPosition.x, endPosition.y-startPosition.y)));
        setPosition(startPosition);
        this.endPosition = endPosition;
        setColor(1f, 1f, 0f, 1f);
    }
    @Override
    public void update(long dt) {
        super.update(dt);
    }

    @Override
    public void draw() {
        super.draw();
    }
}
