package cs4621.celestialEvent;
import blister.GameTime;
import cs4620.common.SceneObject;
import cs4620.scene.SceneApp;
import egl.math.Matrix4;
import egl.math.Vector3;

public class DeepImpactEvent extends CelestialEvent {
	
	//public int iNumParticles = 200;
	public double dStartTime = 0;
	public int iObjIndex;
	public int iDebrisStart, iDebrisEnd;
	public SceneObject soComet = null;
	public static float GConstant = 3f;
	public Vector3 target = new Vector3(0,0,0);
	public boolean bHappening = false;
	public boolean bImpact = false;
	public float fImpactSpeed = 0;
	public float fTargetRadius = 1;
	public float fSpeedMultiplier = (float)(Math.random()/3+0.1);
	
	
	public Vector3 dir = new Vector3(0,0,0);
	public Vector3 origin = new Vector3(0,0,0);
	
	public DeepImpactEvent(int index, int iDebrisStart, int iDebrisEnd, float fRadius){
		//this.iNumParticles = iNumParticles;
		dStartTime = 0;
		iObjIndex = index;
		this.iDebrisStart = iDebrisStart;
		this.iDebrisEnd = iDebrisEnd;
		bHappening = false;
		this.fTargetRadius = fRadius;
	}
	
	public DeepImpactEvent(int index, int iDebrisStart, int iDebrisEnd, float fRadius, Vector3 target){
		this(index, iDebrisStart, iDebrisEnd, fRadius);
		this.target = target;
	}
	
	public void trigger(SceneApp app, GameTime gameTime){
		// now assume that there is only 1 star in the center:
		if (bHappening){
			float et = (float)gameTime.elapsed;
			if (bImpact){
				
				if (gameTime.total-dStartTime > 10){
					bHappening = false;
					bImpact = false;
					for (int i = iDebrisStart; i <= iDebrisEnd; i++){
						app.scene.objects.get("debris_"+i).transformation.set(new Matrix4());
					}
					return;
				}
				
				
				//et = et*fSpeed;
				//Vector3 v3_speed = new Vector3((float)(-5*Math.cos((gameTime.total-dStartTime)/3*2*Math.PI) + 5), (float)(5*Math.sin((gameTime.total-dStartTime)/3*2*Math.PI)), 0f);
				et = et*fSpeedMultiplier;
				//try {
					//app.scene.objects.get("Debris_1").transformation.set(new Matrix4());
					//app.scene.objects.get("Debris_1").addScale(new Vector3(0.1f, 0.1f, 0.1f));
					
					//app.scene.objects.get("Debris_1").addTranslation(v3_speed);
					
					
					for (int i = iDebrisStart; i <= iDebrisEnd; i++){
						SceneObject sco = app.scene.objects.get("debris_"+i);
						Vector3 v3_speed = sco.v3_speed;
						sco.addTranslation(new Vector3(v3_speed.x*et, v3_speed.y*et, v3_speed.z*et));
					}
				
				return;
			}
			
			if (soComet.transformation.getTrans().len() < fTargetRadius){ 
				bImpact = true;
				soComet.transformation.setIdentity(); 

				fSpeedMultiplier = (float)(Math.random()/3+0.1);
				
				Vector3 v3_loc = new Vector3(origin);
				v3_loc.normalize();
				//System.out.println("----------------Deep impact event happening!--------");
				
				//System.out.println("Target = "+target+".\n dir = "+dir+".\n v3_loc ="+v3_loc);
				//System.out.println("origin = "+origin);
				
				float fSpeed_coeff = 10;
				Vector3 v3_speed = new Vector3(v3_loc);
				v3_speed.mul(fSpeed_coeff);
				
				float fRand = (float)Math.random();
				for (int i = iDebrisStart; i <= iDebrisEnd; i++){
					SceneObject sco = app.scene.objects.get("debris_"+i);
					sco.addRotation(new Vector3((float)Math.random()*100f, (float)Math.random()*100f, (float)Math.random()*100f));
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
					v3_perp.mul((float)Math.random()*5);
					
					fRand = (float)Math.random();
					sco.v3_speed.set(v3_speed.x+v3_perp.x, v3_speed.y+v3_perp.y, v3_speed.z+v3_perp.z);
					sco.v3_speed.mul(fRand+0.1f);
					
					
				}
				
				
				dStartTime = gameTime.total;
				
				return;
				
			}
			
			dir.normalize().mul(fImpactSpeed*et);
			soComet.addTranslation(dir);
			
			
		} else {
			float fRand = (float)Math.random();
			if (fRand < 1/60f){
				// setting up a new ejection event:
				
				bHappening = true;
				soComet = app.scene.objects.get("comet_"+iObjIndex);
				
				
				// generate a random sign
				float fSignX = (float)Math.random();
				if(fSignX < 0.5){fSignX = -1;} else {fSignX = 1;}
				float fSignY = (float)Math.random();
				if(fSignY < 0.5){fSignY = -1;} else {fSignY = 1;}
				float fSignZ = (float)Math.random();
				if(fSignZ < 0.5){fSignZ = -1;} else {fSignZ = 1;}
				
				float fScaleFactor = (float)(Math.random()/8+0.1);
				soComet.transformation.set(Matrix4.createScale(fScaleFactor));// scale the size of the Comet
				
				float fDistanceFactor = 30;
				origin.set((float)(Math.random()*fDistanceFactor+fDistanceFactor)*(fSignX)/fScaleFactor, 
						(float)(Math.random()*fDistanceFactor+fDistanceFactor)*(fSignY)/fScaleFactor, 
						(float)(Math.random()*fDistanceFactor+fDistanceFactor)*(fSignZ)/fScaleFactor);
				soComet.transformation.mulBefore(Matrix4.createTranslation(origin));
				fImpactSpeed = (float)(Math.random()*5) + 20; 
				dir.set(target.clone().sub(origin));
				
			}
		}
		
		
		
	}
	

}
