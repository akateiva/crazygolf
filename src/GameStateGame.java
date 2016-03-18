import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;



/**
 * Created by akateiva on 13/03/16.
 */
public class GameStateGame extends GameState {
    //An array which holds the players balls
    ArrayList<EntityBall> players = new ArrayList<>();

    //Which player's turn is it now
    int turnPlayer = 0;


    ArrayList<EntityWall> obstacles = new ArrayList<>();


    //Keyboard data
    //Because the events are fired only on the instances of key presses or releases, we need to know whether a release event event has occured after a press already or not
    boolean spaceKeyHeld = false;
    boolean leftKeyHeld = false;
    boolean rightKeyHeld = false;

    //Mouse position in the world
    Vector3f mouseStart = new Vector3f();

    //The vector of the current aim. It is not normalized and is length indicates the distance between the balls and holes
    Vector3f aimVector;

    //whether we are waiting for the ball to finish moving or waiting for the user to decide what he's gonna be doing with his balls :)
    boolean waitForPlayerInput;


    //Testshit is now an EntityBall that is used to visualise where the hole is
    EntityBall hole;
    EntityTerrain terrain;

    //The position at which a ball is placed in the start
    Vector3f startPosition;
    //The position of the hole
    Vector3f endPosition;


    //Transformation matrices
    Matrix4f projectionMatrix;
    Matrix4f viewMatrix;

    /**
     * Start the game
     * @param n_players number of players
     * @param course_path path to the course file
     */
    GameStateGame(int n_players, String course_path) {
        //GRAPHICS INITIALIZATION FOR THE GAME STATE

        //Set up the plain_color shader uniforms ( projection matrix )
        Main.getShaderManager().bind("plain_color");
        int projectionUniform = Main.getShaderManager().getShaderUniform("plain_color", "projection");

        //Calculate the perspective matrix
        projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(90.0f), (float)Main.getWindowWidth()/Main.getWindowHeight(), 10f, 1000f);

        //Create a FloatBuffer from the projection matrix and move it to video memory
        glUniformMatrix4fv(projectionUniform, false, projectionMatrix.get(BufferUtils.createFloatBuffer(16)));

        //GAME INITIALIZATION

        terrain = new EntityTerrain();
        terrain.setColor(0.1f, 1.0f, 0.1f, 1.0f);

        //Load the course
        obstacles = new ArrayList<>();
        String course_file = Util.resourceToString(course_path);
        Scanner scanner = new Scanner(course_file);

        testpoint = new ArrayList<>();

        while(scanner.hasNextLine()) {
            String curLine = scanner.nextLine();
            String parts[] = curLine.split(" ");


            //Note:
            //The coordinates from the level editor are in the range of [0..31], while the world coordinates range from [0..310], thats why multiplication by ten is used
            switch (parts[0].toLowerCase()) {
                case "n":
                    if(parts[1].equals(parts[3]) && parts[2].equals(parts[4]))
                        break;
                    obstacles.add(new EntityWall(
                            new Vector3f(Float.parseFloat(parts[1])*10.0f, Float.parseFloat(parts[2])*10.0f, 0),
                            new Vector3f(Float.parseFloat(parts[3])*10.0f, Float.parseFloat(parts[4])*10.0f, 0)));
                    break;
                case "s":
                    startPosition = new Vector3f(Float.parseFloat(parts[1])*10.0f, Float.parseFloat(parts[2])*10.0f, 0);
                    break;
                case "e":
                    endPosition = new Vector3f(Float.parseFloat(parts[1])*10.0f, Float.parseFloat(parts[2])*10.0f, 0);
                    break;
            }
        }
        if(startPosition == null || endPosition == null){
            throw(new RuntimeException("Course does not have a start/end."));
        }
        hole = new EntityBall();
        hole.setPosition(endPosition);
        hole.setColor(0,0,0,1);


        //Create balls for all players
        for(int i = 0; i < n_players; i++){
            players.add(new EntityBall());
            players.get(i).setPosition(startPosition);
            players.get(i).setVisible(false);
        }

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
        players.get(player).setVisible(true);
        waitForPlayerInput = true;

        //Make the camera look from the current position of the ball at the hole
        Vector3f camera_direction = endPosition.sub(players.get(player).getPosition(), new Vector3f()).normalize();
        setCameraLookAt(camera_direction.mul(-100).add(players.get(player).getPosition()).add(0,0,200) , endPosition);
    }

    /**
     * Will set the camera to look in the aim vector of the player
     */
    void updateAimCamera(){
        Vector3f cameraPosition;
        Vector3f cameraTarget;

        //projectionMatrix.unproject()

    }

    /**
     * Changes the camera positions instantly
     * @param eyePosition the position of the camera
     * @param cameraTarget the position of the point you want to look at
     */
    void setCameraLookAt(Vector3f eyePosition, Vector3f cameraTarget){
        //Set up a view matrix and move it into memory
        Main.getShaderManager().bind("plain_color");
        viewMatrix = new Matrix4f().setLookAt(eyePosition, cameraTarget, new Vector3f(0,0,1));
        int viewUniform = Main.getShaderManager().getShaderUniform("plain_color", "view");
        glUniformMatrix4fv(viewUniform, false, viewMatrix.get(BufferUtils.createFloatBuffer(16)));

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

    }

    /**
     * Calculates the current position of the mouse in world space
     * @return
     */
    Vector3f mouseToWorld(){
        //Retrieve the mouse X and Y position
        //These coordinates are pixel distances relative to the top left corner of the screen
        float mouseX = Main.getMouseX();
        //Because OpenGL origin is at the bottom left, we have to adjust the pixel coordinates to account for it
        float mouseY = Main.getWindowHeight() - Main.getMouseY();

        //By unprojecting the mouse coordinates, we get a ray from the close clip plane to the far clip plane
        //In order to get our position on the terrain plane, we have to find out the depth from the depth buffer
        FloatBuffer screenDepth = BufferUtils.createFloatBuffer(1);
        glReadPixels((int)mouseX, (int)mouseY, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, screenDepth);

        Matrix4f pvmatrix = projectionMatrix.mul(viewMatrix, new Matrix4f());
        Vector3f worldpos = new Vector3f();
        pvmatrix.unproject(mouseX, mouseY, screenDepth.get(), Main.getViewport(), worldpos);

        return worldpos;
    }

    /**
     * Whenever a mouse event occurs this gets called.
     * @param button The GLFW ID of the key
     * @param action The GLFW ID of the action
     * @param mods The GLFW ID of the modifier
     */
    @Override
    void mouseEvent(int button, int action, int mods) {
        if(button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS && waitForPlayerInput){
            mouseStart = mouseToWorld();
        }
        if(button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE && waitForPlayerInput){
            Vector3f mouseEnd = mouseToWorld();
            Vector3f putVector = mouseStart.sub(mouseEnd, new Vector3f());

            //Due to depth buffer imprecision Z might be +- 0, so just clamp it to 0
            putVector.z = 0;

            players.get(turnPlayer).setVelocity(putVector);

            waitForPlayerInput = false;

        }
    }

    /**
     * Any GameState logic takes place here.
     *
     * @param dt the time in milliseconds since last update call
     */
    @Override
    void update(long dt) {

        if(!waitForPlayerInput){
            players.get(turnPlayer).update(dt);

            for(int i = 0; i < obstacles.size(); i++){
                players.get(turnPlayer).wallIntersection(obstacles.get(i));
            }


            if(!players.get(turnPlayer).isMoving()){
                // if the ball has stopped moving, we can let the other player take his turn now
                if(turnPlayer + 1 >= players.size()){
                    //If it was the last player's turn, its now the first players turn
                    setTurn(0);
                }else{
                    //Else just increment
                    setTurn(turnPlayer + 1);
                }

            }
        }
    }

    /**
     * This method gets invoked every frame something has to be drawn on screen
     */
    @Override
    void draw() {
        //Draw all the balls
        for(int i = 0; i < players.size(); i++){
            players.get(i).draw();
        }
        //Draw all the obsacles
        for(int i = 0 ; i < obstacles.size(); i++){
            obstacles.get(i).draw();
        }

        hole.draw();
        //Draw the terrain
        terrain.draw();
    }
}
