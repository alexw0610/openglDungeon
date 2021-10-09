#version 450

layout (location=0) in vec3 vertex;
layout (location=1) in vec2 texture;

out vec2 outLightPosition;
out vec3 outVertex;
out double outLightStrength;
out double outLightFallOff;
out vec3 outLightColor;
out double outEngineTick;

layout (std140, column_major, binding = 1) uniform UBO{
    uniform dvec3 cameraPosition;
    uniform double engineTick;
    uniform dvec2 lightPosition;
    uniform double layer;
    uniform dvec2 aspectRatio;
    uniform double lightStrength;
    uniform double lightFallOff;
    uniform dvec3 lightColor;
} ubo;

void main()
{
    outLightPosition = vec2(ubo.lightPosition.x, ubo.lightPosition.y);
    outLightStrength = ubo.lightStrength;
    outLightFallOff = ubo.lightFallOff;
    outLightColor = vec3(ubo.lightColor.r, ubo.lightColor.g, ubo.lightColor.b);
    outEngineTick = ubo.engineTick;
    //Scale the object
    vec3 objectScaled = vec3(vertex.x, vertex.y, 1);

    //Translate the object
    //vec3 objectPositionTranslated = objectScaled + vec3(0,0,0);

    outVertex = objectScaled;

    //Translate the object relative to camera
    vec3 objectPositionCameraTranslated = objectScaled - vec3(ubo.cameraPosition.x, ubo.cameraPosition.y,0);

    //Scale the object to adjust for aspect ratio
    vec3 objectAspectScaled =  vec3(objectPositionCameraTranslated.x, objectPositionCameraTranslated.y * ubo.aspectRatio.y, objectPositionCameraTranslated.z);

    //Scale the object relative to camera zoom
    vec3 positionScaledCamera = vec3(objectAspectScaled * ubo.cameraPosition.z);

    gl_Position = vec4(positionScaledCamera, 1);
}