package cs4620.gl;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cs4620.common.Scene;
import cs4620.common.SceneObject;
import cs4620.common.event.SceneTransformationEvent;
import cs4621.celestialEvent.ParticleMoonletEvent;
import egl.math.Matrix4;
import egl.math.Vector3;

public class CameraController {
	protected final Scene scene;
	public RenderCamera camera;
	public final RenderEnvironment rEnv;
	
	protected boolean prevFrameButtonDown = false;
	protected int prevMouseX, prevMouseY;
	
	protected boolean orbitMode = true;
	
	public int E_turnRate = 0, Q_turnRate = 0, up_turnRate = 0, 
					down_turnRate = 0, right_turnRate = 0, left_turnRate = 0;
	
	public static Matrix4 endur_exterior_cam_trans = Matrix4.createTranslation(0,0,1);
	
	// thrust of Endurance in Endurance's own frame:
	public static Vector3 endur_throttle = new Vector3(0,0,0);
	
	public static float fAccelCoeff = 2000;
	
	public static float fEndurSpeedCoeff = 0.01f;

	
	public CameraController(Scene s, RenderEnvironment re, RenderCamera c) {
		scene = s;
		rEnv = re;
		camera = c;
	}
	
	/**
	 * Update the camera's transformation matrix in response to user input.
	 * 
	 * Pairs of keys are available to translate the camera in three direction oriented to the camera,
	 * and to rotate around three axes oriented to the camera.  Mouse input can also be used to rotate 
	 * the camera around the horizontal and vertical axes.  All effects of these controls are achieved
	 * by altering the transformation stored in the SceneCamera that is referenced by the RenderCamera
	 * this controller is associated with.
	 * 
	 * @param et  time elapsed since previous frame
	 */
	public void update(double et) {
		Vector3 motion = new Vector3();
		Vector3 rotation = new Vector3();
		
		Vector3 camSpeed = new Vector3(camera.camSpeed);
		//System.out.println(camSpeed);
		
		
		
		if (Math.abs(camSpeed.x) < 0.0001){camSpeed.x = 0;}
		camSpeed.x -= (float)(camSpeed.x * 9.2 * et);
		if (Math.abs(camSpeed.y) < 0.0001){camSpeed.y = 0;}
		camSpeed.y -= (float)(camSpeed.y * 9.2 * et);
		if (Math.abs(camSpeed.z) < 0.0001){camSpeed.z = 0;}
		camSpeed.z -= (float)(camSpeed.z * 9.2 * et);
		
		
		
		// Endurance's location, but not rotation, scale:
		SceneObject soEndurance = scene.objects.get("Endurance_parent_object");
		
		//System.out.println("-----------------------------------------------");
		//System.out.println(endur_throttle);
		
		
		
		if (Scene.bBegin){
			
			//Vector3 endurance_Speed = new Vector3(soEndurance.v3_speed);
			if (!(soEndurance.transformation.getTrans().len() < 2.5)){
				soEndurance.addTranslation(soEndurance.v3_speed.clone().mul((float)et*fEndurSpeedCoeff));
				endur_exterior_cam_trans.mulAfter(Matrix4.createTranslation((soEndurance.v3_speed.clone().mul((float)et*fEndurSpeedCoeff))));
			}
			
			
			Vector3 endur_Accel = new Vector3();
			Vector3 endur_loc = new Vector3(soEndurance.transformation.getTrans());
			
			//SceneObject soEndurance_self = scene.objects.get("Endurance_object");
			endur_Accel.set(rEnv.findObject(scene.objects.get("Endurance_object")).mWorldTransform.mulDir(endur_throttle.clone()));
			//System.out.println(endur_Accel);
			endur_Accel.add(endur_loc.mul((float)(-ParticleMoonletEvent.GConstant/Math.pow(endur_loc.len(), 3))));
			// endur_loc.set(0,0,-iForwardThrottle);
			soEndurance.transformation.clone().invert().mulDir(endur_loc);
			// now endur_loc = acceleration of Endurance in world coordinates:
			// endur_Accel.add(endur_loc.mul((float)et));
			soEndurance.v3_speed.add(endur_Accel.mul((float)et));
			
			
			RenderObject roc = rEnv.findObject(scene.objects.get("Endurance_object"));
			RenderObject rop = roc.parent;
			rop.mWorldTransform.set(soEndurance.transformation);
			
			roc.mWorldTransform.set(rop.mWorldTransform.clone().mulBefore(roc.sceneObject.transformation));
		}
		
		
		
		if (Scene.bFlightControl){
			// flight mode:
			if(Keyboard.isKeyDown(Keyboard.KEY_W)){if (endur_throttle.z < 100){endur_throttle.z += 60*(float)et;}}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_S)){if (endur_throttle.z > 0){endur_throttle.z = Math.max(endur_throttle.z-60*(float)et, 0);}};
				
				
		} else {
			// viewing mode:
			if(Keyboard.isKeyDown(Keyboard.KEY_W)&& !Scene.bFollow_cam_active) {
				if(camSpeed.z > 0){camSpeed.set(0,0,0);}else{ 
				if(camSpeed.z > -60)camSpeed.add(0, 0, Math.max(-60, (float)(fAccelCoeff * et * (camSpeed.z*et/5-0.05f))));}}	
			if(Keyboard.isKeyDown(Keyboard.KEY_S)&& !Scene.bFollow_cam_active) {
				if(camSpeed.z < 0){camSpeed.set(0,0,0);}else{ 
				if(camSpeed.z < 60)camSpeed.add(0, 0, Math.min(60, (float)(fAccelCoeff * et * (camSpeed.z*et/5+0.05f))));}}
		}
			if(Keyboard.isKeyDown(Keyboard.KEY_A)&& !Scene.bFollow_cam_active) {
				if(camSpeed.x > 0){camSpeed.set(0,0,0);}else{ 
				if(camSpeed.x > -30)camSpeed.add(Math.max(-30, (float)(fAccelCoeff * et * (camSpeed.x*et/5-0.05f))), 0, 0);}}	
			if(Keyboard.isKeyDown(Keyboard.KEY_D)&& !Scene.bFollow_cam_active) {
				if(camSpeed.x < 0){camSpeed.set(0,0,0);}else{ 
				if(camSpeed.x < 30)camSpeed.add(Math.min(30, (float)(fAccelCoeff * et * (camSpeed.x*et/5+0.05f))), 0, 0);}}
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)&& !Scene.bFollow_cam_active) {
				if(camSpeed.y > 0){camSpeed.set(0,0,0);}else{ 
				if(camSpeed.y > -30)camSpeed.add(0, Math.max(-30, (float)(fAccelCoeff * et * (camSpeed.y*et/5-0.05f))), 0);}}	
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)&& !Scene.bFollow_cam_active) {
				if(camSpeed.y < 0){camSpeed.set(0,0,0);}else{ 
				if(camSpeed.y < 30)camSpeed.add(0, Math.min(30, (float)(fAccelCoeff * et * (camSpeed.y*et/5+0.05f))), 0);}}
		
		
		
		
		boolean thisFrameButtonDown = Mouse.isButtonDown(0) && !(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL));
		int thisMouseX = Mouse.getX(), thisMouseY = Mouse.getY();
		if (thisFrameButtonDown && prevFrameButtonDown) {
			rotation.add(0, -0.1f * (thisMouseX - prevMouseX), 0);
			rotation.add(0.1f * (thisMouseY - prevMouseY), 0, 0);
		}
		prevFrameButtonDown = thisFrameButtonDown;
		prevMouseX = thisMouseX;
		prevMouseY = thisMouseY;
		
		
		RenderObject parent;
		Matrix4 pMat;
		boolean bExterior_cam_active = Scene.bExterior_cam_active;
		float fSpeedMult = 80;
		float fRotateMult = 60;
		if (bExterior_cam_active){
			fSpeedMult *= 0.01f;
			fRotateMult *= 0.5f;
			
			//parent = rEnv.findObject(scene.objects.get("Endurance_parent_object"));
			parent = rEnv.findObject(scene.objects.get(camera.sceneObject.parent));
			pMat = parent == null ? new Matrix4() : parent.mWorldTransform;
		} else {
			parent = rEnv.findObject(scene.objects.get(camera.sceneObject.parent));
			pMat = parent == null ? new Matrix4() : parent.mWorldTransform;
		}
		
		
		camera.camSpeed.set(camSpeed);
		
		motion.set(camera.camSpeed.clone().mul((float)(et)));
		//System.out.println(motion);
		if(motion.len() > 0.00001) {
			//motion.normalize();
			motion.mul(fSpeedMult * (float)et);
			if (bExterior_cam_active){
				translate(pMat, endur_exterior_cam_trans, motion);
			} else {
				translate(pMat, camera.sceneObject.transformation, motion);
			}
		}
		
		//if (bExterior_cam_active){camera.sceneObject.transformation.set(endur_exterior_cam_trans.clone().mulAfter(pMat));}
		
		
		
		// Now rotation:
		float turnRate_Coeff = 0.03f;
		if(Keyboard.isKeyDown(Keyboard.KEY_E)&& !Scene.bFollow_cam_active) { E_turnRate++;} else {E_turnRate/=1.05;}
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)&& !Scene.bFollow_cam_active) { Q_turnRate++;} else {Q_turnRate/=1.05;} 
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)&& !Scene.bFollow_cam_active) { up_turnRate++;} else {up_turnRate/=1.05;} 
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)&& !Scene.bFollow_cam_active) { down_turnRate++;} else {down_turnRate/=1.05;}
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)&& !Scene.bFollow_cam_active) { right_turnRate++;} else {right_turnRate/=1.05;} 
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)&& !Scene.bFollow_cam_active) { left_turnRate++;} else {left_turnRate/=1.05;}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_O)) { orbitMode = true; } 
		if(Keyboard.isKeyDown(Keyboard.KEY_F)) { orbitMode = false; } 		
			
		
		rotation.add(0, 0, -turnRate_Coeff*E_turnRate); 
		rotation.add(0, 0, turnRate_Coeff*Q_turnRate); 
		rotation.add(turnRate_Coeff*up_turnRate, 0, 0); 
		rotation.add(-turnRate_Coeff*down_turnRate, 0, 0); 
		rotation.add(0, -turnRate_Coeff*right_turnRate, 0); 
		rotation.add(0, turnRate_Coeff*left_turnRate, 0); 
		
		turnRate_Coeff = 30;// turn rate upper limit
		if (E_turnRate > turnRate_Coeff){E_turnRate--;} else if (E_turnRate < 1) {E_turnRate = 1;}
		if (Q_turnRate > turnRate_Coeff){Q_turnRate--;} else if (Q_turnRate < 1) {Q_turnRate = 1;}
		if (up_turnRate > turnRate_Coeff){up_turnRate--;} else if (up_turnRate < 1) {up_turnRate = 1;}
		if (down_turnRate > turnRate_Coeff){down_turnRate--;} else if (down_turnRate < 1) {down_turnRate = 1;}
		if (left_turnRate > turnRate_Coeff){left_turnRate--;} else if (left_turnRate < 1) {left_turnRate = 1;}
		if (right_turnRate > turnRate_Coeff){right_turnRate--;} else if (right_turnRate < 1) {right_turnRate = 1;}
		
		
		
		
		if(rotation.lenSq() > 0.001) {
			rotation.mul((float)(fRotateMult * et));
			if (bExterior_cam_active){
				rotateAround(pMat, endur_exterior_cam_trans, rotation, soEndurance.transformation.getTrans());
			} else {rotate(pMat, camera.sceneObject.transformation, rotation);}
			
		}
		
		//endur_exterior_cam_trans.mulAfter(soEndurance.transformation);
		if (bExterior_cam_active){camera.sceneObject.transformation.set(endur_exterior_cam_trans);}
		
		if (Scene.bFollow_cam_active){
			RenderObject ro = rEnv.findObject(scene.objects.get("Endurance_exterior_cam"));
			
			ro = rEnv.findObject(scene.objects.get("Endurance_exterior_cam"));
			ro.mWorldTransform.set(ro.sceneObject.transformation).mulAfter(ro.parent.mWorldTransform);
			ro.mWorldTransformIT.set(ro.mWorldTransform.getAxes()).invert().transpose();
			
			camera.sceneObject.transformation.set(ro.mWorldTransform);
			camera.sceneObject.transformation.mulBefore(Matrix4.createScale(200));
			camera.mWorldTransform.set(camera.sceneObject.transformation);
		}
		
		
		
		
		//RenderObject roEndurance = rEnv.findObject(scene.objects.get("Endurance_object"));
		//Matrix4 endurance_trans = roEndurance.mWorldTransform;
		
		
		
		
		//scene.objects.get("k").transformation
		//System.out.println("1------------------");
		//System.out.println(camera.sceneObject.transformation);
		//System.out.println("2------------------");
		//System.out.println(this.rEnv.findObject(camera.sceneObject).mWorldTransform);
		//System.out.println("3------------------");
		//System.out.println(scene.objects.get("Endurance_parent_object").transformation);
		//System.out.println("4------------------");
		//System.out.println(this.rEnv.findObject(scene.objects.get("Endurance_parent_object")).mWorldTransform);
		
		//(camController.rEnv.findObject(app.scene.objects.get("Endurance_parent_object")).mWorldTransform.clone().mulAfter(endurance_exterior_cam));
		
		
		
		
		
		
		
		
		//camera.parent.sceneObject.transformation.mulAfter(motionM);

		

		/*
		
				Vector3 rotation = new Vector3();
		cam_accel.set(0, 0, 0, 0);
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) { if(thrust < 100){thrust += 1;} }
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) { if(thrust > 0){thrust -= 1;} }
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) { cam_accel.x += -10f; }
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) { cam_accel.x += 10f; }
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) { cam_accel.y += -20f; }
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) { cam_accel.y += 200f; }
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) { cam_accel.x = 0; cam_accel.y = 0; cam_accel.z = 0; world_speed.x = 0; world_speed.y = 0; world_speed.z = 0; thrust = 0;}
		
		Vector4 cam_speed = new Vector4(world_speed);
		Matrix4 mTran = new Matrix4(camera.parent.sceneObject.transformation);
		mTran.invert();
		mTran.mul(cam_speed);
		
		cam_accel.z -= thrust;
		
		//cam_speed.add(cam_accel.mul((float)et));
		cam_accel.add(side_dragCoeff*Math.abs(cam_speed.x)*-cam_speed.x, liftCoeff*cam_speed.z*cam_speed.z + vertical_dragCoeff*Math.abs(cam_speed.y)*-cam_speed.y, forward_dragCoeff*Math.abs(cam_speed.z)*-cam_speed.z, 0);
		cam_speed.add(cam_accel.mul((float)et));
		
		float speedCoeff = 2f;
		//lFrameCount++;
		System.out.println(cam_speed.toString());
		//System.out.println("Framecount = "+lFrameCount+". FPS = "+1/et);
		camera.parent.sceneObject.transformation.mul(cam_speed);
		//float pow_x, pow_y, pow_z;
		
		Matrix4 motionM = Matrix4.createTranslation((float)et*cam_speed.x*speedCoeff, (float)et*cam_speed.y*speedCoeff, (float)et*cam_speed.z*speedCoeff);
		
		
		//System.out.println(motionM.toString());
		//motionM.mulAfter(camera.sceneObject.transformation);
		//System.out.println("------------------");
		//System.out.println(motionM.toString());
		
		rotation.add(0, -0.2f*turnRate_Coeff*E_turnRate, 0); 
		rotation.add(0, 0.2f*turnRate_Coeff*Q_turnRate, 0); 
		rotation.add(-1f*turnRate_Coeff*up_turnRate, 0, 0); 
		rotation.add(1f*turnRate_Coeff*down_turnRate, 0, 0); 
		rotation.add(0, 0, -1f*turnRate_Coeff*right_turnRate); 
		rotation.add(0, 0, 1f*turnRate_Coeff*left_turnRate); 
		
		turnRate_Coeff = 120;
		
		/*
		System.out.println("-----------------------");
		System.out.println(E_turnRate);
		System.out.println(Q_turnRate);
		System.out.println(up_turnRate);
		System.out.println(down_turnRate);
		System.out.println(left_turnRate);
		System.out.println(right_turnRate);
		/
		
		if (E_turnRate > turnRate_Coeff){E_turnRate--;} else if (E_turnRate < 1) {E_turnRate = 1;}
		if (Q_turnRate > turnRate_Coeff){Q_turnRate--;} else if (Q_turnRate < 1) {Q_turnRate = 1;}
		if (up_turnRate > turnRate_Coeff){up_turnRate--;} else if (up_turnRate < 1) {up_turnRate = 1;}
		if (down_turnRate > turnRate_Coeff){down_turnRate--;} else if (down_turnRate < 1) {down_turnRate = 1;}
		if (left_turnRate > turnRate_Coeff){left_turnRate--;} else if (left_turnRate < 1) {left_turnRate = 1;}
		if (right_turnRate > turnRate_Coeff){right_turnRate--;} else if (right_turnRate < 1) {right_turnRate = 1;}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_O)) { orbitMode = true; } 
		if(Keyboard.isKeyDown(Keyboard.KEY_F)) { orbitMode = false; } 
		
		boolean thisFrameButtonDown = Mouse.isButtonDown(0) && !(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL));
		int thisMouseX = Mouse.getX(), thisMouseY = Mouse.getY();
		if (thisFrameButtonDown && prevFrameButtonDown) {
			rotation.add(0, -0.1f * (thisMouseX - prevMouseX), 0);
			rotation.add(0.1f * (thisMouseY - prevMouseY), 0, 0);
		}
		prevFrameButtonDown = thisFrameButtonDown;
		prevMouseX = thisMouseX;
		prevMouseY = thisMouseY;
		
		//RenderObject parent = rEnv.findObject(scene.objects.get(camera.sceneObject.parent));
		
		

		if(rotation.lenSq() > 0.01) {
			rotation.mul((float)(30.0 * et));
			rotate(camera.parent.parent.mWorldTransform, camera.parent.sceneObject.transformation, rotation);
		}
		scene.sendEvent(new SceneTransformationEvent(camera.sceneObject));
		

		camera.parent.sceneObject.transformation.mulAfter(motionM);
		
		
		world_speed.set(cam_speed);
		world_speed.y += (float)et*-gravity;
		//Vector3 loc = camera.parent.sceneObject.transformation.getTrans();
		//System.out.println(loc);
		//if (loc.x > 2000 || loc.x < -2000){world_speed.x = -world_speed.x;}
		//if (loc.y > 1000 || loc.y < 200){world_speed.y = -world_speed.y;}
		//if (loc.z > 2000 || loc.z < -2000){world_speed.z = -world_speed.z;}
		
		
		
		*/
		
		
		
		
		
		//System.out.println(ParticleMoonletEvent.GConstant);
		
		
		
		
		scene.sendEvent(new SceneTransformationEvent(camera.sceneObject));
		
		
		
		
		
	}

	/**
	 * Apply a rotation to the camera.
	 * 
	 * Rotate the camera about one ore more of its local axes, by modifying <b>transformation</b>.  The 
	 * camera is rotated by rotation.x about its horizontal axis, by rotation.y about its vertical axis, 
	 * and by rotation.z around its view direction.  The rotation is about the camera's viewpoint, if 
	 * this.orbitMode is false, or about the world origin, if this.orbitMode is true.
	 * 
	 * @param parentWorld  The frame-to-world matrix of the camera's parent
	 * @param transformation  The camera's transformation matrix (in/out parameter)
	 * @param rotation  The rotation in degrees, as Euler angles (rotation angles about x, y, z axes)
	 */
	protected void rotate(Matrix4 parentWorld, Matrix4 transformation, Vector3 rotation) {
		// TODO#A3 SOLUTION START
		
		rotation = rotation.clone().mul((float)(Math.PI / 180.0));
		Matrix4 mRot = Matrix4.createRotationX(rotation.x);
		mRot.mulAfter(Matrix4.createRotationY(rotation.y));
		mRot.mulAfter(Matrix4.createRotationZ(rotation.z));

		if (orbitMode) {
			Vector3 rotCenter = new Vector3(0,0,0);
			transformation.clone().invert().mulPos(rotCenter);
			parentWorld.clone().invert().mulPos(rotCenter);
			mRot.mulBefore(Matrix4.createTranslation(rotCenter.clone().negate()));
			mRot.mulAfter(Matrix4.createTranslation(rotCenter));
		}
		transformation.mulBefore(mRot);
		
		// SOLUTION END
	}
	
	/*
	protected void rotateAround(Matrix4 parentWorld, Matrix4 transformation, Vector3 rotation, Vector3 center) {
		
		rotation = rotation.clone().mul((float)(Math.PI / 180.0));
		Matrix4 mRot = Matrix4.createRotationX(rotation.x);
		mRot.mulAfter(Matrix4.createRotationY(rotation.y));
		mRot.mulAfter(Matrix4.createRotationZ(rotation.z));

		if (orbitMode) {
			Vector3 rotCenter = new Vector3(center);
			transformation.mulAfter(parentWorld);
			transformation.mulAfter(Matrix4.createTranslation(rotCenter.negate()));
			transformation.mulAfter(mRot);
			transformation.mulAfter(Matrix4.createTranslation(center));
			transformation.mulAfter(parentWorld.clone().invert());
			
			
			//mRot.mulBefore(Matrix4.createTranslation(rotCenter.clone().negate()));
			//mRot.mulAfter(Matrix4.createTranslation(rotCenter));
		} else {
			transformation.mulBefore(mRot);
		}
	}
	*/
	
	protected void rotateAround(Matrix4 parentWorld, Matrix4 transformation, Vector3 rotation, Vector3 center) {
		
		rotation = rotation.clone().mul((float)(Math.PI / 180.0));
		Matrix4 mRot = Matrix4.createRotationX(rotation.x);
		mRot.mulAfter(Matrix4.createRotationY(rotation.y));
		mRot.mulAfter(Matrix4.createRotationZ(rotation.z));

		if (orbitMode) {
			Vector3 rotCenter = new Vector3(center);
			transformation.clone().invert().mulPos(rotCenter);
			parentWorld.clone().invert().mulPos(rotCenter);
			mRot.mulBefore(Matrix4.createTranslation(rotCenter.clone().negate()));
			mRot.mulAfter(Matrix4.createTranslation(rotCenter));
		}
		transformation.mulBefore(mRot);
		
	}
	
	/**
	 * Apply a translation to the camera.
	 * 
	 * Translate the camera by an offset measured in camera space, by modifying <b>transformation</b>.
	 * @param parentWorld  The frame-to-world matrix of the camera's parent
	 * @param transformation  The camera's transformation matrix (in/out parameter)
	 * @param motion  The translation in camera-space units
	 */
	protected void translate(Matrix4 parentWorld, Matrix4 transformation, Vector3 motion) {
		// TODO#A3 SOLUTION START

		Matrix4 mTrans = Matrix4.createTranslation(motion);
		
		transformation.mulBefore(mTrans);
		
		// SOLUTION END
	}
}
