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
uniform float roughness;

varying vec2 fUV;
varying vec3 fN; // normal at the vertex
varying vec4 worldPos; // vertex position in world coordinates

void main() {
	vec3 N = normalize(fN);
	vec3 V = normalize(worldCam - worldPos.xyz);

	vec4 finalColor = vec4(0.0, 0.0, 0.0, 0.0);

	for (int i = 0; i < numLights; i++) {
		float r = length(lightPosition[i] - worldPos.xyz);
	  	vec3 L = normalize(lightPosition[i] - worldPos.xyz); 
	 	vec3 H = normalize(L + V);

	 	// calculate F
	 	float F = fo + (1 - fo) * pow((1-dot(V, H)), 5);
	 	
	 	// calculate D
	 	float coeff = 1 / (pow(roughness,2) * pow(dot(N,H), 4));
	 	float ex = (pow(dot(N, H), 2) - 1) / (pow(roughness, 2) * pow(dot(N, H), 2));
		float D = coeff * exp(ex);

		// calculate G
		float term1 = (2 * dot(N,H) * dot(N, V)) / dot(V, H);
		float term2 = (2 * dot(N,H) * dot(N, L)) / dot(V, H);
		float Gtemp = min(term1, term2);
		float G = min(1, Gtemp);

		//calculate first chunk
		vec4 chunk1 = ((getSpecularColor(fUV) * F * D * G) / (PI * dot(N, V) * dot(N,L))) + getDiffuseColor(fUV);
		finalColor += ((vec4(lightIntensity[i], 0.0) * chunk1 * max(dot(N, L), 0)) / (r*r)) + (vec4(ambientLightIntensity, 0.0) * getDiffuseColor(fUV));


		
	}
	gl_FragColor = finalColor * exposure;
}