/**
 * Created by akateiva on 15/03/16.
 */

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class EntityPothole extends Entity {
    int vao_id;
    int vbo_id;

    EntityPothole() {
        float[] vertices = {
                0.25f,0f,3f,
                0.25f,0f,3f,
                0.25f,0f,3f,
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

            // Draw the vertices
            glDrawArrays(GL_TRIANGLES, 0, 2*3);

            // Restore state
            glDisableVertexAttribArray(0);
            glBindVertexArray(0);
    }
}
