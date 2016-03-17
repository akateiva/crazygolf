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
    //An array which holds the players balls
    EntityBall players[];
    //Which player's turn is it now
    int turnPlayer = 0;

    //Keyboard data
    //Because the events are fired only on the instances of key presses or releases, we need to know whether a release event event has occured after a press already or not
    boolean spaceKeyHeld = false;
    boolean leftKeyHeld = false;
    boolean rightKeyHeld = false;

    //The vector of the current aim. It is not normalized and is length indicates the distance between the balls and holes
    Vector3f aimVector;

    //whether we are waiting for the ball to finish moving or waiting for the user to decide what he's gonna be doing with his balls :)
    boolean waitForPlayerInput;


    //Testshit is now an EntityBall that is used to visualise where the hole is
    EntityBall testshit;
    EntityTerrain terrain;

    //The position at which a ball is placed in the start
    Vector3f startPosition;
    //The position of the hole
    Vector3f endPosition;

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

        startPosition = new Vector3f(0, 0, 0);
        endPosition = new Vector3f(100, 0, 0);

        //Create an array of player balls
        players = new EntityBall[n_players];

        //Create balls for all players
        for(int i = 0; i < n_players; i++){
            players[i] = new EntityBall();
            players[i].setPosition(startPosition);
            players[i].setVisible(false);
        }

        testshit = new EntityBall();
        testshit.setPosition(endPosition);
        testshit.setColor(0,0,0,1);

        //Set that its now the first (0th) players turn
        setTurn(0);
    }

    /**
     * Set the current turn. It will also make the players ball visible and adjust the camera.
     * @param player whose turn is it
     */
    void setTurn(int player){
        //Make the player's whose turn is it now ball visible
        turnPlayer = player;
        players[player].setVisible(true);
        waitForPlayerInput = true;

        aimVector = endPosition.sub(players[player].getPosition(), new Vector3f());

        //Make the camera look from the current position of the ball at the hole
        Vector3f camera_direction = endPosition.sub(players[player].getPosition(), new Vector3f()).normalize();
        setCameraLookAt(camera_direction.mul(-100).add(players[player].getPosition()).add(0,0,200) , endPosition);
    }

    /**
     * Will set the camera to look in the aim vector of the player
     */
    void updateAimCamera(){
        Vector3f cameraPosition;
        Vector3f cameraTarget;
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
        if(!waitForPlayerInput)
            return;
        if((key == GLFW_KEY_LEFT || key == GLFW_KEY_A)){
            if(action == GLFW_PRESS){
                leftKeyHeld = true;
            }else if(action == GLFW_RELEASE){
                leftKeyHeld = false;
            }
        }
        if((key == GLFW_KEY_RIGHT || key == GLFW_KEY_D)){
            if(action == GLFW_PRESS){
                rightKeyHeld = true;
            }else if(action == GLFW_RELEASE){
                rightKeyHeld = false;
            }
        }

        if(key == GLFW_KEY_SPACE && action == GLFW_RELEASE){
            players[turnPlayer].setVelocity(new Vector3f(200, 0, 0 ));
            waitForPlayerInput = false;
        }
    }

    /**
     * Any GameState relating logic will be called f om this method.
     *
     * @param dt the time in milliseconds since last update call
     */
    @Override
    void update(long dt) {

        if(!waitForPlayerInput){
            System.out.println(players[turnPlayer].isMoving() );
            players[turnPlayer].update(dt);
            if(!players[turnPlayer].isMoving()){
                // if the ball has stopped moving, we can let the other player take his turn now
                if(turnPlayer +1 >= players.length){
                    //If it was the last player's turn, its now the first players turn
                    setTurn(0);
                }else{
                    //Else just increment
                    setTurn(turnPlayer +1);
                }

            }
        }
    }

    /**
     * Any draw calls should be executed in this method
     */
    @Override
    void draw() {
        //Draw all the balls
        for(int i = 0; i < players.length; i++){
            players[i].draw();
        }
        testshit.draw();
        terrain.draw();
    }
}
