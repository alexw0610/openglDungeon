#version 450

in vec2 outLightPosition;
in vec3 outVertex;
out vec4 fragColor;

void main(){
    fragColor = vec4(1, 0, 0, (1/(1+(distance(outLightPosition,outVertex.xy)*0.1)))-0.5);
}