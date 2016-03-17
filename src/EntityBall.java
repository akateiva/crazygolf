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
        radius = 4.27f;
    }

    /**
     * Draw the model
     */
    @Override
    public void draw() {
        super.draw();
    }

    /**
     * The ball must calculate its new position every frame, bleed some velocity ( due to drag ) and check for collisions with any obstacles
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
        if(velocity.lengthSquared() > 1f){
            return true;

        }
        return false;
    }
}
