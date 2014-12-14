package cs4621.celestialEvent;

import blister.GameTime;
import cs4620.scene.SceneApp;

public abstract class CelestialEvent {
	
	//public boolean bHappening;
	public boolean bEnabled = false;
	public static float fSpeed = 1f;
	
	public CelestialEvent(){
		//bHappening = false;
		bEnabled = false;
	}
	
	public void stopEvent(){bEnabled = false;}
	
	public void startEvent(){bEnabled = true;}
	
	public abstract void trigger(SceneApp app, GameTime gameTime);
		
	
}
