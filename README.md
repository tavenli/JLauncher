JLauncher
=========

JLauncher provides a easy way to start a Java application.

Demo Download:
https://pan.baidu.com/s/10FOpME1QTZZ4IJUpM7V9rw


JLauncher提供一种简单的方式去启动一个Java App程序，免去将Java打包成独立的Run Jar包，方便在部署过程中对配置文件的灵活修改，以及实现轻量级对App程序依赖Jar包的更新。

    程序支持默认存放路径：

    DemoApp/

        config/

        lib/

        jlauncher.xml

        startup.sh

        startup.bat

    目录说明：

    config目录：您可以将java中的所有配置文件放在该目录，例如spring 的配置文件等。

    lib目录：您可以将App中所有依赖的Jar包都放在该目录，JLauncher会自动加载所有Jar包。

jlauncher.xml文件：主配置文件，如果没有，则按默认约定目录读取。通过该配置文件，可以个性化配置config、lib目录的位置和个数，以及java虚拟机的个性化运行参数。

适用JDK 1.5、 1.6、 1.7版本开发的应用，参考例子请下载：http://pan.baidu.com/s/1ntjs3cH







