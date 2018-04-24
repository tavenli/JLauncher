#!/bin/sh

if test -z $JAVA_HOME; then

    echo '请先设置JAVA_HOME环境变量'
	
else

    basepath=`dirname "$0"`
    cd $basepath
    APP_HOME=`pwd`
	export APP_HOME
	APP_NAME="AppDemo"
	JVM_OPTIONS="-server -Xms256m -Xmx1024m"
	
	DEFAULT_OPTS="$DEFAULT_OPTS -Dapp.home=$APP_HOME -Dapp.name=$APP_NAME "
	DEFAULT_OPTS="$DEFAULT_OPTS -Djlauncher.conf=$APP_HOME/jlauncher.xml"
	
	CLASSPATH="$APP_HOME/common/jlauncher-1.0.0-SNAPSHOT.jar"
	MAIN_CLASS="com.pilicat.jlauncher.core.Launcher"
	echo $APP_HOME
	echo $CLASSPATH
	nohup "$JAVA_HOME"/bin/java $JVM_OPTIONS $DEFAULT_OPTS -classpath "$CLASSPATH" $MAIN_CLASS > console.log 2>&1 &
	echo $! > app.pid
fi


