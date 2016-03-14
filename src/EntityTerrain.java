import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.libffi.Closure;
import org.joml.FrustumIntersection;
import org.joml.GeometryUtils;
import org.joml.Intersectiond;
import org.joml.Intersectionf;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBSeamlessCubeMap.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
/**
 * Created by akateiva on 14/03/16.
 */
public class EntityTerrain extends Entity {
    int vao_id;
    int vbo_id;
            EntityTerrain(){
                float[] vertices = {
                        // Left bottom triangle
                        -0.5f, 0.5f, 0f,
                        -0.5f, -0.5f, 0f,
                        0.5f, -0.5f, 0f,
                        // Right top triangle
                        0.5f, -0.5f, 0f,
                        0.5f, 0.5f, 0f,
                        -0.5f, 0.5f, 0f
                };

                //The vertices are going to be put in a VAO ( Vertex Array Object ) first
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
        glBindVertexArray(vao_id);
        glEnableVertexAttribArray(0);

        // Draw the vertices
        glDrawArrays(GL_TRIANGLES, 0, 6);

        // Restore state
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }
}
