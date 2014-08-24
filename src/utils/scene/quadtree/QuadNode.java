package utils.scene.quadtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;


public class QuadNode<T> {

	private Set<QuadLeaf<T>> leafs;
	private AtomicBoolean hasChildren = new AtomicBoolean(false);
	private QuadNode<T> NW = null;
	private QuadNode<T> NE = null;
	private QuadNode<T> SE = null;
	private QuadNode<T> SW = null;
	private final Box bounds;
    private AtomicBoolean lock = new AtomicBoolean(false);
    private int maxElementsPerNode;
    private QuadTree<T> tree;

	public QuadNode(float minX, float minY, float maxX, float maxY, int maxElementsPerNode, QuadTree<T> tree) {
		this.bounds = new Box(minX, minY, maxX, maxY);
		setLeafs(new HashSet<QuadLeaf<T>>(maxElementsPerNode)); 
		setMaxElementsPerNode(maxElementsPerNode);
		setTree(tree);
	}

	public boolean put(QuadLeaf<T> leaf) {
		QuadNode<T> node = getChild(leaf.x, leaf.y);
		if (hasChildren.get() && node != null) return node.put(leaf);
		if(contains(leaf.value))
			return false;
		while(!lock()) {
			// spinlock
		}
		try {
			if (leafs.size() < maxElementsPerNode && leafs.add(leaf)) {
				leaf.node = this;
				return true;
			} else if(leafs.contains(leaf))
				return false;
			/*if (this.leaf.x == leaf.x && this.leaf.y == leaf.y) {
				boolean changed = false;
				for (T value : leaf.values) {
					if (!this.leaf.values.contains(value)) {
						changed = this.leaf.values.add(value) || changed;
					}
				}
				return changed;
			}*/
			this.divide();
			return getChild(leaf.x, leaf.y).put(leaf);
		} finally {
			unlock();
		}
	}

	public boolean put(float x, float y, T value) {
		return put(new QuadLeaf<T>(x, y, value, this));
	}

	public boolean remove(float x, float y, T value) {
		QuadNode<T> node = getChild(x, y);
		if (hasChildren.get() && value != null && node != null) return node.remove(x, y, value);
		while(!lock()) {
			
		}
		try {
			QuadLeaf<T> leaf = getLeafByValue(value);
			if (value != null && leaf != null) {
				return leafs.remove(leaf);
			}
		} finally {
			unlock();
		}
		return false;
	}
	
	public QuadLeaf<T> getLeafByValue(T value) {
		return leafs.stream().filter(l -> l.value == value).findFirst().orElse(null);
	}
	
	public boolean update(float x, float y, T value) {
		QuadNode<T> node = getChild(x, y);	
		if (hasChildren.get() && value != null && node != null) return node.update(x, y, value);

		boolean removed = false;
		
		while(!lock()) {
			
		}
		try {
			QuadLeaf<T> leaf = getLeafByValue(value);
			if(leaf == null)
				return false;
			// if we're still in this node just change the leafs position
			if(bounds.contains(x, y)) {
				leaf.x = x;
				leaf.y = y;
				return true;
			// else we need to remove the leaf from this node and do a new search for the position from the root node
			} else {
				removed = true;
				leafs.remove(leaf);
			}
		} finally {
			unlock();
		}
		if(removed) {
			return tree.put(x, y, value);
		}
			
		return false;
	}
	
	public Box getBounds() {
		return this.bounds;
	}

	public void clear() {
		if (hasChildren.get()) {
			this.NW.clear();
			this.NE.clear();
			this.SE.clear();
			this.SW.clear();
			this.NW = null;
			this.NE = null;
			this.SE = null;
			this.SW = null;
			this.hasChildren.set(false);
		} else {
			this.leafs.clear();
		}
	}

	public T get(float x, float y, AbstractFloat bestDistance) {
		if (hasChildren.get()) {
			T closest = null;
			QuadNode<T> bestChild = this.getChild(x, y);
			if (bestChild != null) {
				closest = bestChild.get(x, y, bestDistance);
			}
			if (bestChild != this.NW && this.NW.bounds.calcDist(x, y) < bestDistance.value) {
				T value = this.NW.get(x, y, bestDistance);
				if (value != null) { closest = value; }
			}
			if (bestChild != this.NE && this.NE.bounds.calcDist(x, y) < bestDistance.value) {
				T value = this.NE.get(x, y, bestDistance);
				if (value != null) { closest = value; }
			}
			if (bestChild != this.SE && this.SE.bounds.calcDist(x, y) < bestDistance.value) {
				T value = this.SE.get(x, y, bestDistance);
				if (value != null) { closest = value; }
			}
			if (bestChild != this.SW && this.SW.bounds.calcDist(x, y) < bestDistance.value) {
				T value = this.SW.get(x, y, bestDistance);
				if (value != null) { closest = value; }
			}
			return closest;
		}			
		

		if (leafs.size() > 0) {
			while(!lock()) {
				
			}
			try {
				T bestValue = null;
				for(QuadLeaf<T> leaf : leafs) {
					T value = leaf.value;
					float distance = (float) Math.sqrt(
							(leaf.x - x) * (leaf.x - x)
							+ (leaf.y - y) * (leaf.y - y));
					if (distance < bestDistance.value) {
						bestDistance.value = distance;
						bestValue = value;
					}
				}
				return bestValue;
			} finally {
				unlock();
			}
		}
		return null;

	}

	public ArrayList<T> get(float x, float y, float maxDistance, ArrayList<T> values) {
		if (hasChildren.get()) {
			if (this.NW.bounds.calcDist(x, y) <= maxDistance) {
				this.NW.get(x, y, maxDistance, values);
			}
			if (this.NE.bounds.calcDist(x, y) <= maxDistance) {
				this.NE.get(x, y, maxDistance, values);
			}
			if (this.SE.bounds.calcDist(x, y) <= maxDistance) {
				this.SE.get(x, y, maxDistance, values);
			}
			if (this.SW.bounds.calcDist(x, y) <= maxDistance) {
				this.SW.get(x, y, maxDistance, values);
			}
			return values;
		}
		if (this.leafs.size() > 0) {
			while(!lock()) {
				
			}
			try {
				for(QuadLeaf<T> leaf : leafs) {
					float distance = (leaf.x - x) * (leaf.x - x) + (leaf.y - y) * (leaf.y - y);
					if (distance <= maxDistance * maxDistance) {
						values.add(leaf.value);
					}
				}
			} finally {
				unlock();
			}
		}
		return values;
	}

	/*public ArrayList<T> get(Box bounds, ArrayList<T> values) {
		if (this.hasChildren) {
			if (this.NW.bounds.intersects(bounds)) {
				this.NW.get(bounds, values);
			}
			if (this.NE.bounds.intersects(bounds)) {
				this.NE.get(bounds, values);
			}
			if (this.SE.bounds.intersects(bounds)) {
				this.SE.get(bounds, values);
			}
			if (this.SW.bounds.intersects(bounds)) {
				this.SW.get(bounds, values);
			}
			return values;
		}
		if (this.leaf != null && this.leaf.values.size() > 0 && bounds.contains(this.leaf.x, this.leaf.y)) {
			values.addAll(this.leaf.values);
		}
		return values;
	}*/

	/*public int execute(Box globalBounds, QuadTree.Executor<T> executor) {
		int count = 0;
		if (this.hasChildren) {
			if (this.NW.bounds.intersects(globalBounds)) {
				count += this.NW.execute(globalBounds, executor);
			}
			if (this.NE.bounds.intersects(globalBounds)) {
				count += this.NE.execute(globalBounds, executor);
			}
			if (this.SE.bounds.intersects(globalBounds)) {
				count += this.SE.execute(globalBounds, executor);
			}
			if (this.SW.bounds.intersects(globalBounds)) {
				count += this.SW.execute(globalBounds, executor);
			}
			return count;
		}
		if (this.leaf != null && this.leaf.values.size() > 0 && globalBounds.contains(this.leaf.x, this.leaf.y)) {
			count += this.leaf.values.size();
			for (T object : this.leaf.values) executor.execute(this.leaf.x, this.leaf.y, object);
		}
		return count;
	}*/

	private void divide() {
		hasChildren.compareAndSet(false, true);
		this.NW = new QuadNode<T>(this.bounds.minX, this.bounds.centreY, this.bounds.centreX, this.bounds.maxY, maxElementsPerNode, tree);
		this.NE = new QuadNode<T>(this.bounds.centreX, this.bounds.centreY, this.bounds.maxX, this.bounds.maxY, maxElementsPerNode, tree);
		this.SE = new QuadNode<T>(this.bounds.centreX, this.bounds.minY, this.bounds.maxX, this.bounds.centreY, maxElementsPerNode, tree);
		this.SW = new QuadNode<T>(this.bounds.minX, this.bounds.minY, this.bounds.centreX, this.bounds.centreY, maxElementsPerNode, tree);
		if (this.leafs.size() > 0) {
			for(QuadLeaf<T> leaf : leafs)
				getChild(leaf.x, leaf.y).put(leaf);
			leafs.clear();
		}
	}

	private QuadNode<T> getChild(float x, float y) {
		if (hasChildren.get()) {
			if (x < this.bounds.centreX) {
				if (y < this.bounds.centreY)
					return this.SW;
				return this.NW;
			}
			if (y < this.bounds.centreY)
				return this.SE;
			return this.NE;
		}
		return null;
	}

	public QuadLeaf<T> firstLeaf() {
		if (hasChildren.get()) {
			QuadLeaf<T> leaf = this.SW.firstLeaf();
			if (leaf == null) { leaf = this.NW.firstLeaf(); }
			if (leaf == null) { leaf = this.SE.firstLeaf(); }
			if (leaf == null) { leaf = this.NE.firstLeaf(); }
			return leaf;
		}
		QuadLeaf<T> leaf = null;
		while(!lock()) {
			
		}
		try {
			for(QuadLeaf<T> leaf2 : leafs) {
				leaf = leaf2;
				break;
			}
		} finally {
			unlock();
		}
		return leaf;
	}

	public boolean nextLeaf(QuadLeaf<T> currentLeaf, AbstractLeaf<T> nextLeaf) {
		if (hasChildren.get()) {
			boolean found = false;
			if (currentLeaf.x <= this.bounds.centreX && currentLeaf.y <= this.bounds.centreY) {
				found = this.SW.nextLeaf(currentLeaf, nextLeaf);
				if (found) {
					if (nextLeaf.value == null) { nextLeaf.value = this.NW.firstLeaf(); }
					if (nextLeaf.value == null) { nextLeaf.value = this.SE.firstLeaf(); }
					if (nextLeaf.value == null) { nextLeaf.value = this.NE.firstLeaf(); }
					return true;
				}
			}
			if (currentLeaf.x <= this.bounds.centreX && currentLeaf.y >= this.bounds.centreY) {
				found = this.NW.nextLeaf(currentLeaf, nextLeaf);
				if (found) {
					if (nextLeaf.value == null) { nextLeaf.value = this.SE.firstLeaf(); }
					if (nextLeaf.value == null) { nextLeaf.value = this.NE.firstLeaf(); }
					return true;
				}
			}
			if (currentLeaf.x >= this.bounds.centreX && currentLeaf.y <= this.bounds.centreY) {
				found = this.SE.nextLeaf(currentLeaf, nextLeaf);
				if (found) {
					if (nextLeaf.value == null) { nextLeaf.value = this.NE.firstLeaf(); }
					return true;
				}
			}
			if (currentLeaf.x >= this.bounds.centreX && currentLeaf.y >= this.bounds.centreY) {
				return this.NE.nextLeaf(currentLeaf, nextLeaf);
			}
			return false;
		}
		return leafs.contains(currentLeaf);
	}

	public QuadLeaf<T> nextLeaf(QuadLeaf<T> currentLeaf) {
		AbstractLeaf<T> nextLeaf = new AbstractLeaf<T>(null);
		nextLeaf(currentLeaf, nextLeaf);
		return nextLeaf.value;
	}
	
	public boolean lock() {
		return lock.compareAndSet(false, true);
	}

	public boolean unlock() { return lock.compareAndSet(true, false); }

	public boolean contains(T value) {
		if(hasChildren.get()) {
			if(NW != null && NW.contains(value))
				return true;
			if(NE != null && NE.contains(value))
				return true;
			if(SW != null && SW.contains(value))
				return true;
			if(SE != null && SE.contains(value))
				return true;
			return false;
		} else {
			while(!lock()) {
				
			}
			try {
				for(QuadLeaf<T> leaf : leafs) {
					if(leaf.value == value)
						return true;
				}
			} finally {
				unlock();
			}
			return false;
		}
	}

	public Set<QuadLeaf<T>> getLeafs() {
		return leafs;
	}

	public void setLeafs(Set<QuadLeaf<T>> leafs) {
		this.leafs = leafs;
	}

	public int getMaxElementsPerNode() {
		return maxElementsPerNode;
	}

	public void setMaxElementsPerNode(int maxElementsPerNode) {
		this.maxElementsPerNode = maxElementsPerNode;
	}

	public QuadTree<T> getTree() {
		return tree;
	}

	public void setTree(QuadTree<T> tree) {
		this.tree = tree;
	}

}