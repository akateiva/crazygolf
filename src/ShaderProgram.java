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
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.ARBSeamlessCubeMap.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * A helper class that stores the shader program, vertex shader and fragment shader in one place.
 */
public class ShaderProgram {
    private int programID;
    private Map<String, Integer> uniforms;



    /**
     * Construct a shader program with given vertex and fragment shaders
     * @param vertPath
     * @param fragPath
     */
    ShaderProgram(String vertPath, String fragPath){
        programID = glCreateProgram();
        String vertSource = Util.resourceToString(vertPath);
        String fragSource = Util.resourceToString(fragPath);
        int vertID = glCreateShader(GL_VERTEX_SHADER);
        int fragID = glCreateShader(GL_FRAGMENT_SHADER);


        glShaderSource(vertID, vertSource);
        glShaderSource(fragID, fragSource);

        glCompileShader(vertID);
        glCompileShader(fragID);

        //TODO : Remove these debugging statements
        String log = glGetShaderInfoLog(vertID);
        String log2 = glGetShaderInfoLog(fragID);

        glAttachShader(programID, vertID);
        glAttachShader(programID, fragID);
        glLinkProgram(programID);

        //If the shader could not link successfully, stop
        if (glGetProgrami(programID, GL_LINK_STATUS) != GL_TRUE) {
            throw new RuntimeException(glGetProgramInfoLog(programID));
        }

        //Instantiate a map for the uniform names and ID's
        uniforms = new HashMap<>();

        //Since the vert and frag shaders have been compiled and linked into a glProgram, we can get rid of them
        glDeleteShader(vertID);
        glDeleteShader(fragID);
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

    /**
     * Store the id of the named uniform in a Map
     * Be careful! If a uniform is not found, 0 will be stored
     * @param uniformName
     */

    public void loadUniform(String uniformName) {
        uniforms.put(uniformName, glGetUniformLocation(programID, uniformName));
    }

    /**
     * A method to set a Matrix4f uniform's value.
     * @param uniformName
     * @param uniform
     */
    public void setUniformMatrix4f(String uniformName, Matrix4f uniform) {
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        uniform.get(fb);
        glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
    }
}