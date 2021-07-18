#version 330

in vec3 vertex;
in vec2 outTexture;
out vec4 fragColor;

uniform sampler2D texture_sampler;

void main(){
    fragColor = vec4(texture(texture_sampler,outTexture));
}