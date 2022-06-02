#version 450

#define DARKNESS 0.1

in vec3 vertex;
in vec2 textureUv;
flat in double engineTick;
flat in double textureOffSetX;
flat in double textureOffSetY;
flat in double alwaysVisible;
flat in double fixedSize;
flat in vec3 colorOverride;

out vec4 fragColor;

layout(binding = 0) uniform sampler2D texture_sampler;
layout(binding = 1) uniform sampler2D view_sampler;
layout(binding = 2) uniform sampler2D light_sampler;

void main(){
    vec2 textureSheetSize = textureSize(texture_sampler,0);
    vec2 viewAreaSize = textureSize(view_sampler,0);
    vec2 viewMapUv = ((vec2(gl_FragCoord.xy)) / viewAreaSize.xy);
    vec4 viewArea = vec4(texture(view_sampler, viewMapUv.xy));
    vec4 pixelColor = vec4(texture(texture_sampler, textureUv)) *  vec4(colorOverride.rgb,1);
    vec3 basePixel = vec3(0,1,0);
    if(alwaysVisible == 0){
        basePixel = pixelColor.rgb * viewArea.r;
    }else{
        basePixel = pixelColor.rgb;
    }
    fragColor = vec4(basePixel, pixelColor.a);

}