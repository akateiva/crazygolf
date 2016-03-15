import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import java.nio.FloatBuffer;

/**
 * Created by akateiva on 13/03/16.
 */
public class GameStateGame extends GameState {
    EntityTerrain terr;
    EntityPothole pot;
    int colorUniform;
    GameStateGame() {
        //Set up the plain_color shader uniforms ( model view projection matrix as well as color )
        Main.getShaderManager().bind("plain_color");
        int projectionUniform = Main.getShaderManager().getShaderUniform("plain_color", "projection");
        int modelviewUniform = Main.getShaderManager().getShaderUniform("plain_color", "modelview");
        colorUniform = Main.getShaderManager().getShaderUniform("plain_color", "color");

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        //Create a perspective matrix and move the data into the float buffer
        Matrix4f matrix = new Matrix4f().perspective((float) Math.toRadians(90.0f), Main.getWIDTH()/Main.getHEIGHT(), 0.1f, 100f);
        matrix.get(fb);
        glUniformMatrix4fv(projectionUniform, false, fb);

        matrix.setLookAt(5.0f, 5.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        matrix.get(fb);
        glUniformMatrix4fv(modelviewUniform, false, fb);
        terr = new EntityTerrain();
        pot = new EntityPothole();
    }

    /**
     * Any GameState relating logic will be called from this method.
     *
     * @param dt the time in milliseconds since last update call
     */
    @Override
    void update(long dt) {
        terr.update(dt);
    }

    /**
     * Any draw calls should be executed in this method
     */
    @Override
    void draw() {
        //We will be using a float buffer to move the matrix data to the GPU

        terr.draw();


    }
}
