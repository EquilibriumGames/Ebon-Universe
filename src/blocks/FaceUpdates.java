package blocks;

public interface FaceUpdates {
	void update(final Chunk chunk, final Block parent, final int faceIndex, final float cx, final float cy, final float cz);
}
