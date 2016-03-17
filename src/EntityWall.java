import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Created by akateiva on 16/03/16.
 */
public class EntityWall extends EntityPlainDrawable {
    private Vector3f endPosition;

    private Vector3f normal;

    EntityWall(Vector3f startPosition, Vector3f endPosition){
        super(new Mesh(new Vector2f(endPosition.x-startPosition.x, endPosition.y-startPosition.y)));
        setPosition(startPosition);
        this.endPosition = endPosition;
        setColor(1f, 1f, 0f, 1f);

        computeNormal();
    }
    @Override
    public void update(long dt) {
        super.update(dt);
    }

    @Override
    public void draw() {
        super.draw();
    }

    private void computeNormal(){
        //First get the direction of the line
        normal = new Vector3f();
        Vector3f direction = getPosition().sub(endPosition, new Vector3f()).normalize();

        //Compute the normal
        normal.x = -direction.y;
        normal.y = direction.x;
    }

    public Vector3f getNormal() {
        return normal;
    }
    public Vector3f getEndPosition() {
        return endPosition;
    }
}
