import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

/**
 * Created by akateiva on 11/04/16.
 */
public class GameStateMenu extends GameState{
    SceneManager sceneManager;

    //Transformation matrices
    Matrix4f projectionMatrix;
    Matrix4f viewMatrix;
    int projectionUniform;
    int viewUniform;

    GameStateMenu(){
        //Set up the shaders
        projectionUniform = Main.getShaderManager().getShaderUniform("texturedModel", "projection");
        viewUniform = Main.getShaderManager().getShaderUniform("texturedModel", "view");


        Main.getShaderManager().bind("texturedModel");

        projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(90.0f), (float)Main.getWindowWidth()/Main.getWindowHeight(), 0.1f, 100f);
        glUniformMatrix4fv(projectionUniform, false, projectionMatrix.get(BufferUtils.createFloatBuffer(16)));

        viewMatrix = new Matrix4f().setLookAt(10.0f, 0.0f, 10.0f, 0.f, 0.f, 0.f, 0.f, 0.f, 1.f);
        glUniformMatrix4fv(viewUniform, false, viewMatrix.get(BufferUtils.createFloatBuffer(16)));


        sceneManager = new SceneManager();
        sceneManager.add(new EntityProp(new Model(Util.resourceToString("res/models/golfball.obj"))));
        //two.setPosition(new Vector3f(0,1,0));
        //sceneManager.add(two);



    }

    @Override
    void update(long dt) {

    }

    @Override
    void draw() {
        sceneManager.draw();
    }

    @Override
    void keyEvent(int key, int scancode, int action, int mods) {

    }

    @Override
    void mouseEvent(int button, int action, int mods) {

    }
}
