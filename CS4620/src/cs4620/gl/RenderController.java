package cs4620.gl;

import java.sql.Time;
import java.util.ArrayList;

import blister.GameTime;
import cs4620.common.Material;
import cs4620.common.Mesh;
import cs4620.common.Scene;
import cs4620.common.SceneCamera;
import cs4620.common.SceneObject;
import cs4620.common.Texture;
import cs4620.common.event.SceneCollectionModifiedEvent;
import cs4620.common.event.SceneEvent;
import cs4620.common.event.SceneEventQueue;
import cs4620.common.event.SceneObjectResourceEvent;
import cs4620.common.event.SceneReloadEvent;
import cs4620.common.event.SceneTransformationEvent;
import egl.IDisposable;
import egl.math.Matrix4;
import egl.math.Vector2;
import egl.math.Vector3;

public class RenderController implements IDisposable {
	public final SceneEventQueue queue;
	public final Scene scene;
	public RenderEnvironment env;
	private boolean requestNewScene = false;
	private double t = 0,
				timeDelta = 0; // use created counter rather than system time
							// to be able to sync with every call to update
	
	public RenderController(Scene s, Vector2 viewSize) {
		scene = s;
		queue = new SceneEventQueue(scene);
		scene.addListener(queue);
		env = RenderTreeBuilder.build(scene, viewSize);
	}
	@Override
	public void dispose() {
		env.dispose();
	}

	public boolean isNewSceneRequested() {
		return requestNewScene;
	}
	
	// called ~60 times a sec
	public void update(Renderer r, CameraController camController, GameTime gametime) {
		
		ArrayList<SceneEvent> le = new ArrayList<>();
		queue.getEvents(le);
		if(le.size() == 0) return;
		
		boolean isTreeModified = false;
		boolean areTransformsModified = false;
		boolean areResourcesModified = false;
		
		for(SceneEvent e : le) {
			if(e instanceof SceneCollectionModifiedEvent) {
				SceneCollectionModifiedEvent cme = (SceneCollectionModifiedEvent)e;
				switch (cme.dataType) {
				case Texture:
					areResourcesModified = true;
					if(cme.isAdded) {
						Texture t = scene.textures.get(cme.name);
						if(t != null) env.addTexture(t);
					}
					else {
						env.removeTexture(cme.name);
					}
					break;
				case Mesh:
					areResourcesModified = true;
					if(cme.isAdded) {
						Mesh m = scene.meshes.get(cme.name);
						if(m != null) env.addMesh(m);
					}
					else {
						env.removeMesh(cme.name);
					}
					break;
				case Material:
					areResourcesModified = true;
					if(cme.isAdded) {
						Material m = scene.materials.get(cme.name);
						if(m != null) env.addMaterial(m);
					}
					else {
						env.removeMaterial(cme.name);
					}
					break;
				case Object:
					isTreeModified = !cme.isAdded || (scene.objects.get(cme.name) != null);
					break;
				default:
					break;
				}
			}
			else if(e instanceof SceneTransformationEvent) {
				areTransformsModified = true;
			}
			else if(e instanceof SceneObjectResourceEvent) {
				areResourcesModified = true;
			}
			else if(e instanceof SceneReloadEvent) {
				requestNewScene = true;
			}
		}
		
		if(isTreeModified) {
			RenderTreeBuilder.buildTree(scene, env);
			if(camController.camera != null) {
				SceneObject so = camController.camera.sceneObject;
				for(RenderCamera cam : env.cameras ) {
					if(cam.sceneObject == so) {
						camController.camera = cam;
						break;
					}
				}				
			}
			else if(env.cameras.size() > 0) {
				camController.camera = env.cameras.get(0);
			}
		}
		else if(areTransformsModified) {
			RenderTreeBuilder.rippleTransformations(env);
		}
		
		if(areResourcesModified || isTreeModified) {
			env.linkObjectResources();
			r.buildPasses(env.root);
		}
		
		////////////////////////////////
		
		//System.out.println(seconds);
		/*
		try {
			AnimationObject ao = new AnimationObject(scene);
			ao.reset("Star");
			ao.rotateObject("Star", 0.f, (float)t / 4, 0.f);
			if (gametime.elapsed > 10) {
				ao.wobbleRadius("Star", t/30 * Math.min((gametime.elapsed - 4) / 7, 20));
			}
			else {
				ao.wobbleRadius("Star", t / 30);
			}
			t += (Math.random()/2 + .5) * 1;
			
		} catch(Exception e) {}
		*/
	}
}