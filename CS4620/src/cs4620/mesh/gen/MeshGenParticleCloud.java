package cs4620.mesh.gen;

import cs4620.common.BasicType;
import cs4620.mesh.MeshData;
import egl.NativeMem;

/**
 * Generates A particle cloud
 * @author Jason Zhao
 *
 */
public class MeshGenParticleCloud extends MeshGenerator {
	@Override
	public void generate(MeshData outData, MeshGenOptions opt) {
		// Calculate Vertex And Index Count
		int numParticle = 30;
		
		outData.vertexCount = numParticle * 3;
		outData.indexCount = numParticle * 3;

		// Create Storage Spaces
		outData.positions = NativeMem.createFloatBuffer(outData.vertexCount * 3);
		outData.uvs = NativeMem.createFloatBuffer(outData.vertexCount * 2);
		outData.normals = NativeMem.createFloatBuffer(outData.vertexCount * 3);
		outData.indices = NativeMem.createIntBuffer(outData.indexCount);
		
		// Add Positions
		for (int i = 0; i < numParticle; i++){
			outData.positions.put(0);
			outData.positions.put(0);
			outData.positions.put(0);
			outData.uvs.put(0);
			outData.uvs.put(0);
			
			outData.positions.put(1);
			outData.positions.put(0);
			outData.positions.put(0);
			outData.uvs.put(1);
			outData.uvs.put(0);
			
			outData.positions.put(0);
			outData.positions.put(1);
			outData.positions.put(0);
			outData.uvs.put(0);
			outData.uvs.put(1);
			
			outData.normals.put(i);
			outData.normals.put(i);
			outData.normals.put(i);
			
			outData.normals.put(i);
			outData.normals.put(i);
			outData.normals.put(i);
			
			outData.normals.put(i);
			outData.normals.put(i);
			outData.normals.put(i);
			
			outData.indices.put(i * 3);
			outData.indices.put(i * 3 + 1);
			outData.indices.put(i * 3 + 2);
		}


		
	}

	@Override
	public BasicType getType() {
		return BasicType.ParticleCloud;
	}
}
