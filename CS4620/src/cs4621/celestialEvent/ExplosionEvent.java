package cs4621.celestialEvent;
import blister.GameTime;
import cs4620.common.Scene;
import cs4620.common.SceneObject;
import cs4620.scene.SceneApp;
import egl.math.Matrix4;
import egl.math.Vector3;

public class ExplosionEvent extends CelestialEvent {
	public int iObjIndexStart, iObjIndexEnd;
	private boolean moveToOrigin = true;
	
	public ExplosionEvent(int indexStart, int indexEnd){
		iObjIndexStart = indexStart;
		iObjIndexEnd = indexEnd;
	}
	
	public void trigger(SceneApp app, GameTime gameTime){
		for (int i = iObjIndexStart; i < iObjIndexEnd; i++){
			SceneObject particle = app.scene.objects.get("particle_"+i);
			
			if (moveToOrigin) {
				float theta =(float) (Math.random() * 2 * Math.PI),
						phi = (float) (Math.random() * 2 * Math.PI);
				particle.v3_speed.x = (float) (Math.cos(phi) * Math.sin(theta));
				particle.v3_speed.y = (float) (Math.sin(phi) * Math.sin(theta));
				particle.v3_speed.z = (float) (Math.cos(theta));
				particle.transformation.setIdentity();
				
//				particle.v3_speed.set((float)(Math.random() - .5f), (float)(Math.random() - .5f), (float)(Math.random() - .5f));
				particle.v3_speed.mul((float) (Math.random() + 1) * 5);
				particle.addRotation(new Vector3((float)Math.random() * 360, (float)Math.random() * 360, (float) Math.random() * 360));
				particle.addScale(new Vector3(5f).add((float)Math.random() * 5));
			}else {
				particle.addTranslation(particle.v3_speed.clone().mul((float)gameTime.elapsed));
			}
			
		}
		moveToOrigin = false;
			
	}
	

}
