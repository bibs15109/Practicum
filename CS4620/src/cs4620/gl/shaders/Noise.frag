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

//	  finalColor += vec4(lightIntensity[i], 0.0) * (Idiff + Ispec) / (r*r) + vec4(ambientLightIntensity, 0.0) * Iamb;
      finalColor += vec4(lightIntensity[i], 0.0) * (Idiff + Ispec) / (r*r) + vec4(.1, .1, .1, 0.0) * Iamb;
	}
    red = (cos(vTime) + 1) / 2;
//    green =(sin(vTime) + 1) / 2;
    green = (sin(worldPos.x + 1)/ 2);
    
    float theta = acos(worldPos.z / length(worldPos));
    float phi = atan(worldPos.y, worldPos.x);
    
//    blue = (abs(sin(phi + .5) * cos(theta)) + .5) / 2;
    
    
    float coeff = 1 / (sqrt(2* PI));
    float e = exp(-pow((theta - 1.5),2) / 2);
    float gauss = coeff * e;
    
    float gauss2 = abs(cos(phi + 2 * theta + vTime * 6));
    float gauss3= abs(cos((1 * phi + 1 + 2 * theta) - vTime * 1));
    float gauss4= abs(cos((2.3 * phi + 2 + 2 *theta) + vTime * 7.8));
    float gauss5= abs(cos((3.1 * phi + 3 + 2 * theta) + vTime * 1.2));
    float gauss6= abs(cos((4.5 * phi + 4 + 2 * theta) - vTime * 3));
    float tGauss1 = (gauss2 + gauss3 + gauss4 + gauss5 + gauss6) / 5;

    
    float gauss7 = abs(cos(theta + 2 * phi + vTime));
    float gauss8= abs(cos((4 * theta + 1 + 2* phi) + vTime * 1.1));
    float gauss9= abs(cos((5.4 * theta + 2 + 2 * phi) + vTime * 3.2));
    float gauss10= abs(cos((9.3 * theta + 3 + 2 * phi) - vTime * 2.8));
    float gauss11= abs(cos((7.9 * theta + 4 + 2 * phi) - vTime * 1.3));
    float tGauss2 = (gauss7 + gauss8 + gauss9 + gauss10 + gauss11) /5;
    
//    float finalGauss = pow((tGauss1 * tGauss2),3);
    float finalGauss = tGauss2 * tGauss1;
    vec4 modifier = vec4(0, 0, finalGauss * 1.25,  0);

    gl_FragColor = (finalColor * .9 + modifier) * exposure;
}