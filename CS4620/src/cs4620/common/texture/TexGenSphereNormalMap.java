package cs4620.common.texture;

import egl.math.Color;
import egl.math.Colord;
import egl.math.Vector2i;
import egl.math.Vector3d;

public class TexGenSphereNormalMap extends ACTextureGenerator {
	// 0.5f means that the discs are tangent to each other
	// For greater values discs intersect
	// For smaller values there is a "planar" area between the discs
	private float bumpRadius;
	// The number of rows and columns
	// There should be one disc in each row and column
	private int resolution;
	
	public TexGenSphereNormalMap() {
		this.bumpRadius = .5f;
		this.resolution = 10;
		this.setSize(new Vector2i(256));
	}
	
	public void setBumpRadius(float bumpRadius) {
		this.bumpRadius = bumpRadius;
	}
	
	public void setResolution(int resolution) {
		this.resolution = resolution;
	}
	
	@Override
	public void getColor(float u, float v, Color outColor) {

		float   u_r = u * resolution,
				v_r = v * resolution;
		
		float u_center = Math.round(u_r),
			  v_center = Math.round(v_r);
		 
		double  theta,
				phi;
		
		if (Math.pow((u_r - u_center),2) + Math.pow((v_r - v_center),2) < Math.pow(bumpRadius, 2)) {
			float u_circleCenter = u_center / resolution,
				  v_circleCenter = v_center / resolution;
			
			theta = u_circleCenter * Math.PI * 2.0;
			phi = (1 - v_circleCenter) * Math.PI;

		}
		else { 
			theta = u * Math.PI * 2.0;
			phi = (1 - v) * Math.PI;
		}
		
		double  x = -Math.sin(theta) * Math.sin(phi),
				y = Math.cos(phi),
				z = -Math.cos(theta) * Math.sin(phi);
		
		double  transX = (x + 1) / 2,
				transY = (y + 1) / 2,
				transZ = (z + 1) / 2;
		
		outColor.set(new Colord(transX,transY,transZ));
		
	}
}
