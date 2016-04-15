/**
 * Created by akateiva on 11/04/16.
 */

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.LinkedList;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


/**
 * Scene manager manages all the entities in the scene, updates them, draws them
 */
public class SceneManager {
    private int vao_id;
    private LinkedList<Entity> entities;

    SceneManager(){
        entities = new LinkedList<>();

        //Initialize the VAO
        vao_id = glGenVertexArrays();
        glBindVertexArray(vao_id);
    }

    /**
     * @param entity the entity to be added to the scene
     */
    public void add(Entity entity){
        entities.add(entity);
    }

    /**
     * @param entity the entity to be removed from the scene
     */
    public void remove(Entity entity){
        entities.remove(entity);
    }


    /**
     * Invoked by the GameState for any logic/physics updates in the scene
     * @param dt
     */
    public void update(long dt){

    }

    /**
     * Invoked by the GameState to render all scene objects
     */
    public void draw(){
        glBindVertexArray(vao_id);
        Main.getShaderManager().bind("texturedModel");

        //floatbuffer for moving matrix data
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);


        //Iterate through our entities
        Iterator<Entity> itr = entities.iterator();
        while(itr.hasNext()){
            Entity i = itr.next();
            //Apply the model transformation matrix and send it to the shader
            Matrix4f model_transformation = new Matrix4f()
                    .rotate(i.getAngle(), 0, 0, 1.0f)
                    .translate(i.getPosition());
            model_transformation.get(fb);
            glUniformMatrix4fv(Main.getShaderManager().getShaderUniform("texturedModel", "model"), false,fb);
            //Render the object
            i.draw();
        }


    }
}
