说一下butterknife包下的内容。
### 1.bind方法
bind方法很简单，就是获取到当前activity的decorview，然后createBinding。
```
  @NonNull @UiThread
  public static Unbinder bind(@NonNull Activity target) {
    View sourceView = target.getWindow().getDecorView();
    return createBinding(target, sourceView);
  }
```

### 2.createBinding
```
Constructor<? extends Unbinder> constructor = findBindingConstructorForClass(targetClass);
```

```
      Class<?> bindingClass = Class.forName(clsName + "_ViewBinding");
      //noinspection unchecked
      bindingCtor = (Constructor<? extends Unbinder>) bindingClass.getConstructor(cls, View.class);
```
创建对应的Binding类，并调用其构造方法，而在其构造方法当中进行一些列的binding操作。一个例子如下。
```

    View view;
    target.title = Utils.findRequiredViewAsType(source, R.id.title, "field 'title'", TextView.class);
    target.subtitle = Utils.findRequiredViewAsType(source, R.id.subtitle, "field 'subtitle'", TextView.class);
    view = Utils.findRequiredView(source, R.id.hello, "field 'hello', method 'sayHello', and method 'sayGetOffMe'");
    target.hello = Utils.castView(view, R.id.hello, "field 'hello'", Button.class);
    view2130968578 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.sayHello();
      }
    });
    view.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View p0) {
        return target.sayGetOffMe();
      }
    });
    view = Utils.findRequiredView(source, R.id.list_of_things, "field 'listOfThings' and method 'onItemClick'");
    target.listOfThings = Utils.castView(view, R.id.list_of_things, "field 'listOfThings'", ListView.class);
    view2130968579 = view;
    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {
        target.onItemClick(p2);
      }
    });
    target.footer = Utils.findRequiredViewAsType(source, R.id.footer, "field 'footer'", TextView.class);
    target.headerViews = Utils.listOf(
        Utils.findRequiredView(source, R.id.title, "field 'headerViews'"), 
        Utils.findRequiredView(source, R.id.subtitle, "field 'headerViews'"), 
        Utils.findRequiredView(source, R.id.hello, "field 'headerViews'"));
```

这里比较简单，就不在多说了。