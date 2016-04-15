/**
 * Created by akateiva on 11/04/16.
 */

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
public class EntityProp extends Entity {

    //Vertex buffer object
    private int vbo_id;

    //Entity buffer object
    private int ebo_id;

    private Model model;

    EntityProp(Model model){
        this.model = model;
        initializeGraphics(model);
    }

    private void initializeGraphics(Model model){
        vbo_id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_id);
        glBufferData(GL_ARRAY_BUFFER, model.getVboBuffer(), GL_STATIC_DRAW);

        /*
        ebo_id = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo_id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) BufferUtils.createIntBuffer(elements.length).put(elements).flip(), GL_STATIC_DRAW);
         */

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

        glDrawArrays(GL_TRIANGLES, 0, model.getTriangleCount());
    }

}
