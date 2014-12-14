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

// Shading Information
uniform float dispMagnitude;

varying vec2 fUV;
varying vec3 fN; // normal at the vertex
varying vec4 worldPos; // vertex position in world-space coordinates

void main() {
	vec3 c = vec3(getNormalColor(vUV));
	float disp = (c.x + c.y + c.z) / 3;

	vec3 norm= disp * dispMagnitude * vNormal;
	vec4 transPos = vec4(norm, 0) + vPosition;

	// Calculate Point In World Space
	worldPos = mWorld * transPos;
	// Calculate Projected Point
	gl_Position = mViewProjection * worldPos;

	// We have to use the inverse transpose of the world transformation matrix for the normal
	fN = normalize((mWorldIT * vNormal).xyz);
	fUV = vUV;
}