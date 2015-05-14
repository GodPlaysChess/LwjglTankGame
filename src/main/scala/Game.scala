import java.io.PrintStream
import java.nio.ByteBuffer

import org.lwjgl.Sys
import org.lwjgl.glfw.Callbacks._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl._
import org.lwjgl.system.MemoryUtil._

class Game() {
  // We need to strongly reference callback instances.
  // The window handle
  private var window: Long = _
  private var errorCallback: GLFWErrorCallback = _
  private var keyCallback: GLFWKeyCallback = _

  def run() {
    System.out.println("Hello LWJGL " + Sys.getVersion + "!")

    try {
      init()
      loop()

      // Release window and window callbacks
      glfwDestroyWindow(window)
      keyCallback.release()
    } finally {
      // Terminate GLFW and release the GLFWerrorfun
      glfwTerminate()
      errorCallback.release()
    }
  }

  private def init() {
    // Setup an error callback. The default implementation
    // will print the error message in System.err.
    errorCallback = errorCallbackPrint(System.err)
    glfwSetErrorCallback(errorCallback)

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (glfwInit() != GL11.GL_TRUE)
      throw new IllegalStateException("Unable to initialize GLFW")

    // Configure our window
    glfwDefaultWindowHints() // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GL_FALSE) // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GL_TRUE) // the window will be resizable

    val WIDTH: Int = 300
    val HEIGHT: Int = 300

    // Create the window
    window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL)
    if (window == NULL)
      throw new RuntimeException("Failed to create the GLFW window")

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    keyCallback = new GLFWKeyCallback() {
      override def invoke(window: Long, key: Int, scancode: Int, action: Int, modes: Int): Unit = {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
          glfwSetWindowShouldClose(window, GL_TRUE) // We will detect this in our rendering loop
      }
    }
    glfwSetKeyCallback(window, keyCallback)

    // Get the resolution of the primary monitor
    val vidmode: ByteBuffer = glfwGetVideoMode(glfwGetPrimaryMonitor())
    // Center our window
    glfwSetWindowPos(
      window,
      (GLFWvidmode.width(vidmode) - WIDTH) / 2,
      (GLFWvidmode.height(vidmode) - HEIGHT) / 2
    )

    // Make the OpenGL context current
    glfwMakeContextCurrent(window)
    // Enable v-sync
    glfwSwapInterval(1)

    // Make the window visible
    glfwShowWindow(window)
  }

  private def loop() {
    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the ContextCapabilities instance and makes the OpenGL
    // bindings available for use.
    GLContext.createFromCurrent()

    // Set the clear color
    glClearColor(1.0f, 0.0f, 0.0f, 0.0f)

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (glfwWindowShouldClose(window) == GL_FALSE) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT) // clear the framebuffer

      glfwSwapBuffers(window) // swap the color buffers

      // Poll for window events. The key callback above will only be
      // invoked during this call.
      glfwPollEvents()
    }
  }
}

object Game {
  def init(width: Int, heigth: Int, printStream: PrintStream): (GLFWErrorCallback, GLFWKeyCallback) = {
    val errorCallback = errorCallbackPrint(printStream)
    glfwSetErrorCallback(errorCallback)

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (glfwInit() != GL11.GL_TRUE)
      throw new IllegalStateException("Unable to initialize GLFW")

    // Configure our window
    glfwDefaultWindowHints() // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GL_FALSE) // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GL_TRUE) // the window will be resizable

    // Create the window
    val window = glfwCreateWindow(width, heigth, "Hello World!", NULL, NULL)
    if (window == NULL)
      throw new RuntimeException("Failed to create the GLFW window")

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    val keyCallback = new GLFWKeyCallback() {
      override def invoke(window: Long, key: Int, scancode: Int, action: Int, modes: Int): Unit = {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
          glfwSetWindowShouldClose(window, GL_TRUE) // We will detect this in our rendering loop
      }
    }
    glfwSetKeyCallback(window, keyCallback)

    // Get the resolution of the primary monitor
    val vidmode: ByteBuffer = glfwGetVideoMode(glfwGetPrimaryMonitor())
    // Center our window
    glfwSetWindowPos(
      window,
      (GLFWvidmode.width(vidmode) - WIDTH) / 2,
      (GLFWvidmode.height(vidmode) - HEIGHT) / 2
    )

    // Make the OpenGL context current
    glfwMakeContextCurrent(window)
    // Enable v-sync
    glfwSwapInterval(1)

    // Make the window visible
    glfwShowWindow(window)

    (errorCallback, keyCallback)
  }
}
