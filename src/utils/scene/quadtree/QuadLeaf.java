package utils.scene.quadtree;

public class QuadLeaf<T> {
	public volatile float x;
	public volatile float y;
	public final T value;
	public QuadNode<T> node;

	public QuadLeaf(float x, float y, T value, QuadNode<T> node) {
		this.x = x;
		this.y = y;
		this.value = value;
		this.node = node;
	}
}