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
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * Created by akateiva on 14/03/16.
 */
public class EntityTerrain extends Entity {
    int vboId;
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
        // Sending data to OpenGL requires the usage of (flipped) byte buffers
                FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
                verticesBuffer.put(vertices);
                verticesBuffer.flip();

                vboId = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, 0);

    }
    @Override
    public void update(long dt) {

    }

    @Override
    public void draw() {
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glVertexPointer(3, GL_FLOAT, 0, 0);
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }
}
