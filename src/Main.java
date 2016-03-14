import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.glfw.GLFW;
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

import static org.lwjgl.system.MemoryUtil.*;


public class Main{
    //Callbacks
    static private GLFWErrorCallback errorCallback;

    //Window handle and information about the window
    static private long window;
    static private int WIDTH = 1280;
    static private int HEIGHT = 720;
    //The shader program
    static private ShaderProgram shaderProgram;
    //Transformation matrices
    static Matrix4f viewMatrix = new Matrix4f();
    static Matrix4f projectionMatrix = new Matrix4f();

    /**
     * main
     * @param args
     */
    public static void main(String[] args) {
        try{
            init();
            loop();
            glfwDestroyWindow(window);
        } finally {
            glfwTerminate();
        }
    }





    /**
     * Initialize GLFW
     */
    private static void init() {
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        if ( glfwInit() != GLFW_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");


        //OSX by default will run OpenGL2.1, which only supports version 120 shaders
        //By setting OpenGL to 3.3, version 330 shaders will work
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable


        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");



        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        // Center our window
        glfwSetWindowPos(
                window,
                (vidmode.width() - WIDTH) / 2,
                (vidmode.height() - HEIGHT) / 2
        );

        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        GL.createCapabilities();

        //glEnableClientState(GL_VERTEX_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
    }

    private static void loop() {
        // Set the clear color
        glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));
        //Load the default frag and vert shaders
        shaderProgram = new ShaderProgram("vertshader", "fragshader");
        shaderProgram.loadUniform("model_matrix");
        shaderProgram.loadUniform("view_matrix");
        shaderProgram.loadUniform("projection_matrix");

        projectionMatrix.setPerspective((float)Math.toRadians(90), WIDTH/HEIGHT, 0.1f, 100);
        viewMatrix.setLookAt(
                1f, 0f, 5f,
                0, 0, 0,
                0, 0, 1);

        //Because for
        shaderProgram.setUniformMatrix4f("model_matrix", new Matrix4f());
        shaderProgram.setUniformMatrix4f("view_matrix", viewMatrix);
        shaderProgram.setUniformMatrix4f("projection_matrix", projectionMatrix);
        EntityTerrain terr = new EntityTerrain();

        // Run the rendering loop until the user has attempted to close
        while ( glfwWindowShouldClose(window) == GLFW_FALSE ) {
            //Clear the screen of previous draw calls
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            shaderProgram.bind();







            //Draw everything onto the screen
            glfwSwapBuffers(window);

            //Check for any events ( KB/mouse)
            glfwPollEvents();

        }
    }

}