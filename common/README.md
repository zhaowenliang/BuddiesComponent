# 基础通用工具模块

用途：提供基础工具类、方法。  
包名：`cc.buddies.component.common`

## 引用库

1. 内部使用`compileOnly`方式引用库，为某些封装工具使用到的库，该库未在当前模块直接引用，
需要在使用到对应工具方法的时候，在对应模块引用相应库，未用到则不需引入。（推荐使用`implementation`方式引用。）

    > compileOnly 'androidx.appcompat:appcompat:1.1.0'  
    > compileOnly 'androidx.fragment:fragment:1.2.5'  
    > compileOnly 'androidx.recyclerview:recyclerview:1.1.0'  
    > compileOnly 'androidx.exifinterface:exifinterface:1.2.0'  // 只在MediaStoreAddition类使用  

2. io相关推荐使用common-io库，编解码相关推荐使用common-codec库。

    > implementation 'commons-io:commons-io:2.6'  // apache io 工具库  
    > implementation 'commons-codec:commons-codec:1.14'  // apache 消息摘要/字符编解码处理 工具库  

## 资源文件

1. layout

    a. custom_dialog_loading.xml

    该文件为通用加载中弹窗`CustomLoadingDialog`布局文件，如果想要自定义样式，可以重新定义同名文件，打包的时候会将原本文件覆盖。

    b. custom_dialog_progress.xml

    该文件为通用进度弹窗`CustomProgressDialog`布局文件，如果想要自定义样式，可以重新定义同名文件，打包的时候会将原本文件覆盖。

2. drawable

    a. custom_loading_dialog_background.xml：CustomLoadingDialog、CustomProgressDialog弹窗背景样式  
    b. custom_loading_dialog_progress.xml：CustomLoadingDialog加载中样式  
    c. custom_toast_background.xml：ToastUtils自定义Toast背景样式  

## 存储使用

1. io相关工具，使用commons-io库。

    > {@link org.apache.commons.io.FileUtils}  
    > {@link org.apache.commons.io.FilenameUtils}  
    > {@link org.apache.commons.io.IOUtils}  

2. 消息摘要及加解密处理，使用commons-codec库。

    > {@link org.apache.commons.codec.digest.DigestUtils}  
    > {@link org.apache.commons.codec.digest.Md5Crypt}  
    > {@link org.apache.commons.codec.binary.Base64}  

3. 内存缓存

    > {@link android.util.LruCache}

4. 磁盘缓存

   该工具来自于 [GitHub DiskLruCache](https://github.com/JakeWharton/DiskLruCache)

   > {@link com.jakewharton.disklrucache.DiskLruCache}

5. 数据缓存队列

    > 单向队列：{@link java.util.Queue}的子类  
    > 双向队列：{@link java.util.Deque}的子类  
