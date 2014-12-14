package cs4620.gl;

import java.util.ArrayList;

import cs4620.common.Mesh;
import cs4620.common.Scene;
import cs4620.common.SceneObject;
import egl.math.Matrix4;
import egl.math.Vector3;

public class AnimationObject{
	private final Scene scene;
	private Matrix4 tInverse;

	public AnimationObject(Scene scene) {
		this.scene = scene;
		this.tInverse = scene.objects.get("Star").transformation.clone().invert();
		//scene.objects.get("Star")
	}
	
	/**Rotates an object given that objects mesh and material
	 * @param String name : the name of the object
	 * @param Float rotX : rotation about the x axis
	 * @param Float rotY : rotation about the y axis
	 * @param Float rotZ : rotation about the z axis
	 * */
	public void rotateObject(String name, float rotX, float rotY, float rotZ) {
		scene.objects.get(name).addRotation(new Vector3(rotX, rotY, rotZ));
	}
	
	public void changeTexture(String name, String newTexture) {
		scene.objects.get(name).setMaterial(newTexture);
	}
	
	public void wobbleRadius(String name, double theta) {
		double cos = 1 + .05 * Math.cos(theta);
		SceneObject star = scene.objects.get(name);
		star.addScale(new Vector3((float)cos));;
	}
	
	public void reset(String name) {
		SceneObject star = scene.objects.get(name);
		
		star.transformation.mulAfter(this.tInverse);
	}
	
}
