//from jiuzhang
public class LFUCache {

    private final Map<Integer, CacheNode> cache;
    private final LinkedHashSet[] frequencyList;
    private int lowestFrequency;
    private int maxFrequency;
    private final int maxCacheSize;

    // @param capacity, an integer
    public LFUCache(int capacity) {
        // cache = key + cacheNode
        this.cache = new HashMap<Integer, CacheNode>(capacity);
        // keep the order as they added to the set
        this.frequencyList = new LinkedHashSet[capacity * 2];
        this.lowestFrequency = 0;
        this.maxFrequency = capacity * 2 - 1;
        this.maxCacheSize = capacity;
        initFrequencyList();
    }

    // @param key, an integer
    // @param value, an integer
    // @return nothing
    public void set(int key, int value) {
        CacheNode currentNode = cache.get(key);
        // if key doesn't exist, if reach capacity, evict elements
        // add current node to frequency0 list
        if (currentNode == null) {
            if (cache.size() == maxCacheSize) {
                doEviction();
            }
            LinkedHashSet<CacheNode> nodes = frequencyList[0];
            currentNode = new CacheNode(key, value, 0);
            nodes.add(currentNode);
            cache.put(key, currentNode);
            lowestFrequency = 0;
        } else {
            // if key exists, update value
            currentNode.v = value;
        }
        // increase frequency
        addFrequency(currentNode);
    }

    public int get(int key) {
        CacheNode currentNode = cache.get(key);
        if (currentNode != null) {
            // increase frequency
            addFrequency(currentNode);
            return currentNode.v;
        } else {
            return -1;
        }
    }

    // increase currentNode's frequency
    public void addFrequency(CacheNode currentNode) {
        int currentFrequency = currentNode.frequency;
        // if less than maxFrequency
        if (currentFrequency < maxFrequency) {
            int nextFrequency = currentFrequency + 1;
            //increase currentNode's frequency and move it to next level frequency
            LinkedHashSet<CacheNode> currentNodes = frequencyList[currentFrequency];
            LinkedHashSet<CacheNode> newNodes = frequencyList[nextFrequency];
            moveToNextFrequency(currentNode, nextFrequency, currentNodes, newNodes);
            // 等价于 update currentNode 的value
            cache.put(currentNode.k, currentNode);
            // if currentNode is lowestFrequency and lowestFrequency list is empty
            // lowestFrequency = next level
            if (lowestFrequency == currentFrequency && currentNodes.isEmpty()) {
                lowestFrequency = nextFrequency;
            }
        } else {
            // Hybrid with LRU: put most recently accessed ahead of others:
            LinkedHashSet<CacheNode> nodes = frequencyList[currentFrequency];
            // 等价于把最老的给移除
            nodes.remove(currentNode);
            nodes.add(currentNode);
        }
    }

    public int remove(int key) {
        CacheNode currentNode = cache.remove(key);
        if (currentNode != null) {
            LinkedHashSet<CacheNode> nodes = frequencyList[currentNode.frequency];
            nodes.remove(currentNode);
            if (lowestFrequency == currentNode.frequency) {
                findNextLowestFrequency();
            }
            return currentNode.v;
        } else {
            return -1;
        }
    }

    public int frequencyOf(int key) {
        CacheNode node = cache.get(key);
        if (node != null) {
            return node.frequency + 1;
        } else {
            return 0;
        }
    }

    public void clear() {
        for (int i = 0; i <= maxFrequency; i++) {
            frequencyList[i].clear();
        }
        cache.clear();
        lowestFrequency = 0;
    }

    public int size() {
        return cache.size();
    }

    public boolean isEmpty() {
        return this.cache.isEmpty();
    }

    public boolean containsKey(int key) {
        return this.cache.containsKey(key);
    }
    
    // create a frequencyList with init size = 2 * capacity - 1
    private void initFrequencyList() {
        for (int i = 0; i <= maxFrequency; i++) {
            frequencyList[i] = new LinkedHashSet<CacheNode>();
        }
    }

    // delete target number elements in frequencylist with lowestFrequency count
    private void doEviction() {
        int currentlyDeleted = 0;
        double target = 1; // just one
        while (currentlyDeleted < target) {
            LinkedHashSet<CacheNode> nodes = frequencyList[lowestFrequency];
            if (nodes.isEmpty()) {
                break;
            } else {
                Iterator<CacheNode> it = nodes.iterator();
                while (it.hasNext() && currentlyDeleted++ < target) {
                    CacheNode node = it.next();
                    it.remove();
                    cache.remove(node.k);
                }
                if (!it.hasNext()) {
                    findNextLowestFrequency();
                }
            }
        }
    }

    // move currentNode from currentNodes to newNodes with frequency = nextFrequency
    private void moveToNextFrequency(CacheNode currentNode, int nextFrequency,
                                     LinkedHashSet<CacheNode> currentNodes,
                                     LinkedHashSet<CacheNode> newNodes) {
        currentNodes.remove(currentNode);
        newNodes.add(currentNode);
        currentNode.frequency = nextFrequency;
    }
    
    // find next frequency in frequencyList that the count of elements for this frequency is not empty.
    private void findNextLowestFrequency() {
        while (lowestFrequency <= maxFrequency && frequencyList[lowestFrequency].isEmpty()) {
            lowestFrequency++;
        }
        if (lowestFrequency > maxFrequency) {
            lowestFrequency = 0;
        }
    }

    private class CacheNode {
        public final int k;
        public int v;
        public int frequency;

        public CacheNode(int k, int v, int frequency) {
            this.k = k;
            this.v = v;
            this.frequency = frequency;
        }
    }
}
