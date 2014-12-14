package cs4621.celestialEvent;
import blister.GameTime;
import cs4620.common.SceneObject;
import cs4620.scene.SceneApp;
import egl.math.Matrix4;
import egl.math.Vector3;

public class CometEvent extends CelestialEvent {
	
	//public int iNumParticles = 200;
	public double dStartTime = 0;
	public int iObjIndex;
	public SceneObject[] soComet;
	public Matrix4[] oldTrans;
	public static float GConstant = 3f;
	public Vector3 target = new Vector3(0,0,0);
	public boolean bHappening = false;
	public float fImpactSpeed = 0;
	public int length;
	
	private Vector3 dir = new Vector3(0,0,0);
	
	public CometEvent(int index, int ilen){
		//this.iNumParticles = iNumParticles;
		dStartTime = 0;
		iObjIndex = index;
		length = ilen;
		bHappening = false;
		soComet = new SceneObject[length];
		oldTrans = new Matrix4[length];
		for(int i = 0; i < length; i++){
			oldTrans[i] = new Matrix4();
		}
	}
	
	public CometEvent(int index, int ilen, Vector3 target){
		this(index, ilen);
		this.target = target;
	}
	
	public void trigger(SceneApp app, GameTime gameTime){
		// now assume that there is only 1 star in the center:
		if (bHappening){
			float et = (float)gameTime.elapsed;
			if (soComet[0].transformation.getTrans().len() > 300){
				bHappening = false;
				soComet[0].transformation.setIdentity();
				for(int i = 0; i < length-1; i++){
					soComet[i+1].transformation.set(oldTrans[i]);
				}
				return;}
			
			dir.normalize().mul(fImpactSpeed*et);
			soComet[0].addTranslation(dir);
			for(int i = 0; i < length-1; i++){
				soComet[i+1].transformation.set(oldTrans[i]);
			}
			
			for(int i = 0; i < length; i++){
				oldTrans[i].set(soComet[i].transformation);
				soComet[i].transformation.mulBefore(Matrix4.createScale((((float) length-i)*((float) length-i))/(length * length)));
				soComet[i].cometColor.set(((float)length - 1 -i)/(length - 1),((float)length - 1 -i)/(length - 1),1f);
			}
			
			
		} else {
			float fRand = (float)Math.random();
			if (fRand < 1/60f){
				// setting up a new ejection event:
				
				bHappening = true;
				for(int i = 0; i < length; i++){
					soComet[i] = app.scene.objects.get("comet_"+iObjIndex+"_"+i);
				}
				//soComet[0].cometColor.set(new Vector3((float)Math.random(), (float)Math.random(), (float)Math.random()));
				soComet[0].cometColor.set(1f,1f,1f);
				target.set((float)(Math.random()*10+3), (float)(Math.random()*10+3), (float)(Math.random()*10+3));
				
				// generate a random sign
				float fSignX = (float)Math.random();
				if(fSignX < 0.5){fSignX = -1;} else {fSignX = 1;}
				float fSignY = (float)Math.random();
				if(fSignY < 0.5){fSignY = -1;} else {fSignY = 1;}
				float fSignZ = (float)Math.random();
				if(fSignZ < 0.5){fSignZ = -1;} else {fSignZ = 1;}
				
				float fScaleFactor = (float)(Math.random()/20+0.07);
				soComet[0].transformation.set(Matrix4.createScale(fScaleFactor));// scale the size of the Comet
				
				float fDistanceFactor = 50;
				soComet[0].transformation.mulBefore(Matrix4.createTranslation
									((float)(Math.random()*fDistanceFactor+fDistanceFactor)*(fSignX)/fScaleFactor, 
									(float)(Math.random()*fDistanceFactor+fDistanceFactor)*(fSignY)/fScaleFactor, 
									(float)(Math.random()*fDistanceFactor+fDistanceFactor)*(fSignZ)/fScaleFactor));
				fImpactSpeed = (float)(Math.random()*8) + 10; 
				dir.set(target.sub(soComet[0].transformation.getTrans()));
				dStartTime = gameTime.total;
				for(int i = 1; i < length; i++){
					soComet[i].cometColor.set(soComet[0].cometColor);
					soComet[i].transformation.set(soComet[0].transformation);
				}
				for(int i = 0; i < length; i++){
					oldTrans[i].set(soComet[i].transformation);
				}
			}
		}
		
		
		
	}
	

}
