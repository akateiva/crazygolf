import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteShader;

/**
 * Created by akateiva on 15/03/16.
 */
public class ShaderManager {
    //Inner class for storing the shader program ID and the ID's of uniforms in one place;
    class ShaderData {
        Map<String, Integer> uniforms;
        int program_id;

        ShaderData(int program_id) {
            uniforms = new HashMap<>();
            this.program_id = program_id;
        }

        public void createUniform(String uniformName){
            uniforms.put(uniformName, glGetUniformLocation(program_id, uniformName));
        }
        public int getUniform(String uniformName){
            return uniforms.get(uniformName);
        }
    }


    Map<String, ShaderData> shader_programs;



    ShaderManager(){
        shader_programs = new HashMap<>();
    }

    /**
     * Create a shader program
     * @param programName  the name that the program will be saved as
     * @param vertex_source the source of the vertex shader in a string
     * @param fragment_source the source of the fragment shader in a string
     * @return the id of the new shader program
     */
    public int createShader(String programName, String vertex_source, String fragment_source){
        System.out.println("Creating shader " + programName);

        //Create a blank shader program
        int program_id = glCreateProgram();

        //Create blank vertex and fragment shaders
        int vertID = glCreateShader(GL_VERTEX_SHADER);
        int fragID = glCreateShader(GL_FRAGMENT_SHADER);

        //Move the source strings into the shaders
        glShaderSource(vertID, vertex_source);
        glShaderSource(fragID, fragment_source);

        //Compile the shaders
        glCompileShader(vertID);
        glCompileShader(fragID);

        //Attach the vertex and fragment shaders to the shader program
        glAttachShader(program_id, vertID);
        glAttachShader(program_id, fragID);

        //Link the program
        glLinkProgram(program_id);

        //If the shader could not link successfully (i.e. one of them failed compiling), throw an exception
        if (glGetProgrami(program_id, GL_LINK_STATUS) != GL_TRUE) {
            throw new RuntimeException(glGetProgramInfoLog(program_id));
        }

        ShaderData shaderData = new ShaderData(program_id);
        //Save the shader id in the hashmap
        shader_programs.put(programName, shaderData);

        //Since the vert and frag shaders have been compiled and linked into a glProgram, we can get rid of them
        glDeleteShader(vertID);
        glDeleteShader(fragID);

        return program_id;
    }

    /**
     * Get the id of a shader program.
     * @param programName
     * @return the id of the requested shader program
     */
    public int getShaderProgram(String programName){
        return shader_programs.get(programName).program_id;
    }

    /**
     * Get the id of a shader uniform and store it in a hashmap for later use
     * @param programName
     * @param uniformName
     * @return the id of shader uniform
     */
    public void createShaderUniform(String programName, String uniformName){
        shader_programs.get(programName).createUniform(uniformName);
    }

    /**
     * Fetch the already created shader uniform id
     * @param programName
     * @param uniformName
     * @return the id of shader uniform
     */
    public int getShaderUniform(String programName, String uniformName){
        return shader_programs.get(programName).getUniform(uniformName);
    }


}
