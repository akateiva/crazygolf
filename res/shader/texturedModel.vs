#version 330

layout (location=0) in vec3 pos;
layout (location=1) in vec3 color;
layout (location=2) in vec2 texcoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec3 Color;
out vec2 Texcoord;

void main()
{
    //= projection * view * model * vec4(pos, 1.0);
    gl_Position = projection * view * model * vec4(pos, 1.0);
    Color = color;
    Texcoord = texcoord;
}