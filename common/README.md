# 基础通用工具模块

用途：提供基础工具类、方法。  
包名：`cc.buddies.component.common`

## 内部引用库

1. 内部使用`compileOnly`方式引用库，为某些封装工具使用到的库，该库未在当前模块直接引用，
需要在使用到对应工具方法的时候，在对应模块引用相应库，未用到则不需引入。（推荐使用`implementation`方式引用。）

    > compileOnly 'androidx.appcompat:appcompat:1.1.0'  
    > compileOnly 'androidx.fragment:fragment:1.2.5'  
    > compileOnly 'androidx.recyclerview:recyclerview:1.1.0'  
    > compileOnly 'androidx.exifinterface:exifinterface:1.2.0'  // 只在MediaStoreAddition类使用  

2. 内部使用`api`方式引用库，此方式引用库不只在当前模块使用，并且将该库外抛出去，所有引用当前模块的模块，均可使用此方式引用库。

    > api 'commons-io:commons-io:2.6'  // apache io 工具库  
    > api 'commons-codec:commons-codec:1.14'  // apache 消息摘要/字符编解码处理 工具库  

## 资源文件

1. AndroidManifest

    a. 提供了两个基本使用权限

    ```Java
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />  
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />  
    ```

    b. 使用ContentProvider提供了全局上下文ApplicationContext

    使用：`cc.buddies.component.common.provider.CommonContextProvider`

    ```Java
    <provider
        android:name=".provider.CommonContextProvider"
        android:authorities="${applicationId}.common.context-provider"
        android:exported="false" />
    ```

    c. 提供了共享虚拟目录FileProvier

    使用：`cc.buddies.component.common.helper.FileProviderHelper`  
    映射目录：参见xml目录下`provider_file_paths.xml`文件

    ```Java
    <provider
        android:name="androidx.core.content.FileProvider"
        android:authorities="${applicationId}.file-provider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/provider_file_paths" />
    </provider>
    ```

2. layout

    a. default_dialog_loading.xml

    该文件为通用加载中弹窗`CustomLoadingDialog`布局文件，如果想要自定义样式，可以重新定义同名文件，打包的时候会将原本文件覆盖。

    b. default_dialog_progress.xml

    该文件为通用加载中弹窗`CustomProgressDialog`布局文件，如果想要自定义样式，可以重新定义同名文件，打包的时候会将原本文件覆盖。

3. drawable

    a. default_loading_dialog_background.xml

    CustomLoadingDialog、CustomProgressDialog弹窗背景样式

    b. default_loading_dialog_progress.xml

    CustomLoadingDialog加载中样式

    c. default_toast_background.xml

    ToatUtils自定义Toast背景样式

4. strings

    a. app_storage_directory

    在StorageUtils中封装了获取SD卡上外部存储目录位置的方法 getDefaultAppExternalStorageDir(Context context) 默认获取`app_storage_directory`所标识的名称（该标识字符串默认为空），如果为空则使用应用包名。作为资源文件，同样可以在app模块重新定义同名资源文件，在打包的时候将原本值覆盖。（*注意*：获取此目录需要读取存储卡权限）

## 存储使用

1. io相关工具，使用commons-io库，不再单独实现。

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
