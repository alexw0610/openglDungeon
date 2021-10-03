#version 450

layout (location=0) in vec3 vertex;
layout (location=1) in vec2 texture;

out vec3 outVertex;
out vec2 outTexture;
out double outEngineTick;

layout (std140, column_major) uniform UBO{
    uniform dvec3 cameraPosition;
    uniform dvec2 objectPosition;
    uniform double layer;
    uniform double objectScale;
    uniform dvec2 aspectRatio;
    uniform double textureRotation;
    uniform double engineTick;
} ubo;

void main()
{
    outEngineTick = ubo.engineTick;

    //Rotate the texture coordinates
    vec2 rotatedTexture = vec2(
        (texture.x * cos(radians(float(ubo.textureRotation)))) - (texture.y * sin(radians(float(ubo.textureRotation)))),
        (texture.x * sin(radians(float(ubo.textureRotation)))) + (texture.y * cos(radians(float(ubo.textureRotation))))
    );

    //Scale the texture coordinates
    outTexture = vec2(rotatedTexture.x * ubo.objectScale, rotatedTexture.y * ubo.objectScale);

    //Scale the object
    vec3 objectScaled = vec3(vertex.x * ubo.objectScale, vertex.y * ubo.objectScale, 0);

    //Translate the object
    vec3 objectPositionTranslated = objectScaled + vec3(ubo.objectPosition.x, ubo.objectPosition.y, 1);

    outVertex = objectPositionTranslated;

    //Translate the object relative to camera
    vec3 objectPositionCameraTranslated = objectPositionTranslated - vec3(ubo.cameraPosition.x, ubo.cameraPosition.y, 0);

    //Scale the object to adjust for aspect ratio
    vec3 objectAspectScaled =  vec3(objectPositionCameraTranslated.x, objectPositionCameraTranslated.y * ubo.aspectRatio.y, objectPositionCameraTranslated.z);

    //Scale the object relative to camera zoom
    vec3 positionScaledCamera = vec3(objectAspectScaled.x * ubo.cameraPosition.z, objectAspectScaled.y * ubo.cameraPosition.z, objectAspectScaled.z);

    gl_Position = vec4(positionScaledCamera, 1);
}