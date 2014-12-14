package cs4620.common;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import egl.math.Matrix4;
import egl.math.Vector3;
import cs4620.common.event.SceneCollectionModifiedEvent;
import cs4620.common.event.SceneDataType;
import cs4620.common.event.SceneEvent;
import cs4620.common.event.SceneEventQueue;
import cs4620.common.texture.TexGenCheckerBoard;
import cs4620.common.texture.TexGenSphereNormalMap;
import cs4620.common.texture.TexGenUVGrid;
import cs4620.mesh.gen.MeshGenCube;
import cs4620.mesh.gen.MeshGenCylinder;
import cs4620.mesh.gen.MeshGenParticle;
import cs4620.mesh.gen.MeshGenPlane;
import cs4620.mesh.gen.MeshGenSphere;
import cs4620.mesh.gen.MeshGenParticleCloud;
import cs4620.scene.ViewScreen;
import cs4621.celestialEvent.ParticleMoonletEvent;



public class Scene {
	/**
	 * The Name Of The Root Node
	 */
	public static final String ROOT_NODE_NAME = "World";
	public static final HashSet<String> DEFAULT_MESHES = new HashSet<>(Arrays.asList("Sphere", "Cube", "Cylinder"));
	public static final HashSet<String> DEFAULT_MATERIALS = new HashSet<>(Arrays.asList("Generic"));
	public static final HashSet<String> DEFAULT_TEXTURES = new HashSet<>(Arrays.asList("Checker Board", "UV"));

	// Jason Zhao:
	public static boolean bBegin = false;
	public static boolean bExterior_cam_active = false;
	public static boolean bFlightControl = false;
	
	/**
	 * Container Of Unique Meshes
	 */
	public final UniqueContainer<Mesh> meshes = new UniqueContainer<Mesh>(new UniqueContainer.IAllocator<Mesh>() {
		@Override
		public Mesh generate() {
			return new Mesh();
		}
	}, "__Mesh__");
	/**
	 * Container Of Unique Textures
	 */
	public final UniqueContainer<Texture> textures = new UniqueContainer<Texture>(new UniqueContainer.IAllocator<Texture>() {
		@Override
		public Texture generate() {
			return new Texture();
		}
	}, "__Texture__");
	/**
	 * Container Of Unique Cube Map	
	 */
	public final UniqueContainer<Cubemap> cubemaps = new UniqueContainer<Cubemap>(new UniqueContainer.IAllocator<Cubemap>() {
		@Override
		public Cubemap generate() {
			return new Cubemap();
		}
	}, "__Cubemap__");
	/**
	 * Container Of Unique Materials
	 */
	public final UniqueContainer<Material> materials = new UniqueContainer<Material>(new UniqueContainer.IAllocator<Material>() {
		@Override
		public Material generate() {
			return new Material();
		}
	}, "__Material__");
	/**
	 * Container Of Unique Objects
	 */
	public final UniqueContainer<SceneObject> objects = new UniqueContainer<SceneObject>(new UniqueContainer.IAllocator<SceneObject>() {
		@Override
		public SceneObject generate() {
			return new SceneObject();
		}
	}, "__Object__");

	public Vector3 background = new Vector3();
	
	/**
	 * A list of queues that listen to updates in the scene
	 */
	private final ArrayList<SceneEventQueue> changeListeners = new ArrayList<>();

	public Scene() {
		// Add Primitive Shapes
		Mesh m = new Mesh();
		m.setGenerator(new MeshGenSphere());
		addMesh(new NameBindMesh("Sphere", m));
		m = new Mesh();
		m.setGenerator(new MeshGenCylinder());
		addMesh(new NameBindMesh("Cylinder", m));
		m = new Mesh();
		m.setGenerator(new MeshGenCube());
		addMesh(new NameBindMesh("Cube", m));
		m = new Mesh();
		m.setGenerator(new MeshGenPlane());
		addMesh(new NameBindMesh("Plane", m));
		m = new Mesh();
		m.setGenerator(new MeshGenParticleCloud());
		addMesh(new NameBindMesh("ParticleCloud", m));

		// Add Simple Generated Textures
		Texture t = new Texture();
		t.setGenerator(new TexGenCheckerBoard());
		addTexture(new NameBindTexture("Checker Board", t));
		t = new Texture();
		t.setGenerator(new TexGenUVGrid());
		addTexture(new NameBindTexture("UV", t));
		t = new Texture();
		t.setGenerator(new TexGenSphereNormalMap());
		addTexture(new NameBindTexture("NormalMapped", t));
		
		// Add Generic Material
		Material mat = new Material();
		mat.setType(Material.T_AMBIENT);
		addMaterial(new NameBindMaterial("Generic", mat));
		mat = new Material();
		mat.setType(Material.T_LAMBERTIAN);
		addMaterial(new NameBindMaterial("Lambertian", mat));

		// Add The Root Node
		SceneObject so = new SceneObject();
		addObject(new NameBindSceneObject(ROOT_NODE_NAME, so));
		
		// Programmatically add objects (particle cloud approximation);
		
		m = new Mesh();
		m.setGenerator(new MeshGenParticle());
		addMesh(new NameBindMesh("particle", m));
		
		// Jason Zhao: maybe can move this to ViewScreen.build();
		int iNumEjection = 500, iNumMoonlet = 2000;
		
		// Particle Ejection Event
		for (int i = 0; i < iNumEjection; i++){
			SceneObject particle = new SceneObject();
			particle.setMesh("particle");
			particle.setMaterial("Generic");
//			addObject(new NameBindSceneObject("particle_"+i, particle) );
		}
		
		// ParticleMoonlet inner ring Event
		for (int i = iNumEjection; i < iNumEjection + iNumMoonlet/2; i++){
			SceneObject particle = new SceneObject();
			particle.setMesh("particle");
			particle.setMaterial("ParticleMaterial");
			
			//particle.v3_speed.set((float)(Math.random()-0.5)*2, (float)(Math.random()-0.5)/5, (float)(Math.random()-0.5)*2);
			
			Matrix4 M;
			Random rand = new Random();
			double dDistroFunc = 7.2+rand.nextGaussian()*1.2;
			if (dDistroFunc < 4){dDistroFunc = 4;}
			if (rand.nextBoolean()){
				M = Matrix4.createScale((float)(Math.random()*4+1))
						.mulAfter(Matrix4.createTranslation(new Vector3((float)dDistroFunc,(float)(rand.nextGaussian()/2),0))
						.mulAfter(Matrix4.createRotationY((float)(rand.nextGaussian()/8*Math.PI))));
			} else {
				M = Matrix4.createScale((float)(Math.random()*4+1))
						.mulAfter(Matrix4.createTranslation(new Vector3((float)dDistroFunc,(float)(rand.nextGaussian()/2),0))
						.mulAfter(Matrix4.createRotationY((float)((rand.nextGaussian()/8+1)*Math.PI))));
			}
			
			
			
			
			M.mulBefore(Matrix4.createRotationX((float)(Math.random()*Math.PI)));
			M.mulBefore(Matrix4.createRotationY((float)(Math.random()*Math.PI)));
			M.mulBefore(Matrix4.createRotationZ((float)(Math.random()*Math.PI)));
			
			particle.v3_speed.set(M.getTrans());
			particle.v3_speed.cross(new Vector3(0,1,0));
			float fDist = particle.v3_speed.len();
			particle.v3_speed.div(fDist);
			//particle.v3_speed.mul((float)rand.nextGaussian()/5+0.8f);
			
			particle.v3_speed.mul((float)Math.sqrt(ParticleMoonletEvent.GConstant/fDist));
			//particle.v3_speed.y = (float)(Math.random()/5*-Math.signum(M.getTrans().y));
			//particle.v3_speed.set(0);
			addObjectWithTransform(new NameBindSceneObject("particle_"+i, particle), M);
		}
		
		// ParticleMoonlet Outer ring event:
		for (int i = iNumEjection + iNumMoonlet/2; i < iNumEjection + iNumMoonlet; i++){
			SceneObject particle = new SceneObject();
			particle.setMesh("particle");
			particle.setMaterial("ParticleMaterial");
			
			//particle.v3_speed.set((float)(Math.random()-0.5)*2, (float)(Math.random()-0.5)/5, (float)(Math.random()-0.5)*2);
			
			Matrix4 M;
			Random rand = new Random();
			
			
			
			double dDistroFunc = 15+rand.nextGaussian()*1.5;
			if (dDistroFunc < 10){dDistroFunc = 10;}
			M = Matrix4.createScale((float)(Math.random()*4+1))
					.mulAfter(Matrix4.createTranslation(new Vector3((float)dDistroFunc,(float)(rand.nextGaussian()/2),0))
					.mulAfter(Matrix4.createRotationY((float)(Math.random()*2*Math.PI))));
			M.mulBefore(Matrix4.createRotationX((float)(Math.random()*Math.PI)));
			M.mulBefore(Matrix4.createRotationY((float)(Math.random()*Math.PI)));
			M.mulBefore(Matrix4.createRotationZ((float)(Math.random()*Math.PI)));
			
			particle.v3_speed.set(M.getTrans());
			particle.v3_speed.cross(new Vector3(0,1,0));
			float fDist = particle.v3_speed.len();
			particle.v3_speed.div(fDist);
			//particle.v3_speed.mul((float)rand.nextGaussian()/5+0.8f);
			
			particle.v3_speed.mul((float)Math.sqrt(ParticleMoonletEvent.GConstant/fDist));
			//particle.v3_speed.y = (float)(Math.random()/5*-Math.signum(M.getTrans().y));
			//particle.v3_speed.set(0);
			
			addObjectWithTransform(new NameBindSceneObject("particle_"+i, particle), M);
		}
		
		
		
		
		
		// Comet Event
		for (int i = 1; i <= ViewScreen.iNumComets; i++){
			SceneObject comet = new SceneObject();
			comet.setMesh("Sphere");
			comet.setMaterial("CometMaterial");
			addObjectWithTransform(new NameBindSceneObject("comet_"+i, comet), Matrix4.createTranslation(new Vector3(999, 999, 999)));
		}
		
		
		
		// Deep Impact Event:
		int iNumDebris = 400;
		SceneObject deepimpact = new SceneObject();
		deepimpact.setMesh("Sphere");
		deepimpact.setMaterial("Generic");
		addObjectWithTransform(new NameBindSceneObject("comet_2", deepimpact), Matrix4.createTranslation(new Vector3(999, 999, 999)));
		deepimpact = new SceneObject();
		deepimpact.setMesh("Sphere");
		deepimpact.setMaterial("Generic");
		addObjectWithTransform(new NameBindSceneObject("comet_3", deepimpact), Matrix4.createTranslation(new Vector3(999, 999, 999)));
		// Ejection for deep impact:
		for (int i = 0; i < iNumDebris; i++){
			SceneObject debris = new SceneObject();
			debris.setMesh("particle");
			debris.setMaterial("MoonMaterial");
			addObject(new NameBindSceneObject("debris_"+i, debris) );
		}
		

		
	}

	public void addListener(SceneEventQueue q){
		changeListeners.add(q);
	}
	public void sendEvent(SceneEvent e) {
		for(SceneEventQueue q : changeListeners) q.addEvent(e);		
	}

	public static class NameBindMesh { 
		String name = null;
		Mesh data = null;

		public NameBindMesh() {
		}
		public NameBindMesh(String s, Mesh d) {
			setName(s);
			setData(d);
		}

		public void setName(String s) {
			name = s;
		}
		public void setData(Mesh d) {
			data = d;
		}
	}
	public void addMesh(NameBindMesh o) {
		meshes.add(o.data);
		meshes.setName(o.data, o.name);

		sendEvent(new SceneCollectionModifiedEvent(SceneDataType.Mesh, o.name, true));
	}
	public void removeMesh(String name) {
		meshes.remove(name);

		sendEvent(new SceneCollectionModifiedEvent(SceneDataType.Mesh, name, false));
	}

	public static class NameBindTexture { 
		String name = null;
		Texture data = null;

		public NameBindTexture() {
		}
		public NameBindTexture(String s, Texture d) {
			setName(s);
			setData(d);
		}

		public void setName(String s) {
			name = s;
		}
		public void setData(Texture d) {
			data = d;
		}
	}
	
	public static class NameBindCubemap {
		String name = null;
		Cubemap data = null;
		public NameBindCubemap() {
		}
		
		public NameBindCubemap(String s, Cubemap d) {
			setName(s);
			setData(d);
		}

		public void setName(String s) {
			name = s;
		}
		public void setData(Cubemap d) {
			data = d;
		}
	}
	
	public void addTexture(NameBindTexture o) {
		textures.add(o.data);
		textures.setName(o.data, o.name);

		sendEvent(new SceneCollectionModifiedEvent(SceneDataType.Texture, o.name, true));
	}
	
	public void addCubemap(NameBindCubemap o) {
		cubemaps.add(o.data);
		cubemaps.setName(o.data, o.name);
		
		sendEvent(new SceneCollectionModifiedEvent(SceneDataType.Cubemap, o.name, true));
	}
	
	public void removeTexture(String name) {
		textures.remove(name);

		sendEvent(new SceneCollectionModifiedEvent(SceneDataType.Texture, name, false));
	}

	public void removeCubemap(String name) {
		cubemaps.remove(name);

		sendEvent(new SceneCollectionModifiedEvent(SceneDataType.Cubemap, name, false));
	}
	
	
	public static class NameBindMaterial { 
		String name = null;
		Material data = null;

		public NameBindMaterial() {
		}
		public NameBindMaterial(String s, Material d) {
			setName(s);
			setData(d);
		}

		public void setName(String s) {
			name = s;
		}
		public void setData(Material d) {
			data = d;
		}
	}
	public void addMaterial(NameBindMaterial o) {
		materials.add(o.data);
		materials.setName(o.data, o.name);

		sendEvent(new SceneCollectionModifiedEvent(SceneDataType.Material, o.name, true));
	}
	public void removeMaterial(String name) {
		materials.remove(name);

		sendEvent(new SceneCollectionModifiedEvent(SceneDataType.Material, name, false));
	}

	public static class NameBindSceneObject { 
		String name = null;
		SceneObject data = null;

		public NameBindSceneObject() {
		}
		public NameBindSceneObject(String s, SceneObject d) {
			setName(s);
			setData(d);
		}

		public void setName(String s) {
			name = s;
		}
		public void setData(SceneObject d) {
			data = d;
		}
	}
	public void addObject(NameBindSceneObject o) {
		objects.add(o.data);
		objects.setName(o.data, o.name);
		if(o.data.parent == null && !o.name.equals(ROOT_NODE_NAME)) o.data.parent = ROOT_NODE_NAME;
		sendEvent(new SceneCollectionModifiedEvent(SceneDataType.Object, o.name, true));
	}
	
	// Jason Zhao: add object with a given transformation:
	public void addObjectWithTransform(NameBindSceneObject o, Matrix4 transform) {
		o.data.transformation.set(transform);
		objects.add(o.data);
		objects.setName(o.data, o.name);
		if(o.data.parent == null && !o.name.equals(ROOT_NODE_NAME)) o.data.parent = ROOT_NODE_NAME;
		sendEvent(new SceneCollectionModifiedEvent(SceneDataType.Object, o.name, true));
	}
	
	
	public void removeObject(String name) {
		// Can't Delete The Root
		if(name.equals(ROOT_NODE_NAME)) return;
		objects.remove(name);
		for(SceneObject o : objects) {
			if(o.parent != null && o.parent.equals(name)) o.parent = ROOT_NODE_NAME;
		}
		
		sendEvent(new SceneCollectionModifiedEvent(SceneDataType.Object, name, false));
	}	
	
	public void setBackground(Vector3 background) {
		this.background.set(background);
	}

	public void saveData(String file) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// Scene Root
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("scene");
		doc.appendChild(rootElement);

		// Textures
		for(Texture o : textures) {
			if(DEFAULT_TEXTURES.contains(o.getID().name)) continue;

			Element e = doc.createElement("texture");
			Element tName = doc.createElement("name");
			tName.appendChild(doc.createTextNode(o.getID().name));
			e.appendChild(tName);

			Element eData = doc.createElement("data");
			o.saveData(doc, eData);
			e.appendChild(eData);
			
			rootElement.appendChild(e);
		}
		
		// Cube maps
		for(Cubemap o : cubemaps) {
			if(DEFAULT_TEXTURES.contains(o.getID().name)) continue;

			Element e = doc.createElement("cubemap");
			Element tName = doc.createElement("name");
			tName.appendChild(doc.createTextNode(o.getID().name));
			e.appendChild(tName);

			Element eData = doc.createElement("data");
			o.saveData(doc, eData);
			e.appendChild(eData);
			
			rootElement.appendChild(e);
		}
		
		// Meshes
		for(Mesh o : meshes) {
			if(DEFAULT_MESHES.contains(o.getID().name)) continue;

			Element e = doc.createElement("mesh");
			Element tName = doc.createElement("name");
			tName.appendChild(doc.createTextNode(o.getID().name));
			e.appendChild(tName);

			Element eData = doc.createElement("data");
			o.saveData(doc, eData);
			e.appendChild(eData);
			
			rootElement.appendChild(e);
		}
		
		// Materials
		for(Material o : materials) {
			if(DEFAULT_MATERIALS.contains(o.getID().name)) continue;

			Element e = doc.createElement("material");
			Element tName = doc.createElement("name");
			tName.appendChild(doc.createTextNode(o.getID().name));
			e.appendChild(tName);

			Element eData = doc.createElement("data");
			o.saveData(doc, eData);
			e.appendChild(eData);
			
			rootElement.appendChild(e);
		}
		
		// Objects
		for(SceneObject o : objects) {
			if(o.getID().name.equals(ROOT_NODE_NAME)) continue;

			Element e = doc.createElement("object");
			Element tName = doc.createElement("name");
			tName.appendChild(doc.createTextNode(o.getID().name));
			e.appendChild(tName);

			Element eData = doc.createElement("data");
			o.saveData(doc, eData);
			e.appendChild(eData);
			
			rootElement.appendChild(e);
		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(file));
		transformer.transform(source, result);
	}
}