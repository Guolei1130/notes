### 0.LinkedList
LinkedList即实现了List接口，也实现了Deque接口，其底层实现为双向链表。链表的特点就是在中间插入数据快，而查询数据慢。

### 1.LinkedList中的Node
```
    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
```

* 从中可以看到底层数据为双向链表。Node中包含上一个节点，下一个节点，以及自己的信息。

### 2.List接口add相关方法的实现
list接口的添加数据的方法有四个，如下：

* add(E e)
* add(int index, E element)
* addAll(Collection<? extends E> c)
* addAll(int index, Collection<? extends E> c)

下面来说明这个方法的实现。

#### 2.1 add(E e)
在这个方法中调用linkLast去加入数据。
```
    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }
```

* 生成一个新的node节点
* 将这个新节点赋值给last节点，作为链表的最后一个节点
* 如果l为null，说明是第一次加入数据，就将这个节点置为first节点，代表链表中第一个有数据的节点(ps：最开头应该是一个空数据的节点)
* 如果不是，则将上一个节点的next指向这个节点
* size++，modeCount++

#### 2.2 add(int index, E element)
在指定的位置加入节点。
```
    public void add(int index, E element) {
        checkPositionIndex(index);

        if (index == size)
            linkLast(element);
        else
            linkBefore(element, node(index));
    }
```

* 判断index是否合法
* 如果index等于size，那么就在链表最后出加入节点
* 在中间插入节点

我们先来看下node(index)的逻辑。
```
    Node<E> node(int index) {
        // assert isElementIndex(index);

        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }
```
这是一段很niu b的代码。下面来一一解析。

* size>>1 相当于 size /2
* 判断index 距离左端近还有右端近，在将index出的node查找出来并返回。

接下来再看linkBefore方法。
```
    void linkBefore(E e, Node<E> succ) {
        // assert succ != null;
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, e, succ);
        succ.prev = newNode;
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        size++;
        modCount++;
    }
```

* pred  当前节点的上一个节点
* newNode 将要插入的对象包装成node节点(指定上一个节点，下一个节点)
* 将当前节点的上一个节点指定为我们生成的新节点
* 如果当前节点的上一个节点为null，说明是该节点为第一个有数据的节点，就将生成的新节点置为first节点，否则指定上个节点的下一个节点为生成的新节点，这样就把数据插入进去了。在中间插入节点块的原因就在于不用想ArrayList那样进行数组复制的动作。
* size++，modCount++

#### 2.3  addAll(int index, Collection<? extends E> c)

因为addAll(Collection<? extends E> c)方法就是直接调用这个方法，因此就来分析下这个方法。因为这个方法有点长，所以就来分步讲解。
```
        checkPositionIndex(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        if (numNew == 0)
            return false;

        Node<E> pred, succ;
        if (index == size) {
            succ = null;
            pred = last;
        } else {
            succ = node(index);
            pred = succ.prev;
        }
```

* 国际惯例 先检查index是否合法
* 将Collection转成对象数组，判断长度，若为0，直接返回44
* 如果index等于size的话，就说明要在链表最后插入数据，则，succ(当前节点置为null，pred置为链表的最后一个节点)
* 否则，将succ置为index的节点，pred为当前节点的上一个节点

```
        for (Object o : a) {
            @SuppressWarnings("unchecked") E e = (E) o;
            Node<E> newNode = new Node<>(pred, e, null);
            if (pred == null)
                first = newNode;
            else
                pred.next = newNode;
            pred = newNode;
        }
```

这里的操作就很明白了

* 循环取出对象数组中的对象，并生成新的节点
* 判断pred是否为为null，为null就将新节点设置first节点，否则就将pred的节点的下一个节点指向新节点，并将生成的节点复制给pred。

总的来说，就是循环生成节点，并指定上一个节点的next节点，并将上一个节点置为这个刚生成的节点，值得一提的是，在生成节点的时候，指定了节点的上一个节点，这样下来，就形成了双向链表。

要注意到的是，这样的步骤下来，我们并没有指定生成的最后一个节点的下一个节点，因此，有如下代码：

```
        if (succ == null) {
            last = pred;
        } else {
            pred.next = succ;
            succ.prev = pred;
        }
```

这里就是用来指定最后节点的下一个节点的。

* 若succ为null，这说明是在链表的最后插入数据的，将last置为pred即可，
* 若不是，则指定插入的最后一个节点的下一个节点为succ(index处原来的节点)，并给这个节点指定prev(该节点的上一个节点)

### 3.List接口的get方法实现
这里就很简单了，代码如下：
```
    public E get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }
```

* 直接找到node节点，返回item数据

### 4.队列相关
因为队列是一种先进先出的方式，插入数据的逻辑并没有什么变化。因此，这里就不说了。

