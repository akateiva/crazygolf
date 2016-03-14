import org.lwjgl.*;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.joml.*;

import java.nio.FloatBuffer;
import java.util.Hashtable;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main{
    //Callbacks
    static private GLFWErrorCallback errorCallback;


    //Window handle and information about the window
    static private long window;
    static private int WIDTH = 1280;
    static private int HEIGHT = 720;

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
     * Draw a cube on the screen at a position.
     * @param x
     * @param y
     * @param z
     */
    private static void drawCube(float x, float y, float z){
        //Save the current matrix, because we will be translating the cubes
        glPushMatrix();
        glTranslatef(x + 0.5f, y + 0.5f, z + 0.5f);

        glBegin(GL_QUADS);
        glVertex3f( 0.5f, 0.5f,-0.5f);
        glVertex3f(-0.5f, 0.5f,-0.5f);
        glVertex3f(-0.5f, 0.5f, 0.5f);
        glVertex3f( 0.5f, 0.5f, 0.5f);
        glVertex3f( 0.5f,-0.5f, 0.5f);
        glVertex3f(-0.5f,-0.5f, 0.5f);
        glVertex3f(-0.5f,-0.5f,-0.5f);
        glVertex3f( 0.5f,-0.5f,-0.5f);
        glVertex3f( 0.5f, 0.5f, 0.5f);
        glVertex3f(-0.5f, 0.5f, 0.5f);
        glVertex3f(-0.5f,-0.5f, 0.5f);
        glVertex3f( 0.5f,-0.5f, 0.5f);
        glVertex3f( 0.5f,-0.5f,-0.5f);
        glVertex3f(-0.5f,-0.5f,-0.5f);
        glVertex3f(-0.5f, 0.5f,-0.5f);
        glVertex3f( 0.5f, 0.5f,-0.5f);
        glVertex3f(-0.5f, 0.5f, 0.5f);
        glVertex3f(-0.5f, 0.5f,-0.5f);
        glVertex3f(-0.5f,-0.5f,-0.5f);
        glVertex3f(-0.5f,-0.5f, 0.5f);
        glVertex3f( 0.5f, 0.5f,-0.5f);
        glVertex3f( 0.5f, 0.5f, 0.5f);
        glVertex3f( 0.5f,-0.5f, 0.5f);
        glVertex3f( 0.5f,-0.5f,-0.5f);
        glEnd();

        //Restore the matrix
        glPopMatrix();
    }

    /**
     * Draw the floor grid
     */
    private static void drawFloorGrid(){
        glBegin(GL_LINES);
        for(int i=-20;i<=20;i++) {
            glVertex3f(i,-20,0);
            glVertex3f(i,20,0);
            glVertex3f(-20,i,0);
            glVertex3f(20,i,0);
        };
        glEnd();
    }

    /**
     * Initialize GLFW
     */
    private static void init() {
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        if ( glfwInit() != GLFW_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");


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

    }

    /**
     * Window3DView thread loop
     */
    private static void loop() {
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        // Set the clear color
        glClearColor(0.6f, 0.6f, 0.6f, 0.0f);

        //Load the default frag and vert shaders
        shaderProgram = new ShaderProgram("vertshader", "fragshader");

        //Get the location of uniform "color" in the shader
        int colorUniform = GL20.glGetUniformLocation(shaderProgram.getID(), "color");

        // Run the rendering loop until the user has attempted to close
        while ( glfwWindowShouldClose(window) == GLFW_FALSE ) {
            shaderProgram.bind();



            FloatBuffer fb = BufferUtils.createFloatBuffer(16);
            projectionMatrix.setPerspective((float)Math.toRadians(90), WIDTH/HEIGHT, 0.1f, 100).get(fb);
            glMatrixMode(GL_PROJECTION);
            glLoadMatrixf(fb);

            viewMatrix.setLookAt(
                    10f, 0f, 5f,
                    0, 0, 0,
                    0, 0, 1).get(fb);

            glMatrixMode(GL_MODELVIEW);
            glLoadMatrixf(fb);

            //Clear the screen of previous draw calls
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            drawFloorGrid();



            //Draw everything onto the screen
            glfwSwapBuffers(window);

            //Check for any events ( KB/mouse)
            glfwPollEvents();

        }
    }

}