#version 120

// You May Use The Following Functions As RenderMaterial Input
// vec4 getDiffuseColor(vec2 uv)
// vec4 getNormalColor(vec2 uv)
// vec4 getSpecularColor(vec2 uv)

// Lighting Information
const int MAX_LIGHTS = 16;
uniform int numLights;
uniform vec3 lightIntensity[MAX_LIGHTS];
uniform vec3 lightPosition[MAX_LIGHTS];
uniform vec3 ambientLightIntensity;

// Camera Information
uniform vec3 worldCam;
uniform float exposure;

// Shading Information
uniform float shininess;

varying vec2 fUV;
varying vec3 fN; // normal at the vertex
varying vec4 worldPos; // vertex position in world coordinates

void main() {
    float c = 1;
    vec3 N = normalize(fN);
    vec3 V = normalize(worldCam - worldPos.xyz);
    
    vec4 finalColor = vec4(0.0, 0.0, 0.0, 0.0);
    
    for (int i = 0; i < numLights; i++) {
        float r = length(lightPosition[i] - worldPos.xyz);
        vec3 L = normalize(lightPosition[i] - worldPos.xyz);
        vec3 H = normalize(L + V);
        
        // calculate diffuse term
        vec4 Idiff = getDiffuseColor(fUV) * max(dot(N, L), 0.0);
        Idiff = clamp(Idiff, 0.0, 1.0);
        
        // calculate specular term
        vec4 Ispec = getSpecularColor(fUV) * pow(max(dot(N, H), 0.0), shininess);
        Ispec = clamp(Ispec, 0.0, 1.0);
        
        // calculate ambient term
        vec4 Iamb = getDiffuseColor(fUV);
        Iamb = clamp(Iamb, 0.0, 1.0);
        
        finalColor += vec4(lightIntensity[i], 0.0) * (Idiff + Ispec) / (r*r);
    }

    gl_FragColor = finalColor;
}