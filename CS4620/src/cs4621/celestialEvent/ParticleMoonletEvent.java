package cs4621.celestialEvent;
import blister.GameTime;
import cs4620.common.SceneObject;
import cs4620.scene.SceneApp;
import egl.math.Matrix4;
import egl.math.Vector3;

public class ParticleMoonletEvent extends CelestialEvent {
	
	//public int iNumParticles = 200;
	public double dStartTime = 0;
	public int iObjIndexStart, iObjIndexEnd;
	public static float GConstant = 3;
	public float fStarRadius = 1;
	
	public ParticleMoonletEvent(int indexStart, int indexEnd){
		//this.iNumParticles = iNumParticles;
		dStartTime = 0;
		iObjIndexStart = indexStart;
		iObjIndexEnd = indexEnd;
		//GConstant = 0.67f;//6.67e-2f;
	}
	
	public ParticleMoonletEvent(int indexStart, int indexEnd, float fRadius){
		this(indexStart, indexEnd);
		fStarRadius = fRadius;
	}
	
	public void trigger(SceneApp app, GameTime gameTime){
		// now assume that there is only 1 star in the center:
		float et = (float)gameTime.elapsed;
		for (int i = iObjIndexStart; i <= iObjIndexEnd; i++){
			SceneObject sco = app.scene.objects.get("particle_"+i);
			
			Vector3 v3_speed = sco.v3_speed;
			sco.addTranslation(new Vector3(v3_speed.x*et, v3_speed.y*et, v3_speed.z*et));
			Vector3 v3_accel = (sco.transformation.getTrans());
			float fDistanceSq = (float)(Math.pow(v3_accel.x, 2) + Math.pow(v3_accel.y, 2) + Math.pow(v3_accel.z, 2));
			if (fDistanceSq < fStarRadius*fStarRadius){
				sco.transformation.setIdentity();
				sco.v3_speed.set(0);
				//System.out.println("Eaten 1! fStarRadius = "+fStarRadius);
				continue;
			}
			v3_accel.normalize();
			
			v3_accel.mul(-GConstant/fDistanceSq);
			
			v3_speed.add(v3_accel.mul(et));
			//System.out.println("a = "+v3_accel.len()+". GConstant = "+GConstant+". dis sq ="+fDistanceSq);
			//friction in y direction
			//v3_speed.y *= 0.98;
		}
		
		
		
	}
	

}
