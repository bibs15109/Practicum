package cs4620.ray1.shader;

import cs4620.ray1.IntersectionRecord;
import cs4620.ray1.Ray;
import cs4620.ray1.Scene;
import egl.math.Colord;

public class RGBNormals extends Shader {

	public RGBNormals() { }

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "RGB surface normal encoding shader";
	}

	/**
	 * XYZ component to RGB encoding of the normal vector at the intersection point.
	 * 
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param record The intersection record of where the ray intersected the surface.
	 */
	public void shade(Colord outIntensity, Scene scene, Ray ray, IntersectionRecord record) {
		outIntensity.x = (record.normal.x + 1) / 2;
		outIntensity.y = (record.normal.y + 1) / 2;
		outIntensity.z = (record.normal.z + 1) / 2;
	}
}