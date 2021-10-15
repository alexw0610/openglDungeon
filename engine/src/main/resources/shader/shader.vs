#version 450

layout (location=0) in vec3 vertexIn;
layout (location=1) in vec2 textureIn;

out vec3 vertex;
out vec2 textureUv;
out double engineTick;
out double textureOffSetX;
out double textureOffSetY;
out double alwaysVisible;
out double shadeless;
out vec3 colorOverride;


layout (std140, column_major) uniform UBO{
    uniform dvec3 cameraPosition;
    uniform double objectScale;
    uniform dvec2 objectPosition;
    uniform double textureOffSetX;
    uniform double textureOffSetY;
    uniform dvec2 aspectRatio;
    uniform double textureRotation;
    uniform double engineTick;
    uniform double alwaysVisible;
    uniform double shadeless;
    uniform double mirrored;
    uniform double perspectiveLayer;
    uniform dvec3 colorOverride;
} ubo;

void main()
{
    vertex = vertexIn;
    engineTick = ubo.engineTick;
    textureOffSetX = ubo.textureOffSetX;
    textureOffSetY = ubo.textureOffSetY;
    alwaysVisible = ubo.alwaysVisible;
    shadeless = ubo.shadeless;
    colorOverride = vec3(ubo.colorOverride);

    //Rotate the texture coordinates
    vec2 textureCentered = vec2((textureIn.x - 0.5) * ubo.mirrored, textureIn.y - 0.5);
    vec2 rotatedTexture = vec2(
        (textureCentered.x * cos(radians(float(ubo.textureRotation)))) - (textureCentered.y * sin(radians(float(ubo.textureRotation)))),
        (textureCentered.x * sin(radians(float(ubo.textureRotation)))) + (textureCentered.y * cos(radians(float(ubo.textureRotation))))
    );
    textureUv = vec2((rotatedTexture.x) + 0.5,rotatedTexture.y + 0.5);

    //Scale the object
    vec3 objectScaled = vec3(vertexIn.x * ubo.objectScale, vertexIn.y * ubo.objectScale, 0);

    //Translate the object
    vec3 objectPositionTranslated = objectScaled + vec3(ubo.objectPosition.x, ubo.objectPosition.y, 0);

    //Translate the object relative to camera
    vec3 objectPositionCameraTranslated = objectPositionTranslated - vec3(ubo.cameraPosition.x, ubo.cameraPosition.y, 0);

    //Scale the object to adjust for aspect ratio
    vec3 objectAspectScaled =  vec3(objectPositionCameraTranslated.x, objectPositionCameraTranslated.y * ubo.aspectRatio.y, objectPositionCameraTranslated.z);

    //Scale the object relative to camera zoom
    vec3 positionScaledCamera = vec3(objectAspectScaled.x * ubo.cameraPosition.z * ubo.perspectiveLayer, objectAspectScaled.y * ubo.cameraPosition.z * ubo.perspectiveLayer, objectAspectScaled.z);

    gl_Position = vec4(positionScaledCamera, 1);
}