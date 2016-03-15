import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Created by akateiva on 15/03/16.
 */
public class EntityPlainDrawable extends Entity{


    int vao_id;
    int vbo_id;
    int color_uniform; // the pointer to the color vector uniform in the plain_color shader
    int model_uniform; // the pointer to model matrix transformation in the plain_color shader

    EntityPlainDrawable() {
        super();

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

        model_uniform = Main.getShaderManager().getShaderUniform("plain_color", "model");
        color_uniform = Main.getShaderManager().getShaderUniform("plain_color", "color");

        setPosition(new Vector3f());
    }

    @Override
    public void setPosition(Vector3f position) {
        super.setPosition(position);
    }


    @Override
    public void setAngle(Vector3f angle) {
        super.setAngle(angle);
    }

    @Override
    public void update(long dt) {

    }

    @Override
    public void draw() {
        //Tell OpenGL that for drawing this object we will be using the "plain_color" shader
        Main.getShaderManager().bind("plain_color");

        //Transform the vertex positions from model space to world space using the "model" transformation
        Matrix4f model_transformation = new Matrix4f().translate(position);
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        model_transformation.get(fb);
        glUniformMatrix4fv(model_uniform, false,fb);

        //Tell the shader what color we want the object to be
        fb = BufferUtils.createFloatBuffer(4);
        float[] color = {0.0f, 1.0f, 0.0f, 1.0f};
        fb.put(color).flip();
        glUniform4fv(Main.getShaderManager().getShaderUniform("plain_color", "color"), fb);

        //Tell OpenGL that we are going to draw the vao_id vertex array
        glBindVertexArray(vao_id);
        glEnableVertexAttribArray(0);

        // Draw the vertices from our buffer
        glDrawArrays(GL_TRIANGLES, 0, 6);

        // Restore state
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }
}
