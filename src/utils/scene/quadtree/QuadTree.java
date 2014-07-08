package utils.scene.quadtree;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;

public class QuadTree<T> {

	protected QuadNode<T> root = null;
	private volatile int size = 0;
	private AbstractCollection<T> values = null;

	/**
	 * Creates an empty QuadTree with the bounds
	 */
	public QuadTree(float minX, float minY, float maxX, float maxY, int maxElementsPerNode) {
		this.root = new QuadNode<T>(minX, minY, maxX, maxY, maxElementsPerNode, this);
	}

	/**
	 * Associates the specified value with the specified coords in this
	 * QuadTree.
	 */
	public boolean put(float x, float y, T value) {
		if (this.root.put(x, y, value)) {
			increaseSize();
			return true;
		}
		return false;
	}

	public boolean remove(float x, float y, T value) {
		boolean found = false;
		if (this.root.remove(x, y, value)) {
			decreaseSize();
			return true;
		// fail safe: iterate through all leafs and find our value if x and y
		} /*else {
			QuadLeaf<T> leaf = firstLeaf();
			do {
				if(leaf.values.contains(value)) {
					leaf.values.remove(value);
					found = true;
					QuadNode<T> currentNode = leaf.node;
					if(currentNode.getLeaf() == leaf)
						currentNode.clearLeaf();
					else
						System.out.println("Wrong node reference in quad tree leaf");
					decreaseSize();
				}
				leaf = nextLeaf(leaf);
			} while(leaf != null && !found);
		}*/
		if(!found)
			return !contains(value);
		return found;
	}
	
	public boolean update(float x, float y, T value) {
		boolean success = false;
		success = root.update(x, y, value);
		if(!success)
			return !contains(value);
		return success;
	}
	
	public boolean contains(T value) {
		return root.contains(value);
	}

	public void clear() {
		this.root.clear();
		this.size = 0;
	}
	
	private void increaseSize() { 
		this.size++; this.values = null;
	}
	
	private void decreaseSize() { 
		this.size--; this.values = null; 
	}

	/**
	 * Gets the object closest to (x,y)
	 */
	public T get(float x, float y) {
		return this.root.get(x, y, new AbstractFloat(Float.POSITIVE_INFINITY));
	}

	/**
	 * Gets all objects within a certain distance
	 */
	public ArrayList<T> get(float x, float y, float distance) {
		return this.root.get(x, y, distance, new ArrayList<T>());
	}

	/**
	 * Gets all objects inside the specified boundary.
	 */
	/*public ArrayList<T> get(Box bounds, ArrayList<T> values) {
		return this.root.get(bounds, values);
	}*/

	/**
	 * Gets all objects inside the specified area.
	 */
	/*public ArrayList<T> get(float minX, float minY, float maxX, float maxY, ArrayList<T> values) {
		return get(new Box(minX, minY, maxX, maxY), values);
	}

	public int execute(Box bounds, Executor<T> executor) {
		if (bounds == null) {
			return this.root.execute(this.root.getBounds(), executor);
		}
		return this.root.execute(bounds, executor);
	}

	public int execute(float minX, float minY, float maxX, float maxY, Executor<T> executor) {
		return execute(new Box(minX, minY, maxX, maxY), executor);
	}*/

	public int size() {
		return this.size;
	}

	public float getMinX() {
		return this.root.getBounds().minX;
	}

	public float getMaxX() {
		return this.root.getBounds().maxX;
	}

	public float getMinY() {
		return this.root.getBounds().minY;
	}

	public float getMaxY() {
		return this.root.getBounds().maxY;
	}

	/*public AbstractCollection<T> values() {
		if (this.values == null) {
			this.values = new AbstractCollection<T>() {
				@Override
				public Iterator<T> iterator() {
					Iterator<T> iterator = new Iterator<T>() {
						private QuadLeaf<T> currentLeaf = firstLeaf();
						private int nextIndex = 0;
						private T next = first();

						private T first() {
							if (this.currentLeaf == null) {
								return null;
							}
							this.nextIndex = 0;
							loadNext();
							return this.next;
						}

						@Override
						public boolean hasNext() {
							return this.next != null;
						}

						@Override
						public T next() {
							if (this.next == null) {
								return null;
							}
							T current = this.next;
							loadNext();
							return current;
						}

						private void loadNext() {
							boolean searching = true;
							while (searching) {
								if (this.nextIndex < this.currentLeaf.values.size()) {
									this.nextIndex++;
									this.next = this.currentLeaf.values.get(this.nextIndex - 1);
									searching = false;
								} else {
									this.currentLeaf = nextLeaf(this.currentLeaf);
									if (this.currentLeaf == null) {
										this.next = null;
										searching = false;
									} else {
										this.nextIndex = 0;
									}
								}
							}
						}
						@Override
						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
					return iterator;
				}
				@Override
				public int size() {
					return QuadTree.this.size;
				}
			};
		}
		return this.values;
	}*/

	private QuadLeaf<T> firstLeaf() {
		return this.root.firstLeaf();
	}

	private QuadLeaf<T> nextLeaf(QuadLeaf<T> currentLeaf) {
		return this.root.nextLeaf(currentLeaf);
	}

	interface Executor<T>{	
		public void execute(float x, float y, T object); 
	}
}