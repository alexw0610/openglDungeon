#version 450

layout (location=0) in vec3 vertex;
layout (location=1) in vec2 texture;

out vec2 outLightPosition;
out vec3 outVertex;

layout (std140, column_major) uniform UBO{
    uniform dvec3 cameraPosition;
    uniform dvec2 objectPosition;
    uniform double objectScale;
    uniform dvec2 aspectRatio;
    uniform double textureRotation;
    uniform dvec2 viewPointPosition;
    uniform double engineTick;
} ubo;

void main()
{
    outLightPosition = vec2(ubo.objectPosition.x,ubo.objectPosition.y);

    //Scale the object
    vec3 objectScaled = vec3(vertex.x * ubo.objectScale, vertex.y * ubo.objectScale, 1);

    //Translate the object
    //vec3 objectPositionTranslated = objectScaled + vec3(ubo.objectPosition.x,ubo.objectPosition.y,0);

    outVertex = objectScaled;

    //Translate the object relative to camera
    vec3 objectPositionCameraTranslated = objectScaled - vec3(ubo.cameraPosition.x, ubo.cameraPosition.y,0);

    //Scale the object to adjust for aspect ratio
    vec3 objectAspectScaled =  vec3(objectPositionCameraTranslated.x, objectPositionCameraTranslated.y * ubo.aspectRatio.y, objectPositionCameraTranslated.z);

    //Scale the object relative to camera zoom
    vec3 positionScaledCamera = vec3(objectAspectScaled * ubo.cameraPosition.z);

    gl_Position = vec4(positionScaledCamera, 1);
}