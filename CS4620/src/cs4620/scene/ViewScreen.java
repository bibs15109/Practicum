package cs4620.scene;

import java.awt.FileDialog;
import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import blister.GameScreen;
import blister.GameTime;
import blister.ScreenState;
import blister.input.KeyboardEventDispatcher;
import blister.input.KeyboardKeyEventArgs;
import cs4620.common.Scene;
import cs4620.common.SceneObject;
import cs4620.common.event.SceneObjectResourceEvent;
import cs4620.common.event.SceneReloadEvent;
import cs4620.gl.AnimationObject;
import cs4620.gl.CameraController;
import cs4620.gl.GridRenderer;
import cs4620.gl.RenderCamera;
import cs4620.gl.RenderController;
import cs4620.gl.RenderLight;
import cs4620.gl.Renderer;
import cs4620.gl.manip.ManipController;
import cs4620.scene.form.RPMaterialData;
import cs4620.scene.form.RPMeshData;
import cs4620.scene.form.RPTextureData;
import cs4620.scene.form.ScenePanel;
import cs4621.celestialEvent.*;
import egl.GLError;
//import egl.math.Matrix3;
import egl.math.Matrix4;
import egl.math.Vector2;
import egl.math.Vector3;
import egl.math.Vector3d;
import ext.csharp.ACEventFunc;
import ext.java.Parser;

public class ViewScreen extends GameScreen {
	Renderer renderer = new Renderer();
	int cameraIndex = 0;
	boolean pick;
	int prevCamScroll = 0;
	boolean wasPickPressedLast = false;
	boolean showGrid = false;
	
	SceneApp app;
	boolean bBegin = false,
			startExplosion = false;
	// Jason: event handler:
	public CelestialEventHandler celestialEventHandler;
	public float bCentralStarSize = 2;
	public static int iNumComets = 5;
	public static int iCometLength = 30;
	
	protected int camIndex = 1;
	// cam index 1
	public Matrix4 primary_cam = new Matrix4();
	// cam index 2: relative transformation of the Endurance_exterior_cam:
	//public Matrix4 endurance_exterior_cam = new Matrix4();
	
	public RenderLight rl; // the red light;
	public RenderLight eng1, eng2, eng3, eng4;  // engine flames
	public Vector3d engIntensity = new Vector3d(0.000016, 0.000006333, 0);
	
	//public boolean bExterior_cam_active = false;
	public boolean bSwitched = false; // if the camera has been switched back;
	public static boolean bLaserOn = false;
	private static boolean explode = true,
							changeRadius = true,
							shake = false;
	private static double startTime;
	//public boolean bFlightControl = false;
	// End Jason
	
	
	
	
	
	ScenePanel sceneTree;
	RPMeshData dataMesh;
	RPMaterialData dataMaterial;
	RPTextureData dataTexture;
	
	RenderController rController;
	CameraController camController;
	ManipController manipController;
	GridRenderer gridRenderer;
	
	ParticleMoonletEvent moonlet_1;
	DeepImpactEvent impact_1;
	DeepImpactEvent impact_2;
	ExplosionEvent explosion_1;
	
	
	@Override
	public int getNext() {
		return getIndex();
	}
	@Override
	protected void setNext(int next) {
	}

	@Override
	public int getPrevious() {
		return -1;
	}
	@Override
	protected void setPrevious(int previous) {
	}

	@Override
	public void build() {
		app = (SceneApp)game;
		
		renderer = new Renderer();
		
		// Jason Zhao:
		this.celestialEventHandler = new CelestialEventHandler(app.scene);
		
		//ParticleEjectionEvent ejection_1 = new ParticleEjectionEvent(0, 199);
		//this.celestialEventHandler.addEvent(ejection_1);
		//ejection_1.startEvent();
		
		//ParticleEjectionEvent ejection_2 = new ParticleEjectionEvent(200, 399);
		//this.celestialEventHandler.addEvent(ejection_2);
		//ejection_2.startEvent();
		
		//ParticleEjectionEvent ejection_3 = new ParticleEjectionEvent(400, 499);
		//this.celestialEventHandler.addEvent(ejection_3);
		//ejection_3.startEvent();
		
		moonlet_1 = new ParticleMoonletEvent(500, 2499, 2);
		this.celestialEventHandler.addEvent(moonlet_1);
		moonlet_1.startEvent();
		
		for (int i = 1; i <= iNumComets; i++){
			CometEvent comet = new CometEvent(i,iCometLength);
			this.celestialEventHandler.addEvent(comet);
			comet.startEvent();
		}
		
		impact_1 = new DeepImpactEvent(2, 0, 199, 2);
		this.celestialEventHandler.addEvent(impact_1);
		impact_1.startEvent();
		
		impact_2 = new DeepImpactEvent(3, 200, 399, 2);
		this.celestialEventHandler.addEvent(impact_2);
		impact_2.startEvent();
		
		explosion_1 = new ExplosionEvent(Scene.iNumEjection, Scene.iNumEjection + Scene.iNumMoonlet);
		this.celestialEventHandler.addEvent(explosion_1);
		
		//EnduranceVoyageEvent endurance = new EnduranceVoyageEvent();
		//this.celestialEventHandler.addEvent(endurance);
		//endurance.startEvent();

	}
	@Override
	public void destroy(GameTime gameTime) {
	}

	private boolean starTexture = true;
	AnimationObject ao;
	SceneObject so;
	
	
	
	/**
	 * Add Scene Data Hotkeys
	 */
	private final ACEventFunc<KeyboardKeyEventArgs> onKeyPress = new ACEventFunc<KeyboardKeyEventArgs>() {
		@Override
		public void receive(Object sender, KeyboardKeyEventArgs args) {
			switch (args.key) {
			///////////////////
			case Keyboard.KEY_T:
				/*
				try {
					ao = new AnimationObject(app.scene);
					so = app.scene.objects.get("Star");
				} catch (Exception e){}
				if (starTexture)ao.changeTexture("Star", "StarMaterial");
				else ao.changeTexture("Star", "NoiseMaterial");
				app.scene.sendEvent(new SceneObjectResourceEvent(so, SceneObjectResourceEvent.Type.Material));
				starTexture = !starTexture;
				*/
				// Toggle Red_laser:
				if (bLaserOn){
					app.scene.objects.get("Endurance_laser").addScale(new Vector3(1,1,0.0001f));
				} else {
					app.scene.objects.get("Endurance_laser").addScale(new Vector3(1,1,10000f));
				}
				bLaserOn = !bLaserOn;
				
				break;
			///////////////////
			case Keyboard.KEY_M:
				if(!args.getAlt()) return;
				if(dataMaterial != null) {
					app.otherWindow.tabToForefront("Material");
					dataMaterial.addBasic();
				}
				break;
			case Keyboard.KEY_F3:
				FileDialog fd = new FileDialog(app.otherWindow);
				fd.setVisible(true);
				for(File f : fd.getFiles()) {
					String file = f.getAbsolutePath();
					if(file != null) {
						Parser p = new Parser();
						Object o = p.parse(file, Scene.class);
						if(o != null) {
							Scene old = app.scene;
							app.scene = (Scene)o;
							if(old != null) old.sendEvent(new SceneReloadEvent(file));
							return;
						}
					}
				}
				break;
			case Keyboard.KEY_F4:
				try {
					app.scene.saveData("data/scenes/Saved.xml");
				} catch (ParserConfigurationException | TransformerException e) {
					e.printStackTrace();
				}
				break;
			case Keyboard.KEY_F5:
				bBegin = !bBegin;
				Scene.bBegin = bBegin;
				break;
			case Keyboard.KEY_F7:
				startExplosion = true;
				Scene.startExplosion = true;
				shake = !shake;
				break;
			case Keyboard.KEY_ADD: case Keyboard.KEY_RBRACKET:
				ParticleMoonletEvent.GConstant *= 1.02;
				break;
			case Keyboard.KEY_MINUS: case Keyboard.KEY_LBRACKET:
				ParticleMoonletEvent.GConstant /= 1.02;
				break;
				
			case Keyboard.KEY_C:
				// camera switch:
				if (camIndex == 1){
					Scene.bExterior_cam_active = true;
					
					//endurance_exterior_cam.set();
					primary_cam.set(camController.camera.sceneObject.transformation);
					//camController.camera.sceneObject.transformation.set(app.scene.objects.get("Endurance_exterior_cam").transformation);
					
					if (bSwitched){
						//camController.camera.sceneObject.transformation.set(camController.rEnv.findObject(app.scene.objects.get("Endurance_parent_object")).mWorldTransform.clone().mulAfter(endurance_exterior_cam));
						camController.camera.sceneObject.transformation.set(CameraController.endur_exterior_cam_trans);
					} else {
						
						camController.camera.sceneObject.transformation.set(CameraController.endur_exterior_cam_trans.mulBefore(camController.rEnv.findObject(app.scene.objects.get("Endurance_parent_object")).mWorldTransform));
						//camController.camera.sceneObject.transformation.set(camController.rEnv.findObject(app.scene.objects.get("Endurance_exterior_cam")).mWorldTransform);
					}
	
					bSwitched = true;
					camIndex = 2;
				} else if (camIndex == 2){
					//endurance_exterior_cam.set(camController.camera.sceneCamera.transformation);
					Scene.bExterior_cam_active = false;
					Scene.bFollow_cam_active = true;
					//endurance_exterior_cam.set(camController.camera.sceneObject.transformation.mulBefore(camController.rEnv.findObject(app.scene.objects.get("Endurance_parent_object")).mWorldTransform.clone().invert()));
					
					
					
					
					camIndex = 3;
				} else if (camIndex == 3){
					Scene.bFollow_cam_active = false;
					
					camController.camera.sceneObject.transformation.set(primary_cam);
					camIndex = 1;
				}
				break;
			case Keyboard.KEY_L:
				// Toggle between flight control mode and camera mode;
				if (Scene.bExterior_cam_active)Scene.bFlightControl = !Scene.bFlightControl;
				
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public void onEntry(GameTime gameTime) {
		cameraIndex = 0;
		
		rController = new RenderController(app.scene, new Vector2(app.getWidth(), app.getHeight()));
		renderer.buildPasses(rController.env.root);
		camController = new CameraController(app.scene, rController.env, null);
		createCamController();
		manipController = new ManipController(rController.env, app.scene, app.otherWindow);
		gridRenderer = new GridRenderer();
		
		KeyboardEventDispatcher.OnKeyPressed.add(onKeyPress);
		manipController.hook();
		
		Object tab = app.otherWindow.tabs.get("Object");
		if(tab != null) sceneTree = (ScenePanel)tab;
		tab = app.otherWindow.tabs.get("Material");
		if(tab != null) dataMaterial = (RPMaterialData)tab;
		tab = app.otherWindow.tabs.get("Mesh");
		if(tab != null) dataMesh = (RPMeshData)tab;
		tab = app.otherWindow.tabs.get("Texture");
		if(tab != null) dataTexture = (RPTextureData)tab;
		
		wasPickPressedLast = false;
		prevCamScroll = 0;
	}
	@Override
	public void onExit(GameTime gameTime) {
		KeyboardEventDispatcher.OnKeyPressed.remove(onKeyPress);
		rController.dispose();
		manipController.dispose();
	}

	private void createCamController() {
		if(rController.env.cameras.size() > 0) {
			RenderCamera cam = rController.env.cameras.get(cameraIndex);
			camController.camera = cam;
		}
		else {
			camController.camera = null;
		}
	}
	
	@Override
	public void update(GameTime gameTime) {
		pick = false;
		int curCamScroll = 0;
		
		// Jason Zhao: the event handler that updates all the Celestial Events:
		if (bBegin){
			this.celestialEventHandler.update(app, gameTime);
			app.scene.objects.get("Endurance_object").transformation.mulBefore(Matrix4.createRotationZ((float)(gameTime.elapsed)));
			app.scene.objects.get("Star").transformation.mulBefore(Matrix4.createRotationY((float)(-gameTime.elapsed)/2));
			// Endurance red light blinking:
			//System.out.println(gameTime.total % 10);
			
			if (rl == null){
				for (RenderLight light : rController.env.lights){
					//redlight.sceneObject.getID().name
					//System.out.println(redlight.sceneObject.getID().name);
					//System.out.println(redlight.sceneObject.getID().name.compareTo("Endurance_redlight"));
					if (light.sceneObject.getID().name.compareTo("Endurance_redlight") == 0){
						rl = light;
						continue;
					}
					
					if (light.sceneObject.getID().name.compareTo("Endurance_engine1") == 0){
						eng1 = light;
						continue;
					}
					
					if (light.sceneObject.getID().name.compareTo("Endurance_engine2") == 0){
						eng2 = light;
						continue;
					}
					
					if (light.sceneObject.getID().name.compareTo("Endurance_engine3") == 0){
						eng3 = light;
						continue;
					}
					
					if (light.sceneObject.getID().name.compareTo("Endurance_engine4") == 0){
						eng4 = light;
						continue;
					}

				}
			}
			
			if(gameTime.total % 2.2 < 1.5){
				rl.sceneLight.mulIntensity((float)(Math.exp(-0.5*(gameTime.total%1))));;
				//System.out.println("Attenuating! " + rl.sceneLight.defaultIntensity);
				
			} else {
				rl.sceneLight.resetIntensity();
				//System.out.println(" ---------- Reseting! " + rl.sceneLight.defaultIntensity);
			}
			
			float zThrottle = CameraController.endur_throttle.z;
			eng1.sceneLight.setIntensity(engIntensity.clone().mul(zThrottle*zThrottle/10000));
			eng2.sceneLight.setIntensity(engIntensity.clone().mul(zThrottle*zThrottle/10000));
			eng3.sceneLight.setIntensity(engIntensity.clone().mul(zThrottle*zThrottle/10000));
			eng4.sceneLight.setIntensity(engIntensity.clone().mul(zThrottle*zThrottle/10000));
			
			app.scene.objects.get("Endurance_engine1_parent").transformation.m[10] = zThrottle/-15+0.01f;
			app.scene.objects.get("Endurance_engine2_parent").transformation.m[10] = zThrottle/-15+0.01f;
			app.scene.objects.get("Endurance_engine3_parent").transformation.m[10] = zThrottle/-15+0.01f;
			app.scene.objects.get("Endurance_engine4_parent").transformation.m[10] = zThrottle/-15+0.01f;
			
			//((Matrix4.createScale(1, 1, 50)));
			
		} // end begin
		
		
		// start explosion sequence
		if (startExplosion) {
			startExplosion(gameTime);
		}
		//app.scene.objects.get("meteror_"+iObjIndex).;

		if(Keyboard.isKeyDown(Keyboard.KEY_EQUALS)) curCamScroll++;
		if(Keyboard.isKeyDown(Keyboard.KEY_MINUS)) curCamScroll--;
		if(rController.env.cameras.size() != 0 && curCamScroll != 0 && prevCamScroll != curCamScroll) {
			if(curCamScroll < 0) curCamScroll = rController.env.cameras.size() - 1;
			cameraIndex += curCamScroll;
			cameraIndex %= rController.env.cameras.size();
			createCamController();
		}
		prevCamScroll = curCamScroll;
		
		if(camController.camera != null) {
			// This part is called every time an update event occurs. Jason's bookmark.
			camController.update(gameTime.elapsed);
			manipController.checkMouse(Mouse.getX(), Mouse.getY(), camController.camera);
		}
		
		
		
		if(Mouse.isButtonDown(1) || Mouse.isButtonDown(0) && (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))) {
			if(!wasPickPressedLast) pick = true;
			wasPickPressedLast = true;
		}
		else wasPickPressedLast = false;
		
		// View A Different Scene
		if(rController.isNewSceneRequested()) {
			setState(ScreenState.ChangeNext);
		}
	}
	
	int count = 0;
	//explosion sequence
	private void startExplosion(GameTime gameTime) {
		Vector3d oldI = new Vector3d();
		double coeff = gameTime.elapsed * 50;
		if (ParticleMoonletEvent.fStarRadius <= 6 && changeRadius) {
			ParticleMoonletEvent.GConstant *= (1 + ParticleMoonletEvent.fStarRadius/2500);
			app.scene.objects.get("Star").transformation.mulAfter(Matrix4.createScale(1.001f));
			ParticleMoonletEvent.fStarRadius *= 1.001;
			for (RenderLight light : rController.env.lights) {
				if (light.sceneObject.getID().name.compareTo("Light_1") == 0) {
					oldI.set(light.sceneLight.intensity);
					if (oldI.z < 800) {
						Vector3d newI = new Vector3d (oldI.x - coeff, oldI.y, oldI.z + (coeff));
						light.sceneLight.setIntensity(newI);
					}	
				}
			}
		} 		
		else {
			changeRadius = false;
			if (explode) {
				startTime = gameTime.total;
				explode = false;
			}
			double time = gameTime.total - startTime;
			if (ParticleMoonletEvent.eaten >= Scene.iNumMoonlet) {
				if (count > 22) explode(gameTime);
				else {
					if (time % 3 < 1) {
						count ++;
						SceneObject laser = app.scene.objects.get("laser_" + count);
						laser.addScale(new Vector3(1000,1,1));
						laser.addRotation(new Vector3((float)(Math.random() * 100 * Math.PI), (float)(Math.random() * 100 * Math.PI), (float)(Math.random() * 100 * Math.PI)));
					}
				}
				
			}
			
		}

	}
	
//	private float t =0;
	private void explode(GameTime gameTime) {
		try {
			ao = new AnimationObject(app.scene);
		} catch(Exception e){}
		
		SceneObject star= app.scene.objects.get("Star");
		moonlet_1.stopEvent();
		impact_1.stopEvent(app);
		impact_2.stopEvent(app);
		
		ao.changeTexture("Star", "Mirror");
		if (ParticleMoonletEvent.fStarRadius >=2) {
			star.transformation.mulAfter(Matrix4.createScale(.99f));
			ParticleMoonletEvent.fStarRadius *= .99;
		}
		
		explosion_1.startEvent();
		
		if (shake) {
			for (RenderCamera c : rController.env.cameras) {
				shakeCamera(c);
			}
		}
	}
	
	float t = 1;
	private void shakeCamera(RenderCamera c) {		
		if (t > 0) {
			float cos1 = (float)(Math.random() - .5) * t;
			float cos2 = (float)(Math.random() - .5) * t;
			float cos3 = (float)(Math.random() - .5) * t;
			Matrix4 trans = Matrix4.createTranslation(new Vector3(cos1, cos2, cos3));
			c.sceneCamera.transformation.mulAfter(trans);
			t-=.0001;
		}
	}
	
	@Override
	public void draw(GameTime gameTime) {
		rController.update(renderer, camController, gameTime);

		if(pick && camController.camera != null) {
			manipController.checkPicking(renderer, camController.camera, Mouse.getX(), Mouse.getY());
		}
		
		Vector3 bg = app.scene.background;
		GL11.glClearColor(bg.x, bg.y, bg.z, 0);
		GL11.glClearDepth(1.0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		if(camController.camera != null){
			renderer.draw(camController.camera, rController.env.lights, gameTime);
			manipController.draw(camController.camera);
			if (showGrid)
				gridRenderer.draw(camController.camera);
		}
        GLError.get("draw");
	}
	
	public CelestialEventHandler getCelestialEventHandler() {
		return this.celestialEventHandler;
	}
}
