import org.joml.Matrix3f;
import org.joml.Vector3f;

/**
 * Created by akateiva on 15/03/16.
 */
public class EntityBall extends EntityPlainDrawable {

    //The velocity of the ball in units/s ( 1 m/s = 100 units/s )
    private Vector3f velocity;
    private float radius;

    EntityBall(){
        super(new Mesh(Util.resourceToString("res/models/golfball.obj")));

        velocity = new Vector3f(0f, 0f, 0f);
        radius = 2.7f;
    }

    /**
     * Draw the model
     */
    @Override
    public void draw() {
        super.draw();
    }

    /**
     * The ball must calculate its new position every frame, bleed some velocity ( due to drag )obstacles
     * @param dt delta time in milliseconds
     */
    @Override
    public void update(long dt) {
        super.update(dt);

        //Physics
        // 1 unit of space = 1 centimeter
        // 1 m/s = 100 units/s
        // golf ball size = 42.7 mm = 4.27 units of space
        // golf ball weigth = 46 grams

        //Add the velocity to our position
        setPosition(velocity.mul((float)dt/1000, new Vector3f()).add(getPosition()));
        velocity.mul(0.97f);

    }

    /**
     *
     * @return the velocity vector
     */
    public Vector3f getVelocity() {
        return velocity;
    }

    /**
     *
     * @param velocity the velocity vector
     */
    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    /**
     *
     * @return whether the entity is moving
     */
    public boolean isMoving(){
        return velocity.lengthSquared() > 1f;
    }

    /**
     * Check whether the
     * @param wall the wall to test against
     * @return how many intersections were detected [0..2]
     */
    public int wallIntersection(EntityWall wall){
        //Coordinates relative to the center of the balls
        Vector3f lineStart = wall.getPosition().sub(getPosition(), new Vector3f());
        Vector3f lineEnd = wall.getEndPosition().sub(getPosition(), new Vector3f());
        Vector3f lineVector = lineEnd.sub(lineStart, new Vector3f());

        double det = lineStart.x * lineEnd.y - lineEnd.x * lineStart.y;

        double dLen = lineVector.lengthSquared();

        double discriminant = radius * radius * dLen - det * det;


        if(discriminant < 0){
            return 0;
        }else{
            bounceOff(wall);
            return 1;
        }

    }

    /**
     *
     * @param wall
     */
    public void bounceOff(EntityWall wall){
        //Compute the angle between the wall's normal and the velocity vector
        float alpha = velocity.angle(wall.getNormal());
        //There's probably a more elegant way to rotate a vector, but since this code doesn't run often matrix transformation will do
        Matrix3f rotation = new Matrix3f().rotationZ((float)Math.PI - alpha);

        velocity.mul(rotation);
    }
}
