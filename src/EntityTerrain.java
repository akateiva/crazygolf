import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Created by akateiva on 14/03/16.
 */
public class EntityTerrain extends Entity {
    int vao_id;
    int vbo_id;

    EntityTerrain() {
        float[] vertices = {
                // Left bottom triangle
                -10f, 10f, 0f,
                -10f, -10f, 0f,
                10f, -10f, 0f,
                // Right top triangle
                10f, -10f, 0f,
                10f, 10f, 0f,
                -10f, 10f, 0f
        };

        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices).flip();

        vao_id = glGenVertexArrays();
        glBindVertexArray(vao_id);

        vbo_id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_id);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    @Override
    public void update(long dt) {

    }

    @Override
    public void draw() {
        Main.getShaderManager().bind("plain_color");
        glBindVertexArray(vao_id);
        glEnableVertexAttribArray(0);

        FloatBuffer fb = BufferUtils.createFloatBuffer(4);
        float[] color = {0.0f, 1.0f, 0.0f, 1.0f};
        fb.put(color).flip();
        glUniform4fv(Main.getShaderManager().getShaderUniform("plain_color", "color"), fb);

        // Draw the vertices
        glDrawArrays(GL_TRIANGLES, 0, 6);

        // Restore state
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }
}
