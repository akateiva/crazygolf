import org.joml.Vector3f;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL20.*;
import java.nio.FloatBuffer;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by akateiva on 13/03/16.
 */
public class GameStateGame extends GameState {
    //An array which holds players balls
    EntityBall playerballs[];
    //Which player's turn is it now
    int turn_player;

    //
    float aimOffset;

    //whether we are waiting for the ball to finish moving or waiting for the user to decide what he's gonna be doing with his balls :)
    boolean waiting_for_input;


    //Testshit is now an EntityBall that is used to visualise where the hole is
    EntityBall testshit;
    EntityTerrain terrain;

    //The position at which a ball is placed in the start
    Vector3f start_position;
    //The position of the hole
    Vector3f hole_position;

    GameStateGame(int n_players, String course) {
        //GRAPHICS INITIALIZATION FOR THE GAME STATE

        //Set up the plain_color shader uniforms ( projection matrixr )
        Main.getShaderManager().bind("plain_color");
        int projectionUniform = Main.getShaderManager().getShaderUniform("plain_color", "projection");

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        //Create a perspective matrix and move the data into the float buffer
        Matrix4f matrix = new Matrix4f().perspective((float) Math.toRadians(90.0f), (float)Main.getWIDTH()/Main.getHEIGHT(), 0.1f, 1000f);
        matrix.get(fb);
        glUniformMatrix4fv(projectionUniform, false, fb);

        //GAME INITIALIZATION

        terrain = new EntityTerrain();
        terrain.setColor(0.1f, 1.0f, 0.1f, 1.0f);

        start_position = new Vector3f(0, 0, 0);
        hole_position = new Vector3f(100, 0, 0);

        //Create an array of player balls
        playerballs = new EntityBall[n_players];

        //Create balls for all players
        for(int i = 0; i < n_players; i++){
            playerballs[i] = new EntityBall();
            playerballs[i].setPosition(start_position);
            playerballs[i].setVisible(false);
        }

        testshit = new EntityBall();
        testshit.setPosition(hole_position);
        testshit.setColor(0,0,0,1);

        //Set that its now the first (0th) players turn
        setTurn(0);

        //setCameraLookAt(new Vector3f(200, 200, 200), new Vector3f(0,0,0));
    }

    /**
     * Set the current turn. It will also make the players ball visible and adjust the camera.
     * @param player whose turn is it
     */
    void setTurn(int player){
        //Make the player's whose turn is it now ball visible
        playerballs[player].setVisible(true);
        waiting_for_input = true;

        //Make the camera look from the current position of the ball at the hole
        Vector3f camera_direction = hole_position.sub(playerballs[player].getPosition(), new Vector3f()).normalize();
        setCameraLookAt(camera_direction.mul(-100).add(playerballs[player].getPosition()).add(0,0,200) ,hole_position);
    }

    /**
     * Changes the camera positions
     * @param eyePosition the position of the camera
     * @param cameraTarget the position of the point you want to look at
     */
    void setCameraLookAt(Vector3f eyePosition, Vector3f cameraTarget){
        //Set up a view matrix and move it into memory
        Main.getShaderManager().bind("plain_color");
        Matrix4f matrix = new Matrix4f().setLookAt(eyePosition, cameraTarget, new Vector3f(0,0,1));
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        matrix.get(fb);
        int viewUniform = Main.getShaderManager().getShaderUniform("plain_color", "view");
        glUniformMatrix4fv(viewUniform, false, fb);

    }

    /**
     * This gets called any time a keyboard event is detected
     * @param key the ID of the key pressed
     * @param scancode something we dont really care about
     * @param action whether the key has been pressed or released
     * @param mods modifier keys
     */
    @Override
    void keyEvent(int key, int scancode, int action, int mods) {
        System.out.println(key);
        if(key == GLFW_KEY_SPACE && action == GLFW_RELEASE){
            if(waiting_for_input){
                playerballs[turn_player].setVelocity(new Vector3f(200, 0, 0 ));
                waiting_for_input = false;
            }
        }
    }

    /**
     * Any GameState relating logic will be called from this method.
     *
     * @param dt the time in milliseconds since last update call
     */
    @Override
    void update(long dt) {

        if(!waiting_for_input){
            playerballs[turn_player].update(dt);
            if(!playerballs[turn_player].isMoving()){
                // if the ball has stopped moving, we can let the other player take his turn now
                setTurn(0);
            }
        }
    }

    /**
     * Any draw calls should be executed in this method
     */
    @Override
    void draw() {
        //Draw all the balls
        for(int i = 0; i < playerballs.length; i++){
            playerballs[i].draw();
        }
        testshit.draw();
        terrain.draw();
    }
}
