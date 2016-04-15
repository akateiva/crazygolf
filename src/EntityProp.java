/**
 * Created by akateiva on 11/04/16.
 */

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
public class EntityProp extends Entity {

    //Vertex buffer object
    private int vbo_id;

    //Entity buffer object
    private int ebo_id;


    EntityProp(Model model){
        initializeGraphics();
    }

    private void initializeGraphics(){
        float vertices[] = {
                -0.5f,  0.5f,0, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, // Top-left
                0.5f,  0.5f, 0,0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // Top-right
                0.5f, -0.5f, 0,0.0f, 0.0f, 1.0f, 1.0f, 1.0f, // Bottom-right
                -0.5f, -0.5f,0, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f  // Bottom-left
        };

        int elements[] = {
                0, 1, 2,
                2, 3, 0
        };

        vbo_id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_id);
        glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.createFloatBuffer(vertices.length).put(vertices).flip(), GL_STATIC_DRAW);

        ebo_id = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo_id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) BufferUtils.createIntBuffer(elements.length).put(elements).flip(), GL_STATIC_DRAW);

        //Vertex position attribute
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8*4, 0);

        //Vertex color attribute
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8*4, 3*4);

        //Vertex texture coord attribute
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8*4, 6*4);
        //Throwaway code
        float pixels[] = {
                0.0f, 0.0f, 0.0f,   1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,   0.0f, 0.0f, 0.0f
        };
        int tex;
        tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 2, 2, 0, GL_RGB, GL_FLOAT, (FloatBuffer) BufferUtils.createFloatBuffer(pixels.length).put(pixels).flip());
    }

    @Override
    public void update(long dt) {

    }

    @Override
    public void draw() {
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }

}
