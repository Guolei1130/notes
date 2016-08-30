
**以下源码基于Java 1.8**

### 0.HashMap中的关键变量

* MINIMUN_CAPACITY = 4 (最小容量)
* MAXIMUN_CAPACITY = 1 << 30 ; (最大容量)
* private static final Entry[] EMPTY_TABLE= new HashMapEntry[MINIMUM_CAPACITY >>> 1]; 这里的这个就是hash表，是一种数组链表结构(和字典一样)，默认的容量大小为4>>1，也就是2
* DEFAULT_LOAD_FACTOR 负载因子，默认是0.75F
* modCount 修改次数
* threshold 阀值
* 其他

### 1、HashMapEntry
看HashMapEntry的构造函数。
```
HashMapEntry(K key, V value, int hash, HashMapEntry<K, V> next) {
	this.key = key;
	this.value = value;
	this.hash = hash;
	this.next = next;
}
```

从中可以看出，这是一个单链表的数据结构，存有key、value、hash值以及下一个节点。

### 2、HashMap的的初始化

* HashMap()
* HashMap(int capacity)
* HashMap(int capacity,float loadFactor)

第三个构造方法，直接调用的是第一个构造方法，并对loadFactor进行判断(然而，这并没有什么吊用)
那么。我们就来看HashMap的代码吧。
```
    public HashMap() {
        table = (HashMapEntry<K, V>[]) EMPTY_TABLE;
        threshold = -1; // Forces first put invocation to replace EMPTY_TABLE
    }
```
* 这里的这个table是什么呢？因为这是个数组，而数组中每个元素都是单链表，所有，就构成table的样式了。
* threshold = -1，看注释是说，首次调用替换掉EMPTY_TABLE.

### 3、添加数据

* put(K key, V value)
* putAll(Map<? extends K, ? extends V> map)

#### 3.1、put(K key,V value)
```
    @Override public V put(K key, V value) {
        if (key == null) {
            return putValueForNullKey(value);
        }

        int hash = Collections.secondaryHash(key);
        HashMapEntry<K, V>[] tab = table;
        int index = hash & (tab.length - 1);
        for (HashMapEntry<K, V> e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash && key.equals(e.key)) {
                preModify(e);
                V oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }

        // No entry for (non-null) key is present; create one
        modCount++;
        if (size++ > threshold) {
            tab = doubleCapacity();
            index = hash & (tab.length - 1);
        }
        addNewEntry(key, value, hash, index);
        return null;
    }
```

* 若key为null，则将值存放在entryForNullKey(当然会做一些处理)
* 算出key对应的hash值
* 根据hash值计算出index值取到对应的链表，如果存在hash值相等并且key值相等的的Entry，就修改value值，并返回旧的value值
* 如果size++大于了阀值，对进行扩容并从新计算index值
* 插入一个新的Entry，并返回null

下面来对上面的1,4,5进行说明

##### 3.1.1 putValueForNullKey操作
相对应的源码如下。
```
    private V putValueForNullKey(V value) {
        HashMapEntry<K, V> entry = entryForNullKey;
        if (entry == null) {
            addNewEntryForNullKey(value);
            size++;
            modCount++;
            return null;
        } else {
            preModify(entry);
            V oldValue = entry.value;
            entry.value = value;
            return oldValue;
        }
    }
```
这里对应的操作也很简单，如果当前entryForNullKey为null的话，就添加一个，不为null，就修改值

#### 3.1.2 doubleCapacity() 扩容
扩容部分源代码较长，咱们分段来看。
```
        HashMapEntry<K, V>[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            return oldTable;
        }
        int newCapacity = oldCapacity * 2;
        HashMapEntry<K, V>[] newTable = makeTable(newCapacity);
        if (size == 0) {
            return newTable;
        }
```

* 如果到最大容量了，直接返回
* 将容量设置为原来的2倍
* 制造一个table，(ps：制造的时候会将阀值设置为3/4，（容量>>1） + (容量>>2)，>>1 相当于/2，>>2 相当于/4)
* 如果size(原先存储的数目)为0,直接返回

```
        for (int j = 0; j < oldCapacity; j++) {
            /*
             * Rehash the bucket using the minimum number of field writes.
             * This is the most subtle and delicate code in the class.
             */
            HashMapEntry<K, V> e = oldTable[j];
            if (e == null) {
                continue;
            }
            int highBit = e.hash & oldCapacity;
            HashMapEntry<K, V> broken = null;
            newTable[j | highBit] = e;
            for (HashMapEntry<K, V> n = e.next; n != null; e = n, n = n.next) {
                int nextHighBit = n.hash & oldCapacity;
                if (nextHighBit != highBit) {
                    if (broken == null)
                        newTable[j | nextHighBit] = n;
                    else
                        broken.next = n;
                    broken = e;
                    highBit = nextHighBit;
                }
            }
            if (broken != null)
                broken.next = null;
        }
        return newTable;
```

* 上面代码的就是将原table中每一处对应的链表取出来，并且从新散列

##### 3.1 addNewEntry添加新的Entry
```
table[index] = new HashMapEntry<K, V>(key, value, hash, table[index]);
```
其中table[index]就是一个单链表，这里就是生成一个HashMapEntry并将其插入到index处的，当然，我们还需要看一下生成的构造方法。
```
        HashMapEntry(K key, V value, int hash, HashMapEntry<K, V> next) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }
```
结合逻辑可以知道，我们可以看的出，每次是在链表的头部进行数据插入的。

#### 3.2、putAll
```
    @Override public void putAll(Map<? extends K, ? extends V> map) {
        ensureCapacity(map.size());
        super.putAll(map);
    }
```
* ensureCapacity，确保容量(这里就是进行容量检查，不够扩容，具体的细节就不说了)
* 调用父类去put数据

在这里我们就需要明白父类的实现了。
```
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
```
从中可以看出，如果穿进来的是另一个HashMap的话，就会将这个HashMap中的Entry挨个加入到运来的HashMap中。



### 4、get取值
取值的过程因为在散列与散列码中，有提到过，所以这里就不多说了。

### 5、总结
HashMap查询快速的原因就在于hashtable的思想。就像字典一样。

本博文中只是简单的介绍了下HashMap。当然HaspMap中还有许多值得我们去思考的问题，诸如：

* 负载因子  为什么是0.75？
* 初始容量为什么在Java8中改成2了
* 散列时index的算法
* 为什么每次扩容是*2
* 为什么从新散列是那样求index的
* 其他

等等，这些问题，每一个都值得我们去好好地研究。