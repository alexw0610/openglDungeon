#version 450

#define DARKNESS 0.1

in vec2 textureUv;
flat in double fixedSize;
flat in double spriteSize;
in vec3 color;

out vec4 fragColor;

layout(binding = 0) uniform sampler2D texture_sampler;

void main(){
    vec2 textureSheetSize = textureSize(texture_sampler,0);
    vec2 uvCoords;
    if(fixedSize == 1){
        uvCoords = vec2(((textureUv.x/(textureSheetSize.x/spriteSize))),
                ((textureUv.y/(textureSheetSize.y/spriteSize))));
    }else{
        uvCoords = vec2(textureUv.x,textureUv.y);
    }
    vec4 pixelColor = vec4(texture(texture_sampler, uvCoords));
    if(pixelColor.a > 0 && color != vec3(1.0, 1.0, 1.0)){
        pixelColor = vec4(color,fragColor.a);
    }
    fragColor = pixelColor;
}