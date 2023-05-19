#version 450

layout (location=0) in vec3 vertexIn;
layout (location=1) in vec2 textureIn;

out vec3 vertex;
out vec2 textureUv;
out double engineTick;
out double textureOffSetX;
out double textureOffSetY;
out double alwaysVisible;
out double fixedSize;
out vec3 colorOverride;


layout (std140, column_major, binding = 2) uniform UBO{
    uniform dvec3 cameraPosition;
    uniform double objectScale;
    uniform dvec2 objectPosition;
    uniform double textureOffSetX;
    uniform double textureOffSetY;
    uniform dvec2 aspectRatio;
    uniform double textureRotation;
    uniform double engineTick;
    uniform double alwaysVisible;
    uniform double fixedSize;
    uniform dvec2 transformationOverride;
    uniform dvec3 colorOverride;
    uniform double perspectiveLayer;
    uniform double isClipSpace;
} ubo;

void main()
{
    vertex = vertexIn;
    engineTick = ubo.engineTick;
    textureOffSetX = ubo.textureOffSetX;
    textureOffSetY = ubo.textureOffSetY;
    alwaysVisible = ubo.alwaysVisible;
    fixedSize = ubo.fixedSize;
    colorOverride = vec3(ubo.colorOverride);
    textureUv = vec2(textureIn.x,textureIn.y);

    vec3 position = vec3(vertexIn.x,vertexIn.y,vertexIn.z);

    //Transform the object
    position = vec3(vertexIn.x * (ubo.transformationOverride.x), vertexIn.y * (ubo.transformationOverride.y), 0);

    //Scale the object to adjust for aspect ratio
    position =  vec3(position.x, position.y * ubo.aspectRatio.y, position.z);

    //Scale the object
    position = vec3(position.x * ubo.objectScale, position.y * ubo.objectScale, 0);

    //Translate the object
    position = position + vec3(ubo.objectPosition.x / ubo.aspectRatio.y, ubo.objectPosition.y, 0);

    //Scale the object relative to camera zoom
    if(ubo.fixedSize == 0){
        position = vec3(position.x * ubo.cameraPosition.z, position.y * ubo.cameraPosition.z, position.z);
    }

    gl_Position = vec4(position, 1);
}