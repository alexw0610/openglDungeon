#version 450

#define DARKNESS 0.5

in vec3 vertex;
in vec2 textureUv;
flat in double engineTick;
flat in double textureOffSetX;
flat in double textureOffSetY;
flat in double alwaysVisible;
flat in double shadeless;
flat in double spriteSize;

out vec4 fragColor;

layout(binding = 0) uniform sampler2D texture_sampler;
layout(binding = 1) uniform sampler2D view_sampler;
layout(binding = 2) uniform sampler2D light_sampler;

float rand(vec2 co){
    return (0.5-(fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453)))*200;
}

float getViewAreaRSample(){
    float sum = 0;
    vec2 pos = vec2(gl_FragCoord.xy);
    vec2 viewAreaSize = textureSize(view_sampler,0);
    for(int x=0;x<10;x++) {
        for(int y=0;y<10;y++) {
            vec2 uv = ((vec2(pos.x+rand(vec2(x,y)),pos.y+rand(vec2(y,x))) / viewAreaSize.xy));
            sum = sum + vec4(texture(view_sampler, uv.xy)).r;
        }
    }
    return sum/100;
}

void main(){
    vec2 textureSheetSize = textureSize(texture_sampler,0);
    vec2 viewAreaSize = textureSize(view_sampler,0);
    vec2 viewMapUv = ((vec2(gl_FragCoord.xy)) / viewAreaSize.xy);
    double animationOffsetX = floor(mod(textureOffSetX,(textureSheetSize.x/spriteSize)));
    double animationOffsetY = floor(mod(textureOffSetY,(textureSheetSize.y/spriteSize)));
    vec2 textureUv = vec2(((textureUv.x/(textureSheetSize.x/spriteSize)) + ((spriteSize/textureSheetSize.x)*animationOffsetX)),
    ((textureUv.y/(textureSheetSize.y/spriteSize))+((spriteSize/textureSheetSize.y)*animationOffsetY)));
    vec4 viewArea = vec4(texture(view_sampler, viewMapUv.xy));
    vec4 lightArea = vec4(texture(light_sampler, viewMapUv.xy));
    vec4 pixelColor = vec4(texture(texture_sampler, textureUv));
    vec3 outPixel = vec3(0,0,0);
    if(shadeless == 1) {
        outPixel = pixelColor.rgb;
    } else {
        outPixel = pixelColor.rgb * DARKNESS;
        outPixel = (1+lightArea.rgb) * outPixel.rgb;
    }
    if(alwaysVisible == 1) {
        outPixel = outPixel;
    } else if (viewArea.a == 0) {
        outPixel = vec3(0,0,0);
    }
    fragColor = vec4(outPixel.rgb, pixelColor.a);
}