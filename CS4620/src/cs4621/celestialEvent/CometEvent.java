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
	public SceneObject soComet = null;
	public static float GConstant = 3f;
	public Vector3 target = new Vector3(0,0,0);
	public boolean bHappening = false;
	public float fImpactSpeed = 0;
	
	private Vector3 dir = new Vector3(0,0,0);
	
	public CometEvent(int index){
		//this.iNumParticles = iNumParticles;
		dStartTime = 0;
		iObjIndex = index;
		bHappening = false;
	}
	
	public CometEvent(int index, Vector3 target){
		this(index);
		this.target = target;
	}
	
	public void trigger(SceneApp app, GameTime gameTime){
		// now assume that there is only 1 star in the center:
		if (bHappening){
			float et = (float)gameTime.elapsed;
			if (soComet.transformation.getTrans().len() > 300){ bHappening = false; soComet.transformation.setIdentity(); return;}
			
			dir.normalize().mul(fImpactSpeed*et);
			soComet.addTranslation(dir);
			
			
		} else {
			float fRand = (float)Math.random();
			if (fRand < 1/60f){
				// setting up a new ejection event:
				
				bHappening = true;
				soComet = app.scene.objects.get("comet_"+iObjIndex);
				soComet.cometColor.set(new Vector3((float)Math.random(), (float)Math.random(), (float)Math.random()));
				target.set((float)(Math.random()*10+3), (float)(Math.random()*10+3), (float)(Math.random()*10+3));
				
				// generate a random sign
				float fSignX = (float)Math.random();
				if(fSignX < 0.5){fSignX = -1;} else {fSignX = 1;}
				float fSignY = (float)Math.random();
				if(fSignY < 0.5){fSignY = -1;} else {fSignY = 1;}
				float fSignZ = (float)Math.random();
				if(fSignZ < 0.5){fSignZ = -1;} else {fSignZ = 1;}
				
				float fScaleFactor = (float)(Math.random()/10+0.1);
				soComet.transformation.set(Matrix4.createScale(fScaleFactor));// scale the size of the Comet
				
				float fDistanceFactor = 50;
				soComet.transformation.mulBefore(Matrix4.createTranslation
									((float)(Math.random()*fDistanceFactor+fDistanceFactor)*(fSignX)/fScaleFactor, 
									(float)(Math.random()*fDistanceFactor+fDistanceFactor)*(fSignY)/fScaleFactor, 
									(float)(Math.random()*fDistanceFactor+fDistanceFactor)*(fSignZ)/fScaleFactor));
				fImpactSpeed = (float)(Math.random()*8) + 10; 
				dir.set(target.sub(soComet.transformation.getTrans()));
				dStartTime = gameTime.total;
			}
		}
		
		
		
	}
	

}
