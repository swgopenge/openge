package utils.scene.quadtree;

public class QuadLeaf<T> {
	public float x;
	public float y;
	public final T value;
	public QuadNode<T> node;

	public QuadLeaf(float x, float y, T value, QuadNode<T> node) {
		this.x = x;
		this.y = y;
		this.value = value;
		this.node = node;
	}
}