### 0 HashMap中的关键变量

* DEFAULT_INITIAL_CAPACITY 默认容量16 1<<4 
* MAXIMUM_CAPACITY 最大容量 1<<30
* DEFAULT_LOAD_FACTOR 负载因子 0.75f
* TREEIFY_THRESHOLD 树的阀值
* UNTREEIFY_THRESHOLD
* MIN_TREEIFY_CAPACITY 树最小容量

### 1.put

put方法中调用putVal方法。该方法代码如下：
```
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<K,V> e; K k;
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }
```
下面就来简单的介绍下这里的流程。

#### 1.1 如果table为null或者tab.length为0，则resize()调整尺寸。

其中，resize的代码如下,resize返回一个数组(Node)
```
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
```

* 如果旧的table的长度大于0
	* 如果长度大于最大容量，那个就经阀值设置为Integer的最大值，并且返回原来的table
	* 如果，就容量左溢一位小于最大容量并且旧容量大于默认容量的话阀值*2
* 如果oldThr大于0的话，就将信用量设置为旧的阀值
* 新容量设置为默认容量，新的阀值设置负载因子*默认容量

好吧，接下来我们继续看。
```
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
```
* 如果新的阀值为0的话
	* ft就是新容量*负载因子
	* 那么新的阀值就是 如果新容量小于最大容量或者ft小雨最大容量的时候，则是ft，否则就是INtege的最大值
```
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    else { // preserve order
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
```

* 初始化一个newCap容量的Table
* 如果旧table不为null的话
	* 遍历旧的table
	* 去除j处的node，
		* 如果node的next为null的话，就把这个node复制给newtab的某一处
		* 如果e的类型为TreeNode的话，split()方法。
		* 否则，总的就是调整结构
* 返回新的table

#### 1.2 newNode方法，放(n-1)&hash处没有任何一个node的时候

返回一个Node对象，Node对象中有hash值，key，value和next

#### 1.3 其他

* 第一种情况，p就是e
* p是一个TreeNode对象，就调用putTreeVal方法。

### 2. 关键问题就是如何从Table转化为tree的。

### 3.TreeNode

TreeNode的数据结构如下：

* TreeNode parent 红黑树的连接
* TreeNode left  左孩子
* TreeNode right 右孩子
* TreeNode prev  需要分开下删除

root方法
```
        final TreeNode<K,V> root() {
            for (TreeNode<K,V> r = this, p;;) {
                if ((p = r.parent) == null)
                    return r;
                r = p;
            }
        }
```

计算出根节点

moveRootToFront 方法，确保给定根节点的第一个节点。
