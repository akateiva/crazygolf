import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Main {
    //Callbacks
    static private GLFWErrorCallback errorCallback;
    static private GLFWKeyCallback keyCallback;
    static private GLFWMouseButtonCallback mouseCallback;
    static private GLFWCursorPosCallback cursorPosCallback;

    //Window handle and information about the window
    static private long window;
    static private int windowWidth = 1280;
    static private int windowHeight = 720;

    static private float mouseX = 0;
    static private float mouseY = 0;

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
            loop(args);
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
        window = glfwCreateWindow(windowWidth, windowHeight, "", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        //Set the key callback
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                activeGameState.keyEvent(key, scancode, action, mods);
            }
        });

        glfwSetMouseButtonCallback(window, mouseCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                activeGameState.mouseEvent(button, action, mods);
            }
        });

        glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                mouseX = (float)xpos;
                mouseY = (float)ypos;
            }
        });
        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        // Center our window
        glfwSetWindowPos(
                window,
                (vidmode.width() - windowWidth) / 2,
                (vidmode.height() - windowHeight) / 2
        );

        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_CULL_FACE);
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
    }

    private static void loadResources(){
        //Instantiate the ShaderManager
        shaderManager = new ShaderManager();

        //Create a shader program from the files "texturedModel.vs" and "texturedModel.fs", then create the uniforms of those shaders ( this must be done before the uniforms are used in any way)
        shaderManager.createShader("texturedModel", Util.resourceToString("res/shader/texturedModel.vs"), Util.resourceToString("res/shader/texturedModel.fs"));
        shaderManager.createShaderUniform("texturedModel", "projection");
        shaderManager.createShaderUniform("texturedModel", "model");
        shaderManager.createShaderUniform("texturedModel", "view");
        shaderManager.createShaderUniform("texturedModel", "color");

        //Debug
        //glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
    }

    private static void loop(String[] args) {
        activeGameState = new GameStateMenu();

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

            sync(420);
        }
    }

    public static ShaderManager getShaderManager(){
        return shaderManager;
    }

    public static int getWindowWidth() {
        return windowWidth;
    }

    public static int getWindowHeight() {
        return windowHeight;
    }

    public static int[] getViewport(){
        int viewport[] = {0, 0, windowWidth, windowHeight};
        return viewport;
    }

    public static float getMouseX() {
        return mouseX;
    }

    public static float getMouseY() {
        return mouseY;
    }
}