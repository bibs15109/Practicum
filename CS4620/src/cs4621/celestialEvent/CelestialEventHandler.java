package cs4621.celestialEvent;
import java.util.ArrayList;

import blister.GameTime;
import cs4620.common.Scene;
import cs4620.scene.SceneApp;


public class CelestialEventHandler {

	public final Scene scene;
	public ArrayList<CelestialEvent> eventList = new ArrayList<>();
	

	public void getEvents(ArrayList<CelestialEvent> e) {
		e = eventList;
	}
	public void addEvent(CelestialEvent e) {
		eventList.add(e);
	}
	
	
	
	public CelestialEventHandler(Scene s){
		scene = s;
	}
	
	public void update(SceneApp app, GameTime gameTime){
		for (CelestialEvent e: eventList){
			if (!e.bEnabled){continue;}
			e.trigger(app, gameTime);
			
		}
	}
	
	
	
	
}
