#version 450

#define DARKNESS 0.1
#define DEFAULT_SPRITE_SIZE 32

in vec3 vertex;
in vec2 textureUv;
flat in double engineTick;
flat in double textureOffSetX;
flat in double textureOffSetY;
flat in double alwaysVisible;
flat in double shadeless;
flat in vec3 colorOverride;

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
    double animationOffsetX = floor(mod(textureOffSetX,(textureSheetSize.x/DEFAULT_SPRITE_SIZE)));
    double animationOffsetY = floor(mod(textureOffSetY,(textureSheetSize.y/DEFAULT_SPRITE_SIZE)));
    vec2 textureUv = vec2(((textureUv.x/(textureSheetSize.x/DEFAULT_SPRITE_SIZE)) + ((DEFAULT_SPRITE_SIZE/textureSheetSize.x)*animationOffsetX)),
    ((textureUv.y/(textureSheetSize.y/DEFAULT_SPRITE_SIZE))+((DEFAULT_SPRITE_SIZE/textureSheetSize.y)*animationOffsetY)));
    vec4 viewArea = vec4(texture(view_sampler, viewMapUv.xy));
    vec4 lightArea = vec4(texture(light_sampler, viewMapUv.xy));
    vec4 pixelColor;
    if(colorOverride.r != -1){
        pixelColor = vec4(colorOverride.rgb,1);
    }else{
        pixelColor = vec4(texture(texture_sampler, textureUv));
    }
    float averagePixelValue = (pixelColor.r + pixelColor.g + pixelColor.b)/3;
    vec3 basePixel = vec3(.007, .004, .006);
    if(viewArea.a > 0 || alwaysVisible == 1){
        basePixel = pixelColor.rgb * DARKNESS;
        if(lightArea.a > 0 ){
            if(shadeless == 1){
                basePixel = pixelColor.rgb;
            }else {
                basePixel = basePixel + pixelColor.rgb * lightArea.rgb;
            }
        }
        basePixel = basePixel * viewArea.r;
        fragColor = vec4(basePixel, pixelColor.a);
    }else{
        fragColor = vec4(basePixel, 0);
    }
}