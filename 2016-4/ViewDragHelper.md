### ViewDragHelper
 * 当给控件设置clickable="true" 或者设置监听器的时候，控件就不能拖拽了。
 * 解决办法 重写getViewHorizontalDragRange()和getViewVerticalDragRange() 表示view在水平和垂直方向上移动的距离范围