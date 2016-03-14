/**
 * Created by akateiva on 06/01/16.
 */

import java.io.File;

import static org.lwjgl.opengl.GL20.*;

/**
 * A helper class that stores the shader program, vertex shader and fragment shader in one place.
 */
public class ShaderProgram {
    private int programID;
    private int vertID;
    private int fragID;

    /**
     * Construct a shader program with given vertex and fragment shaders
     * @param vertPath
     * @param fragPath
     */
    ShaderProgram(String vertPath, String fragPath){
        programID = glCreateProgram();
        String vertSource = Util.resourceToString(vertPath);
        String fragSource = Util.resourceToString(fragPath);
        vertID = glCreateShader(GL_VERTEX_SHADER);
        fragID = glCreateShader(GL_FRAGMENT_SHADER);


        glShaderSource(vertID, vertSource);
        glShaderSource(fragID, fragSource);

        glCompileShader(vertID);
        glCompileShader(fragID);
        System.out.println(vertSource);

        glAttachShader(programID, vertID);
        glAttachShader(programID, fragID);

        glLinkProgram(programID);
    }

    /**
     * Return the shader program ID
     * @return int id
     */
    public int getID(){
        return programID;
    }

    /**
     * Start using the shader program
     */
    public void bind(){
        glUseProgram(programID);
    }

}