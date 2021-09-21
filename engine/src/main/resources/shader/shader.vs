#version 450

layout (location=0) in vec3 vertex;
layout (location=1) in vec2 texture;

out vec3 outVertex;
out vec2 outTexture;

layout (std140, column_major) uniform CameraBuffer{
    uniform dvec3 camera;
    uniform dvec2 position;
    uniform double scale;
} cameraBuffer;


void main()
{
    outTexture = vec2(texture.x*cameraBuffer.scale,texture.y*cameraBuffer.scale);
    outVertex = vertex;
    vec3 objectScale = vec3(vertex.x * cameraBuffer.scale, vertex.y * cameraBuffer.scale, 1);
    vec3 positionScale = vec3(objectScale.x, objectScale.y, objectScale.z);
    vec3 positionObjectTranslation = positionScale + vec3(cameraBuffer.position.x,cameraBuffer.position.y,0);
    vec3 positionCameraTranslation = positionObjectTranslation - vec3(cameraBuffer.camera.x,cameraBuffer.camera.y,0);
    gl_Position = vec4(positionCameraTranslation * cameraBuffer.camera.z, 1);
}