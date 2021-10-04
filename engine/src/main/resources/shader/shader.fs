#version 450

#define DARKNESS 0.2

in vec3 vertex;
in vec2 textureUv;
flat in double engineTick;
flat in double textureOffSetX;
flat in double textureOffSetY;
flat in double alwaysVisible;
flat in double shadeless;

out vec4 fragColor;

layout(binding = 0) uniform sampler2D texture_sampler;
layout(binding = 1) uniform sampler2D view_sampler;
layout(binding = 2) uniform sampler2D light_sampler;

void main(){
    vec2 textureSize = textureSize(texture_sampler,0);
    vec2 viewMapUv = ((vec2(gl_FragCoord.xy)) / vec2(1280, 960));
    vec2 textureUv = vec2(((textureUv.x/(textureSize.x/32))+((32/textureSize.x)*textureOffSetX)),
    ((textureUv.y/(textureSize.y/32))+((32/textureSize.y)*textureOffSetY)));

    vec4 viewArea = vec4(texture(view_sampler, viewMapUv.xy));
    vec4 lightArea = vec4(texture(light_sampler, viewMapUv.xy));
    vec4 pixelColor = vec4(texture(texture_sampler, textureUv));

    float averagePixelValue = (pixelColor.r + pixelColor.g + pixelColor.b)/3;
    vec3 basePixel = vec3(0,0,0);

    if(viewArea.r > 0 || alwaysVisible == 1){
        basePixel = pixelColor.rgb * DARKNESS;
        if(lightArea.a > 0){
            basePixel = basePixel + pixelColor.rgb * lightArea.rgb;
        }
        if(shadeless == 1){
            basePixel = pixelColor.rgb;
        }
    }
    fragColor = vec4(basePixel, pixelColor.a);
}