#version 330

in vec3 Color;
in vec2 Texcoord;

out vec4 color;

uniform sampler2D tex;

void main()
{
    color = texture(tex, Texcoord) * vec4(Color, 1.0);
}