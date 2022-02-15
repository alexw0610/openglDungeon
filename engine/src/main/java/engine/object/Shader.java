package engine.object;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLContext;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class Shader {

    public final String shaderKey;
    final int program;
    final int vs;
    final int fs;

    public Shader(String shaderKey, String[] vertexShaderContent, String[] fragmentShaderContent) {

        this.shaderKey = shaderKey;
        GL3 gl = GLContext.getCurrentGL().getGL3();
        program = gl.glCreateProgram();
        if (!gl.glIsProgram(program)) {
            System.err.println("Could not create Shader Program");
        }

        //Vertex Shader
        vs = gl.glCreateShader(gl.GL_VERTEX_SHADER);
        gl.glShaderSource(vs, 1, vertexShaderContent, null);
        gl.glCompileShader(vs);
        IntBuffer status = IntBuffer.allocate(1);
        gl.glGetShaderiv(vs, gl.GL_COMPILE_STATUS, status);
        if (status.get() == gl.GL_FALSE) {
            System.out.println("Could not compile Vertex shader: " + shaderKey);
            printErrLog(vs);
            System.out.println(Arrays.toString(vertexShaderContent));
            System.exit(1);
        }

        //Fragment Shader
        fs = gl.glCreateShader(gl.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fs, 1, fragmentShaderContent, null);
        gl.glCompileShader(fs);
        status = IntBuffer.allocate(1);
        gl.glGetShaderiv(fs, gl.GL_COMPILE_STATUS, status);
        if (status.get() == gl.GL_FALSE) {
            System.out.println("Could not compile Fragment shader: " + shaderKey);
            printErrLog(fs);
            System.exit(1);
        }

        gl.glAttachShader(program, vs);
        gl.glAttachShader(program, fs);
        status = IntBuffer.allocate(1);
        gl.glGetProgramiv(program, gl.GL_ATTACHED_SHADERS, status);
        gl.glBindAttribLocation(program, 0, "vertex");
        gl.glLinkProgram(program);
        status = IntBuffer.allocate(1);
        gl.glGetProgramiv(program, gl.GL_LINK_STATUS, status);
        if (status.get() == gl.GL_FALSE) {
            System.err.println("Could not Link Shader: " + shaderKey);
            System.exit(1);
        }
        gl.glValidateProgram(program);
        status = IntBuffer.allocate(1);
        gl.glGetProgramiv(program, gl.GL_VALIDATE_STATUS, status);
        if (status.get() == gl.GL_FALSE) {
            System.err.println("Could not validate Shader: " + shaderKey);
            System.exit(1);
        }
    }

    public void bind() {
        GL3 gl = GLContext.getCurrentGL().getGL3();
        gl.glUseProgram(program);
    }


    private void printErrLog(int shader) {
        GL3 gl = GLContext.getCurrentGL().getGL3();
        IntBuffer log_length = IntBuffer.allocate(1);
        ByteBuffer message = ByteBuffer.allocate(1024);
        gl.glGetShaderInfoLog(shader, 1024, log_length, message);
        byte[] bytes = message.array();
        String output = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(output);
    }

}

