#version 450

in vec2 outLightPosition;
in vec3 outVertex;
flat in double outEngineTick;
flat in double outLightStrength;
flat in double outLightFallOff;
flat in vec3 outLightColor;
out vec4 fragColor;

void main(){
    float x = floor(pow(distance(outLightPosition, floor(outVertex.xy*16)/16),1.5));
    double alpha = (outLightStrength / (1 + (pow(abs(x),2) * outLightFallOff)+(pow(abs(x),3) * outLightFallOff)));
    vec4 color = vec4(outLightColor * alpha, 1);
    fragColor = color;
}