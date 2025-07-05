#include "DSRScaler.h"
#include <GL/glew.h>
#include <iostream>
#include <cmath>

static float scaleFactor = 1.0f;
static GLuint fbo = 0;
static GLuint fboTex = 0;
static GLuint vao = 0;
static GLuint vbo = 0;
static GLuint shaderProgram = 0;

// Simple passthrough shader
const char* vsSource = R"(
#version 330 core
layout(location = 0) in vec2 pos;
layout(location = 1) in vec2 uv;
out vec2 texCoord;
void main() {
    texCoord = uv;
    gl_Position = vec4(pos, 0.0, 1.0);
}
)";

const char* fsSource = R"(
#version 330 core
in vec2 texCoord;
out vec4 fragColor;
uniform sampler2D inputTex;
void main() {
    fragColor = texture(inputTex, texCoord);
}
)";

GLuint compileShader(GLenum type, const char* src) {
    GLuint shader = glCreateShader(type);
    glShaderSource(shader, 1, &src, nullptr);
    glCompileShader(shader);
    GLint success;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &success);
    if (!success) {
        char log[512];
        glGetShaderInfoLog(shader, 512, nullptr, log);
        std::cerr << "Shader compile error: " << log << std::endl;
    }
    return shader;
}

void initShader() {
    if (shaderProgram != 0) return;
    GLuint vs = compileShader(GL_VERTEX_SHADER, vsSource);
    GLuint fs = compileShader(GL_FRAGMENT_SHADER, fsSource);
    shaderProgram = glCreateProgram();
    glAttachShader(shaderProgram, vs);
    glAttachShader(shaderProgram, fs);
    glLinkProgram(shaderProgram);
    glDeleteShader(vs);
    glDeleteShader(fs);
}

void initQuad() {
    if (vao != 0) return;
    float quad[] = {
        -1, -1, 0, 0,
         1, -1, 1, 0,
         1,  1, 1, 1,
        -1,  1, 0, 1
    };
    GLuint indices[] = { 0, 1, 2, 2, 3, 0 };

    glGenVertexArrays(1, &vao);
    glBindVertexArray(vao);

    glGenBuffers(1, &vbo);
    GLuint ebo;
    glGenBuffers(1, &ebo);

    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, sizeof(quad), quad, GL_STATIC_DRAW);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);

    glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(float), (void*)0);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(float), (void*)(2 * sizeof(float)));
    glEnableVertexAttribArray(1);
}

void ensureFBO(int width, int height) {
    int scaledW = std::round(width * scaleFactor);
    int scaledH = std::round(height * scaleFactor);

    if (fboTex != 0) {
        GLint texW, texH;
        glBindTexture(GL_TEXTURE_2D, fboTex);
        glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH, &texW);
        glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT, &texH);
        if (texW == scaledW && texH == scaledH) return;

        glDeleteTextures(1, &fboTex);
        glDeleteFramebuffers(1, &fbo);
    }

    glGenTextures(1, &fboTex);
    glBindTexture(GL_TEXTURE_2D, fboTex);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, scaledW, scaledH, 0, GL_RGBA, GL_UNSIGNED_BYTE, nullptr);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    glGenFramebuffers(1, &fbo);
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fboTex, 0);

    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
        std::cerr << "[DSRScaler] FBO incomplete!" << std::endl;
    }

    glBindFramebuffer(GL_FRAMEBUFFER, 0);
}

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_DSRScaler_setScaleFactor
  (JNIEnv*, jclass, jfloat factor) {
    scaleFactor = factor;
    std::cout << "[DSRScaler] Set scale factor: " << scaleFactor << std::endl;
}

JNIEXPORT void JNICALL Java_com_fattie_compatlayer_native_DSRScaler_scale
  (JNIEnv*, jclass, jint inputTex, jint width, jint height) {
    initShader();
    initQuad();
    ensureFBO(width, height);

    int scaledW = std::round(width * scaleFactor);
    int scaledH = std::round(height * scaleFactor);

    // 1. Upscale to FBO
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    glViewport(0, 0, scaledW, scaledH);
    glClear(GL_COLOR_BUFFER_BIT);

    glUseProgram(shaderProgram);
    glBindVertexArray(vao);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, inputTex);
    glUniform1i(glGetUniformLocation(shaderProgram, "inputTex"), 0);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

    // 2. Downscale to screen
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    glViewport(0, 0, width, height);
    glClear(GL_COLOR_BUFFER_BIT);

    glBindTexture(GL_TEXTURE_2D, fboTex);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
}