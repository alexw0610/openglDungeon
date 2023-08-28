#version 450

layout (location=0) in vec3 vertexIn;
layout (location=1) in vec2 textureIn;

out vec2 textureUv;
out double fixedSize;
out double spriteSize;
out vec3 color;


layout (std140, column_major, binding = 2) uniform UBO{
    uniform dvec2 objectPosition;
    uniform dvec2 aspectRatio;
    uniform double objectScale;
    uniform double fixedSize;
    uniform double width;
    uniform double height;
    uniform double textureX;
    uniform double textureY;
    uniform double textureWidth;
    uniform double textureHeight;
    uniform dvec3 color;
    uniform double spriteSize;
} ubo;

void main()
{
    fixedSize = ubo.fixedSize;
    spriteSize = ubo.spriteSize;
    textureUv = vec2(textureIn.x,textureIn.y);
    color = vec3(ubo.color.rgb);

    vec3 position = vec3(vertexIn.x,vertexIn.y,vertexIn.z);

    if(position.x < 0){
        if(position.y < 0){
            position = vec3(ubo.objectPosition.x, ubo.objectPosition.y, vertexIn.z);
            textureUv = vec2(ubo.textureX, ubo.textureY + ubo.textureHeight);
        }else{
            position = vec3(ubo.objectPosition.x, ubo.objectPosition.y + ubo.height, vertexIn.z);
            textureUv = vec2(ubo.textureX, ubo.textureY);
        }
    }else {
        if(position.y < 0){
            position = vec3(ubo.objectPosition.x + ubo.width, ubo.objectPosition.y, vertexIn.z);
            textureUv = vec2(ubo.textureX + ubo.textureWidth, ubo.textureY + ubo.textureHeight);
        }else{
            position = vec3(ubo.objectPosition.x + ubo.width, ubo.objectPosition.y + ubo.height, vertexIn.z);
            textureUv = vec2(ubo.textureX + ubo.textureWidth, ubo.textureY);
        }
    }

    //Scale the object to adjust for aspect ratio
    position =  vec3(position.x, position.y * ubo.aspectRatio.y, position.z);

    //Scale the object
    position = vec3(position.x * ubo.objectScale, position.y * ubo.objectScale, 0);

    gl_Position = vec4(position, 1);
}