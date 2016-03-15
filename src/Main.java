import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Main {
    //Callbacks
    static private GLFWErrorCallback errorCallback;

    //Window handle and information about the window
    static private long window;
    static private int WIDTH = 1280;
    static private int HEIGHT = 720;

    //Managers
    static private ShaderManager shaderManager;

    static private GameState activeGameState;

    static private long variableYieldTime, lastTime;

    /**
     * An accurate sync method that adapts automatically
     * to the system it runs on to provide reliable results.
     *
     * @param fps The desired frame rate, in frames per second
     * @author kappa (On the LWJGL Forums)
     */
    static private void sync(int fps) {
        if (fps <= 0) return;

        long sleepTime = 1000000000 / fps; // nanoseconds to sleep this frame
        // yieldTime + remainder micro & nano seconds if smaller than sleepTime
        long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000*1000));
        long overSleep = 0; // time the sync goes over by

        try {
            while (true) {
                long t = System.nanoTime() - lastTime;

                if (t < sleepTime - yieldTime) {
                    Thread.sleep(1);
                }else if (t < sleepTime) {
                    // burn the last few CPU cycles to ensure accuracy
                    Thread.yield();
                }else {
                    overSleep = t - sleepTime;
                    break; // exit while loop
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            lastTime = System.nanoTime() - Math.min(overSleep, sleepTime);

            // auto tune the time sync should yield
            if (overSleep > variableYieldTime) {
                // increase by 200 microseconds (1/5 a ms)
                variableYieldTime = Math.min(variableYieldTime + 200*1000, sleepTime);
            }
            else if (overSleep < variableYieldTime - 200*1000) {
                // decrease by 2 microseconds
                variableYieldTime = Math.max(variableYieldTime - 2*1000, 0);
            }
        }
    }


    /**
     * main
     * @param args
     */
    public static void main(String[] args) {
        try {
            init();
            loadResources();
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

        if (glfwInit() != GLFW_TRUE)
            throw new IllegalStateException("Unable to initialize GLFW");


        //OSX by default will run OpenGL2.1, which only supports version 120 shaders
        //By setting OpenGL to 3.3, version 330 shaders will work
        //(but El Capitan completely disregards this and we end up with version 4.1)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable


        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "", NULL, NULL);
        if (window == NULL)
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

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        //Background color
        glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
    }

    private static void loadResources(){
        //Instantiate the ShaderManager
        shaderManager = new ShaderManager();

        //Create a shader program from the files "vertshader" and "fragshader", then create the uniforms of those shaders ( this must be done before the uniforms are used in any way)
        shaderManager.createShader("plain_color", Util.resourceToString("vertshader"), Util.resourceToString("fragshader"));
        shaderManager.createShaderUniform("plain_color", "projection");
        shaderManager.createShaderUniform("plain_color", "model");
        shaderManager.createShaderUniform("plain_color", "view");
        shaderManager.createShaderUniform("plain_color", "color");
    }

    private static void loop() {
        activeGameState = new GameStateGame();

        System.out.println("OpenGL version: " + glGetString(GL_VERSION));

        // Run the rendering loop until the user has attempted to close
        while (glfwWindowShouldClose(window) == GLFW_FALSE) {
            activeGameState.update(16);
            //Clear the screen of previous draw calls
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            activeGameState.draw();

            //Draw everything onto the screen
            glfwSwapBuffers(window);

            //Check for any events ( KB/mouse)
            glfwPollEvents();

            sync(60);
        }
    }

    public static ShaderManager getShaderManager(){
        return shaderManager;
    }

    public static int getWIDTH() {
        return WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }
}