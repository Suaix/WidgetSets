#WidgetSets
WidgetSets是用来整理平时开发工作中为了解决需求问题而自定义的一些控件，主要是为了满足项目的需求的，因此有些控件没有做深度的抽象集成，待日后完善吧；如果各位是在使用中发现什么bug或问题，欢迎提Issues！

##VerticalSlideLayout
![VertivalSlideLayout效果图](https://github.com/Suaix/WidgetSets/blob/master/img/vertical_slide.gif)

VerticalSlideLayout的效果有点类似三星手机的任务管理器的效果，这里只是做了Y轴方向上的位移改变，如果想做成三星那样的效果，只需要加上缩放效果就可以了；
VerticalSlideLayout自定义了一些属性，如下：
itemHeigh:指定的是子控件的高度，宽度为手机宽度；

clickExpandSmallItem:点击未展开的Item时是否展开Item；

lastVisiableCount：末端可见的未展开的Item个数，默认为3个；

loadMoreCount:末端还剩几个的时候出发加载更多操作，默认为4个；注意loadMoreCount必须必lastVisiableCount大，否则无法出发加载更多操作。

##NomarlDistributionLayout
![NomarlDistributionLayout效果图](https://github.com/Suaix/WidgetSets/blob/master/img/nomarl_distribution.gif)

这是一个正态分布曲线图，用来做风险评估或者偏好选择的，运行效果如上图；控件提供部分自定样式或内容的方法，具体说明如下：

setBackgroundColor:设置正态分布图的背景色；

setValueLinePosition:设置数值线的位置，参数为坐标系的取值范围；

setNomarlDistributionPosition:设置正态分布图最高点所在的位置；

setOnValueChangedListener:设置滑动数值变化的监听器；
