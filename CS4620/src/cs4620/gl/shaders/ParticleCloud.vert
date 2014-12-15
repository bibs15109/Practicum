#version 120

// Note: We multiply a vector with a matrix from the left side (M * v)!
// mProj * mView * mWorld * pos

// RenderCamera Input
uniform mat4 mViewProjection;

// RenderObject Input
uniform mat4 mWorld;
uniform mat3 mWorldIT;


// RenderMesh Input
attribute vec4 vPosition; // Sem (POSITION 0)
attribute vec3 vNormal; // Sem (NORMAL 0)
attribute vec2 vUV; // Sem (TEXCOORD 0)

varying vec2 fUV;
varying vec3 fN; // normal at the vertex
varying vec4 worldPos; // vertex position in world-space coordinates

void main() {
	// Calculate Point In World Space
	//float normal = vNormal.x;
	//if (gameTime < 10){
	//	if (normal > 10){
	//		normal = 0;
	//	}
	//} else if(gameTime < 20){
	//	if (normal > 20){normal = 0;}
	//	else {normal = normal - 10.0;}
	//} else {normal = normal - 20.0;}
	//worldPos = mWorld * vPosition + vec4(gameTime*normal/5, 0.0, 0.0, 0.0);
	// Calculate Projected Point
	//gl_Position = mViewProjection * worldPos;
	
	// We have to use the inverse transpose of the world transformation matrix for the normal
	//fN = normalize((mWorldIT * vNormal).xyz);
	//fUV = vUV;
}