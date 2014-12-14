package cs4621.celestialEvent;
import blister.GameTime;
import cs4620.common.SceneObject;
import cs4620.scene.SceneApp;
import egl.math.Matrix4;
import egl.math.Vector3;

public class ParticleEjectionEvent extends CelestialEvent {
	
	//public int iNumParticles = 200;
	public double dStartTime = 0;
	public int iObjIndexStart, iObjIndexEnd;
	public boolean bHappening;
	public float fSpeedMultiplier;
	
	public ParticleEjectionEvent(int indexStart, int indexEnd){
		//this.iNumParticles = iNumParticles;
		bHappening = false;
		dStartTime = 0;
		iObjIndexStart = indexStart;
		iObjIndexEnd = indexEnd;
		fSpeedMultiplier = 1;
	}
	
	
	
	public void trigger(SceneApp app, GameTime gameTime){
		if (bHappening){
			
			if (gameTime.total-dStartTime > 20){
				bHappening = false;
				for (int i = iObjIndexStart; i <= iObjIndexEnd; i++){
					app.scene.objects.get("particle_"+i).transformation.set(new Matrix4());
				}
			} else {
				float et = (float)gameTime.elapsed;
				//et = et*fSpeed;
				//Vector3 v3_speed = new Vector3((float)(-5*Math.cos((gameTime.total-dStartTime)/3*2*Math.PI) + 5), (float)(5*Math.sin((gameTime.total-dStartTime)/3*2*Math.PI)), 0f);
				et = et*fSpeedMultiplier;
				//try {
					//app.scene.objects.get("Debris_1").transformation.set(new Matrix4());
					//app.scene.objects.get("Debris_1").addScale(new Vector3(0.1f, 0.1f, 0.1f));
					
					//app.scene.objects.get("Debris_1").addTranslation(v3_speed);
					
					
					for (int i = iObjIndexStart; i <= iObjIndexEnd; i++){
						SceneObject sco = app.scene.objects.get("particle_"+i);
						Vector3 v3_speed = sco.v3_speed;
						sco.addTranslation(new Vector3(v3_speed.x*et, v3_speed.y*et, v3_speed.z*et));
					}
					
				//} catch (Exception ex){}
		}
		} else {
			float fRand = (float)Math.random();
			if (fRand < 1/60f){
				// setting up a new ejection event:
				
				bHappening = true;
				fSpeedMultiplier = (float)(Math.random()/3+0.1);
				
				Vector3 v3_loc = new Vector3(1,1,1);
				Matrix4.createRotationX((float)((Math.random()-0.5)*2*Math.PI)).mulDir(v3_loc);
				Matrix4.createRotationY((float)((Math.random()-0.5)*2*Math.PI)).mulDir(v3_loc);
				Matrix4.createRotationZ((float)((Math.random()-0.5)*2*Math.PI)).mulDir(v3_loc);
				v3_loc.normalize();
				
				
				float fSpeed_coeff = 5;
				Vector3 v3_speed = new Vector3(v3_loc);
				v3_speed.mul(fSpeed_coeff);
				
				for (int i = iObjIndexStart; i <= iObjIndexEnd; i++){
					SceneObject sco = app.scene.objects.get("particle_"+i);
					sco.addTranslation(v3_loc);
					
					Vector3 v3_perp1 = new Vector3(-v3_loc.y, v3_loc.x, 0);
					Vector3 v3_perp2 = new Vector3(-v3_loc.z, 0, v3_loc.x);
					Vector3 v3_perp = new Vector3();
					fRand = (float)Math.random()-0.5f;
					v3_perp1.mul(2*fRand);
					fRand = (float)Math.random()-0.5f;
					v3_perp2.mul(2*fRand);
					if (v3_perp1.equals(new Vector3(0,0,0)) ){
						// in case they are both zero vectors
						v3_perp1.set(0.5f, 0.5f, 0.5f);
					}
					
					v3_perp.add(v3_perp1).add(v3_perp2);
					v3_perp.normalize();
					v3_perp.mul((float)Math.random());
					
					fRand = (float)Math.random();
					sco.v3_speed.set(v3_speed.x+v3_perp.x, v3_speed.y+v3_perp.y, v3_speed.z+v3_perp.z);
					sco.v3_speed.mul(fRand+0.1f);
					
				}
				dStartTime = gameTime.total;
			}
		}
	}
	
	

}
