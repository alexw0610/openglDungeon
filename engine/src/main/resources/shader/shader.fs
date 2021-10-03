#version 450
#define VIEWDISTANCE 10
#define PHI 1.61803398874989484820459
#define SEED 5.326573453
#define RESOLUTION 0.05
#define DARKNESS 0.2

in vec3 outVertex;
in vec2 outTexture;

flat in double outEngineTick;

out vec4 fragColor;

layout(binding = 0) uniform sampler2D texture_sampler;
layout(binding = 1) uniform sampler2D view_sampler;
layout(binding = 2) uniform sampler2D light_sampler;

// Gold Noise ©2015 dcerisano@standard3d.com
// - based on the Golden Ratio
// - uniform normalized distribution
// - fastest static noise generator function (also runs at low precision)
// - use with indicated seeding method.
float gold_noise(vec2 xy){
       return fract(tan(distance(xy * PHI * (floor(float(outEngineTick) * 3.5)), xy) * SEED) * xy.x);
}

void main(){
    vec2 visTexPos = vec2(gl_FragCoord.xy) / vec2(1280, 960);
    vec4 viewArea = vec4(texture(view_sampler, visTexPos.xy));
    vec4 lightArea = vec4(texture(light_sampler, visTexPos.xy));
    vec4 pixelColor = vec4(texture(texture_sampler, outTexture));
    float averagePixelValue = (pixelColor.r + pixelColor.g + pixelColor.b)/3;
    vec3 basePixel = vec3(0,0,0);
    if(viewArea.r > 0){
        basePixel = pixelColor.rgb * DARKNESS;
        if(lightArea.a > 0){
            basePixel = basePixel + pixelColor.rgb * lightArea.rgb;
        }
    }
    fragColor = vec4(basePixel, pixelColor.a);
}