#version 120

// You May Use The Following Functions As RenderMaterial Input
// vec4 getDiffuseColor(vec2 uv)
// vec4 getNormalColor(vec2 uv)
// vec4 getSpecularColor(vec2 uv)

// Lighting Information
const int MAX_LIGHTS = 16;
const float fo = .04;
const float PI = 3.1415926535897932384626433832795;
uniform int numLights;
uniform vec3 lightIntensity[MAX_LIGHTS];
uniform vec3 lightPosition[MAX_LIGHTS];
uniform vec3 ambientLightIntensity;

// Camera Information
uniform vec3 worldCam;
uniform float exposure;

// Shading Information
// 0 : smooth, 1: rough
uniform float shininess;

varying vec2 fUV;
varying vec3 fN; // normal at the vertex
varying vec4 worldPos; // vertex position in world coordinates

uniform float vTime;

float rand(vec2 co){
    float a = 12.9898;
    float b = 78.233;
    float c = 43758.5453;
    float dt= dot(co.xy ,vec2(a,b));
    float sn= mod(dt,3.14);
    return fract(sin(sn) * c) * .4;
}

float red;
float blue;
float green;

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

	  finalColor += vec4(lightIntensity[i], 0.0) * (Idiff + Ispec) / (r*r) + vec4(ambientLightIntensity, 0.0) * Iamb;
	}
    red = (cos(vTime) + 1) / 2;
    green =(sin(vTime) + 1) / 2;
//    blue = mod(1-vTime, 1.0);
//    green = mod(.5*vTime, 1.0);
    //vec3 v = vec3(rand(fUV), rand(fUV), 1);//cos(vTime)*.1);
    //vec3 v = vec3(.5,.5,.5);
    //vec3 v = vec3(red, green, 1);

    //gl_FragColor = vec4(v, 0);
    vec4 modifier = vec4(rand(fUV)/3, rand(fUV)/3, rand(fUV)/3, 0);
    gl_FragColor = (finalColor + modifier) * exposure;
}