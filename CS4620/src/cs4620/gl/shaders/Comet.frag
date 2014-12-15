#version 120

uniform float vTime;
const float PI = 3.1415926535897932384626433832795;

uniform vec3 cometColor;

void main() {    
	vec4 finalColor = vec4(0.0, 0.0, 0.0, 0.0);
	finalColor += vec4(cometColor, 0.0);
	
	//finalColor += vec4(cos(2*PI*vTime)/2, cos(2*PI*vTime)/2, cos(2*PI*vTime)/2, 1.0);
    gl_FragColor = finalColor;
}