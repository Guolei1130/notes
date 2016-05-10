### NavigationView
一个导航Menu，常常配合DrawerLayout 一起使用。
```
<?xml version="1.0" encoding="utf-8"?>
<android.support.v4.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="plugin.matericldesign.activity.EntranceActivity"
    android:fitsSystemWindows="true"
    android:id="@+id/drawerlayout"
    >
    <!-- content -->

    <android.support.design.widget.NavigationView
        android:id="@+id/navigationview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="left"
        android:fitsSystemWindows="false"
        app:headerLayout="@layout/view_left_header"
        app:menu="@menu/left_menu"
        />
</android.support.v4.widget.DrawerLayout>
```
NavigationView以下几个属性比较重要
 * headerLayout NavigationView 头部布局
 * menu 菜单部分布局
 * itemIconTint menu部分未选中icon的着色()
 * itemTextColor menu字体颜色(选中和未选中状态)
 * itemBackground item的背景(可以为图片、颜色等)
 * itemTextAppearance item中text的外观
 * theme 主题

默认情况下，menu部分item的icon是没有颜色的，我们可以通过itemIconTint去着色，接下来就从源码的角度来了解NavigationView。
从构造函数看起:
首先是检查Activity的Theme
```
ThemeUtils.checkAppCompatTheme(context);
```
要求AppTheme为Theme.AppCompat相关的theme
接下来是解析并设置相应的属性。
```
TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.NavigationView, defStyleAttr,
                R.style.Widget_Design_NavigationView);
```
有一个默认的style，在design的values中
```
<style name="Widget.Design.NavigationView" parent="">
        <item name="elevation">@dimen/design_navigation_elevation</item>
        <item name="android:background">?android:attr/windowBackground</item>
        <item name="android:fitsSystemWindows">true</item>
        <item name="android:maxWidth">@dimen/design_navigation_max_width</item>
    </style>
```
接下来是设置上面那4个默认的属性。
接下来是menu item icon的ColorStateList
```
final ColorStateList itemIconTint;
        if (a.hasValue(R.styleable.NavigationView_itemIconTint)) {
            itemIconTint = a.getColorStateList(R.styleable.NavigationView_itemIconTint);
        } else {
            itemIconTint = createDefaultColorStateList(android.R.attr.textColorSecondary);
        }
```
从这里可以看到 我们可以通过设置itemIconTint来改变默认状态的图标颜色。
在源码中我们能够看到，通过NavigationMenuPresenter来操作view。
```
mPresenter.setId(PRESENTER_NAVIGATION_VIEW_ID);
        mPresenter.initForMenu(context, mMenu);
        mPresenter.setItemIconTintList(itemIconTint);
        if (textAppearanceSet) {
            mPresenter.setItemTextAppearance(textAppearance);
        }
        mPresenter.setItemTextColor(itemTextColor);
        mPresenter.setItemBackground(itemBackground);
        mMenu.addMenuPresenter(mPresenter);
```
通过上面这段代码将mMenu和mPresenter关联起来，并做了一些初始化。
```
addView((View) mPresenter.getMenuView(this));

        if (a.hasValue(R.styleable.NavigationView_menu)) {
            inflateMenu(a.getResourceId(R.styleable.NavigationView_menu, 0));
        }

        if (a.hasValue(R.styleable.NavigationView_headerLayout)) {
            inflateHeaderView(a.getResourceId(R.styleable.NavigationView_headerLayout, 0));
        }
```
最后就是添加view了。