# 基础通用主题模块

用途：提供基础主题、基础公共资源。由于不同项目内主题通常都有个性化定制，所以不建议将当前模块打包使用，建议将当前模块代码拷贝到项目中，并进行针对项目的定制。

包名：`cc.buddies.component.material`

## 内部引用库

1. 内部使用`compileOnly`方式引用appcompat库，app主题通常都以继承自该库，所以当前内部主题继承自appcompat并修改。

    > compileOnly 'androidx.appcompat:appcompat:1.1.0'

## 资源文件

1. color

    - common_ripple_default_dark.xml：不透明度为0.2的黑色，用于暗色水波纹。
    - common_ripple_default_light.xml：不透明度为0.25的白色，用于亮色水波纹。

2. drawable/drawable-v21

    - common_default_dark_selectable.xml：暗色点击效果（v21以后版本为水波纹样式），可以加到可点击视图的background或foreground属性当中。也可以使用系统主题样式"?android:attr/selectableItemBackground"。
    - common_default_light_selectable.xml：亮色点击效果（v21以后版本为水波纹样式），可以加到可点击视图的background或foreground属性当中。
    - common_btn_default_background.xml：`圆角矩形彩色背景按钮`点击样式（v21以后版本为水波纹样式）。
    - common_btn_default_borderless_background.xml：`圆角矩形透明背景按钮`点击样式（v21以后版本为水波纹样式）。
    - common_btn_default_round_background.xml：`大圆角彩色背景按钮`点击样式（v21以后版本为水波纹样式）。

3. layout

    - common_titlebar_dark.xml：暗色主题的Toolbar配置布局
    - common_titlebar_light.xml：亮色主题的Toolbar配置布局

4. values

    - colors.xml：基础公共主题配置，可定制修改。
    - dimens.xml：基础公共尺寸配置，可定制修改。
    - strings.xml：基础公共字符串配置，可定制修改。
    - styles.xml

        a. Common.AppTheme：继承自`Theme.AppCompat.Light.NoActionBar`的定制主题。内部关键配置为`colorButtonNormal`影响了其他定制按钮主题样式的颜色。  
        b. Common.AppTheme.AppBarOverlay.Dark：继承自`ThemeOverlay.AppCompat.Dark.ActionBar`的标题栏定制主题。暗色标题栏，文本及图标为白色。  
        c. Common.AppTheme.AppBarOverlay.Light：继承自`ThemeOverlay.AppCompat.ActionBar`的标题栏定制主题。亮色标题栏，文本及图标为黑色。  
        d. Common.AppTheme.PopupOverlay：继承自`ThemeOverlay.AppCompat.Light`的标题栏弹窗菜单主题。配置弹窗菜单位于标题栏之下，并且配置背景色。
        e. Common.LoadingDialog：加载中弹窗样式。
        f. Common.DialogActivity：加载中弹窗样式Activity主题。
