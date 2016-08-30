**以Java8为版本分析**

### 1.认识ArrayList的关键变量
在ArrayList中，关键变量有两个。

```
//存储对象的数组
transient Object[] elementData;
//数组长度
private int size;
```

* 之所以用transient关键词修饰，就是为了防止其被序列化。

当然，还有下面的一些变量
```
//默认容量
private static final int DEFAULT_CAPACITY = 10;
//空对象数组
private static final Object[] EMPTY_ELEMENTDATA = {};
//空对象数组，在初始化的时候有用。
private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
```

* ArrayList随机返回快的原因就是因为他将元素存在数组里面，线性存储最快的就是数组了吧。

### 2.先看初始化

有三种初始化的方法。

* ArrayList()
* ArrayList(Collection<? extends E> c) //传入一个COllection对象
* ArrayList(int initialCapacity) //传输初始化容量大小

三种初始化方法的区别之处在于对elementData数组对象的初始化操作，详细过程请看代码。这里说下结果

* 将elementData 空数组对象
* elementData，将c转化为数组对象并赋值
* 若大于0，则初始化大小为initialCapacity，等于0，为空数组对象，小于0，抛出异常

### 3.add操作
向ArrayList中添加数据的操作有四中，分别为：

* add(E e)
* add(int index, E element)
* addAll(Collection<? extends E> c)
* addAll(int index, Collection<? extends E> c)

分别对应

* 在末尾加入一个对象
* 在指定位置加入一个对象
* 在末尾加入一个集合
* 在指定位置加入一个集合

接下来，我以1,4的代码为例，在说明问题及详细流程。

#### 3.1 add(E e)
其对应的源代码如下：
```
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;
    }
```

非常简单的一段代码，其流程如下：

* 判断容量及做扩容操作
* 想对象添加到size的位置，并将size+1(利用好++的代码很优雅～)
* 返回结果

那么，关键性的代码就在于ensureCapacityInterna操作了。我们看看它做了什么。
```
    private void ensureCapacityInternal(int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }

        ensureExplicitCapacity(minCapacity);
    }
```

* 简单来说，就是再size+1 和 初始化大小10之间取大值(再一次被优雅的代码折服)

继续跟踪
```
    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;

        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }
```

* modCount是什么呢？从注释上来看，是记录这个list被修改的次数的
* 当前需要的容量大于现有容量的时候。就grow进行扩容操作

我们接下来看扩容操作的逻辑。
```
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
```

解释如下：

* 获取当前容量
* 新容量为旧容量 + (旧容量右移一位)
* 若新容量小于需要的容量，则将新容量设置需要的容量
* 如果新容量大于最大容量了，如果大于MAX_ARRAY_SIZE的话，设置Integer.MAXVALUE，否则为MAX_ARRAY_SIZE
* 生成一个新的对象数组，并将值复制过去，最后赋值给elementData。

采用这样扩容方法的好处是什么呢？我们想想，党我们一直往里塞数据的时候，容量会增长的越来越快(ArrayList以为我们贪得无厌了～)，同样，这样的坏处就是，党我们添加到最后的时候，我们可能申请了很大的空间，但是只往里加多了一个数据，这样就造成了浪费。不过，这都是小事～～～

#### 3.2 addAll(int index, Collection<? extends E> c)
```
    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckForAdd(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size + numNew);  // Increments modCount

        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                             numMoved);

        System.arraycopy(a, 0, elementData, index, numNew);
        size += numNew;
        return numNew != 0;
    }
```

* 将集合转化为对象数组
* 判断容量及扩容
* 如果需要移动 将elementData 从index的位置移动到index+numNew的位置
* 将a中的元素复制到elementData空开的位置

由于那两个方法是native方法，我们在这里就不说了。

### 4. get操作

```
    public E get(int index) {
        rangeCheck(index);

        return elementData(index);
    }
```
* rangeCheck做检查，如果index大于size就抛出异常。

### 5.set操作
```
    public E set(int index, E element) {
        rangeCheck(index);

        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }
```
### 6.总结
在知道ArrayList中使用的数据结构以及如何添加数据操作之后，明显对ArrayList理解了很多。

