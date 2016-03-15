import org.joml.Vector3f;
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
    Entity terrain;
    int colorUniform;
    GameStateGame() {
        //GRAPHICS INITIALIZATION FOR THE GAME STATE

        //Set up the plain_color shader uniforms ( model view projection matrix as well as color )
        Main.getShaderManager().bind("plain_color");
        int projectionUniform = Main.getShaderManager().getShaderUniform("plain_color", "projection");
        int viewUniform = Main.getShaderManager().getShaderUniform("plain_color", "view");
        colorUniform = Main.getShaderManager().getShaderUniform("plain_color", "color");

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        //Create a perspective matrix and move the data into the float buffer
        Matrix4f matrix = new Matrix4f().perspective((float) Math.toRadians(90.0f), Main.getWIDTH()/Main.getHEIGHT(), 0.1f, 100f);
        matrix.get(fb);
        glUniformMatrix4fv(projectionUniform, false, fb);

        //Set up a view matrix and move it into memory
        matrix.setLookAt(5.0f, 5.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        matrix.get(fb);
        glUniformMatrix4fv(viewUniform, false, fb);

        // THIS IS SUPER SIMPLE, ISNT IT?

        //1. Create a new entity
        terrain = new EntityTerrain();

        //2. We want the grass to be green, don't we? (R G B A)
        ((EntityTerrain)terrain).setColor(0.2f, 0.8f, 0.2f, 1.0f);
    }

    /**
     * Any GameState relating logic will be called from this method.
     *
     * @param dt the time in milliseconds since last update call
     */
    @Override
    void update(long dt) {
        //3. Even though Terrain does not do any logic in its update() function, we call it anyway
        terrain.update(dt);

        //4. This makes terrain move 0.1 unit on the Y axis every frame
        terrain.setPosition(terrain.getPosition().add(0,0.1f,0));
    }

    /**
     * Any draw calls should be executed in this method
     */
    @Override
    void draw() {
        //5. This makes the terrain get drawn on screen
        terrain.draw();
    }
}
