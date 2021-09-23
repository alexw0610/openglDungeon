#version 450

layout (location=0) in vec3 vertex;
layout (location=1) in vec2 texture;

out vec3 outVertex;
out vec2 outTexture;

layout (std140, column_major) uniform UBO{
    uniform dvec3 cameraPosition;
    uniform dvec2 objectPosition;
    uniform double objectScale;
    uniform dvec2 aspectRatio;
} ubo;


void main()
{
    outTexture = vec2(texture.x * ubo.objectScale, texture.y * ubo.objectScale);
    outVertex = vertex;

    //Scale the object
    vec3 objectScaled = vec3(vertex.x * ubo.objectScale, vertex.y * ubo.objectScale, 1);

    //Translate the object
    vec3 objectPositionTranslated = objectScaled + vec3(ubo.objectPosition.x,ubo.objectPosition.y,0);

    //Translate the object relative to camera
    vec3 objectPositionCameraTranslated = objectPositionTranslated - vec3(ubo.cameraPosition.x, ubo.cameraPosition.y,0);

    //Scale the object to adjust for aspect ratio
    vec3 positionAspectScaled =  vec3(objectPositionCameraTranslated.x, objectPositionCameraTranslated.y * ubo.aspectRatio.y, objectPositionCameraTranslated.z);

    //Scale the object relative to camera zoom
    gl_Position = vec4(positionAspectScaled * ubo.cameraPosition.z, 1);
}