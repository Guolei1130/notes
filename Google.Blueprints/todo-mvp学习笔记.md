### todo-mvp学习笔记
1.Activity结合Fragment的形式
2.MVP的关系图
 * BaseView interface setPresenter(T presenter)
 * BasePresenter interface start
 * Contract 约定，将presenter和view绑定起来，这里同样是接口
 * Presenter的实现，实现Presenter的地方。
  * 将Model和View注入
 * Repository实现Model层

这里和国内传统的MVP的区别之处就在于，将presenter和view关联起来，国内目前的所有方案里面是没有管理的。

这样的优点？
* 约束性强
* 减少文件 告别找文件的烦恼

凡是能引起UI变化的，都由Presenter来调用View就响应，也就是说，即使是一个监听器，相应的操作也放在view中。