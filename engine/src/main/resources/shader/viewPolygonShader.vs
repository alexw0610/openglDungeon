#version 450

layout (location=0) in vec3 vertex;
layout (location=1) in vec2 texture;

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
} ubo;

void main()
{
    //Scale the object
    vec3 objectScaled = vec3(vertex.x * ubo.objectScale, vertex.y * ubo.objectScale, 1);

    //Translate the object
    vec3 objectPositionTranslated = objectScaled + vec3(ubo.objectPosition.x,ubo.objectPosition.y,1);

    //Translate the object relative to camera
    vec3 objectPositionCameraTranslated = objectPositionTranslated - vec3(ubo.cameraPosition.x, ubo.cameraPosition.y,0);

    //Scale the object to adjust for aspect ratio
    vec3 objectAspectScaled =  vec3(objectPositionCameraTranslated.x, objectPositionCameraTranslated.y * ubo.aspectRatio.y, objectPositionCameraTranslated.z);

    //Scale the object relative to camera zoom
    vec3 positionScaledCamera = vec3(objectAspectScaled * ubo.cameraPosition.z);

    gl_Position = vec4(positionScaledCamera, 1);
}