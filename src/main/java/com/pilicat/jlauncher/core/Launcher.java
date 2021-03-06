package com.pilicat.jlauncher.core;


import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;

import com.pilicat.jlauncher.core.exception.NoSuchRealmException;
import com.pilicat.jlauncher.core.launcher.Configurator;
import com.pilicat.jlauncher.core.realm.ClassRealm;

/**
 * Command-line invokable application launcher.
 * <p/>
 * <p/>
 * This launcher class assists in the creation of classloaders and <code>ClassRealm</code>s
 * from a configuration file and the launching of the application's <code>main</code>
 * method from the correct class loaded through the correct classloader.
 * </p>
 * <p/>
 * <p/>
 * The path to the configuration file is specified using the <code>jlauncher.conf</code>
 * system property, typically specified using the <code>-D</code> switch to
 * <code>java</code>.
 * </p>
 *
 */
public class Launcher
{
    protected static final String CLASSBUCKETS_CONF = "jlauncher.conf";

    protected static final String UBERJAR_CONF_DIR = "APP-INF/conf/";

    protected ClassLoader systemClassLoader;

    protected String mainClassName;

    protected String mainRealmName;
    
    protected boolean mainLoop;

    protected ClassBucket classBucket;

    private int exitCode = 0;

    public Launcher()
    {
        this.systemClassLoader = Thread.currentThread().getContextClassLoader();
    }

    public void setSystemClassLoader( ClassLoader loader )
    {
        this.systemClassLoader = loader;
    }

    public ClassLoader getSystemClassLoader()
    {
        return this.systemClassLoader;
    }

    public int getExitCode()
    {
        return exitCode;
    }

    public void setAppMain( String mainClassName,
                            String mainRealmName )
    {
        this.mainClassName = mainClassName;

        this.mainRealmName = mainRealmName;
    }

    public String getMainRealmName()
    {
        return this.mainRealmName;
    }

    public String getMainClassName()
    {
        return this.mainClassName;
    }

    


    public boolean isMainLoop() {
		return mainLoop;
	}

	public void setMainLoop(boolean mainLoop) {
		this.mainLoop = mainLoop;
	}

	public ClassBucket getClassBucket() {
		return classBucket;
	}

	public void setClassBucket(ClassBucket classBucket) {
		this.classBucket = classBucket;
	}

	/**
     * Configure from a file.
     *
     * @param is The config input stream.
	 * @throws Exception 
     * @throws MalformedURLException   If the config file contains invalid URLs.
     */
    public void configure( InputStream is )
        throws Exception
    {
        Configurator configurator = new Configurator( this );

        configurator.configure( is );
    }

    /**
     * Retrieve the main entry class.
     *
     * @return The main entry class.
     * @throws ClassNotFoundException If the class cannot be found.
     * @throws NoSuchRealmException   If the specified main entry realm does not exist.
     */
    public Class<?> getMainClass()
        throws ClassNotFoundException, NoSuchRealmException
    {
        return getMainRealm().loadClass( getMainClassName() );
    }

    /**
     * Retrieve the main entry realm.
     *
     * @return The main entry realm.
     * @throws NoSuchRealmException If the specified main entry realm does not exist.
     */
    public ClassRealm getMainRealm()
        throws NoSuchRealmException
    {
        return getClassBucket().getRealm( getMainRealmName() );
    }

    /**
     * Retrieve the enhanced main entry method.
     *
     * @return The enhanced main entry method.
     * @throws ClassNotFoundException If the main entry class cannot be found.
     * @throws NoSuchMethodException  If the main entry method cannot be found.
     * @throws NoSuchRealmException   If the main entry realm cannot be found.
     */
    protected Method getEnhancedMainMethod()
        throws ClassNotFoundException, NoSuchMethodException, NoSuchRealmException
    {
        Class<?> cwClass = getMainRealm().loadClass( ClassBucket.class.getName() );

        Method m = getMainClass().getMethod( "main", new Class[]{String[].class, cwClass} );

        int modifiers = m.getModifiers();

        if ( Modifier.isStatic( modifiers ) && Modifier.isPublic( modifiers ) )
        {
            if ( m.getReturnType() == Integer.TYPE || m.getReturnType() == Void.TYPE )
            {
                return m;
            }
        }

        throw new NoSuchMethodException( "public static void main(String[] args, ClassWorld world)" );
    }

    /**
     * Retrieve the main entry method.
     *
     * @return The main entry method.
     * @throws ClassNotFoundException If the main entry class cannot be found.
     * @throws NoSuchMethodException  If the main entry method cannot be found.
     * @throws NoSuchRealmException   If the main entry realm cannot be found.
     */
    protected Method getMainMethod()
        throws ClassNotFoundException, NoSuchMethodException, NoSuchRealmException
    {
        Method m = getMainClass().getMethod( "main", new Class[]{String[].class} );

        int modifiers = m.getModifiers();

        if ( Modifier.isStatic( modifiers ) && Modifier.isPublic( modifiers ) )
        {
            if ( m.getReturnType() == Integer.TYPE || m.getReturnType() == Void.TYPE )
            {
                return m;
            }
        }

        throw new NoSuchMethodException( "public static void main(String[] args) in " + getMainClass() );
    }

    /**
     * Launch the application.
     *
     * @param args The application args.
     * @throws ClassNotFoundException    If the main entry class cannot be found.
     * @throws IllegalAccessException    If the method cannot be accessed.
     * @throws InvocationTargetException If the target of the invokation is invalid.
     * @throws NoSuchMethodException     If the main entry method cannot be found.
     * @throws NoSuchRealmException      If the main entry realm cannot be found.
     */
    public void launch( String[] args )
        throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
        NoSuchRealmException
    {
        try
        {
            launchEnhanced( args );

            return;
        }
        catch ( NoSuchMethodException e )
        {
            // ignore
        }

        launchStandard( args );
    }

    /**
     * Attempt to launch the application through the enhanced main method.
     * <p/>
     * <p/>
     * This will seek a method with the exact signature of:
     * </p>
     * <p/>
     * <pre>
     *  public static void main(String[] args, ClassWorld world)
     *  </pre>
     *
     * @param args The application args.
     * @throws ClassNotFoundException    If the main entry class cannot be found.
     * @throws IllegalAccessException    If the method cannot be accessed.
     * @throws InvocationTargetException If the target of the invokation is
     *                                   invalid.
     * @throws NoSuchMethodException     If the main entry method cannot be found.
     * @throws NoSuchRealmException      If the main entry realm cannot be found.
     */
    protected void launchEnhanced( String[] args )
        throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
        NoSuchRealmException
    {
        ClassRealm mainRealm = getMainRealm();

        Class<?> mainClass = getMainClass();

        Method mainMethod = getEnhancedMainMethod();

        ClassLoader cl = mainRealm;

        // ----------------------------------------------------------------------
        // This is what the classloader for the main realm looks like when we
        // boot from the command line:
        // ----------------------------------------------------------------------
        // [ AppLauncher$AppClassLoader ] : $CLASSPATH envar
        //           ^
        //           |
        //           |
        // [ AppLauncher$ExtClassLoader ] : ${java.home}/jre/lib/ext/*.jar
        //           ^
        //           |
        //           |
        // [ Strategy ]
        // ----------------------------------------------------------------------

        Thread.currentThread().setContextClassLoader( cl );

        Object ret = mainMethod.invoke( mainClass, new Object[]{args, getClassBucket()} );

        if ( ret instanceof Integer )
        {
            exitCode = ( (Integer) ret ).intValue();
        }

        Thread.currentThread().setContextClassLoader( systemClassLoader );
    }

    /**
     * Attempt to launch the application through the standard main method.
     * <p/>
     * <p/>
     * This will seek a method with the exact signature of:
     * </p>
     * <p/>
     * <pre>
     *  public static void main(String[] args)
     *  </pre>
     *
     * @param args The application args.
     * @throws ClassNotFoundException    If the main entry class cannot be found.
     * @throws IllegalAccessException    If the method cannot be accessed.
     * @throws InvocationTargetException If the target of the invokation is
     *                                   invalid.
     * @throws NoSuchMethodException     If the main entry method cannot be found.
     * @throws NoSuchRealmException      If the main entry realm cannot be found.
     */
    protected void launchStandard( String[] args )
        throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
        NoSuchRealmException
    {
        ClassRealm mainRealm = getMainRealm();

        Class<?> mainClass = getMainClass();

        Method mainMethod = getMainMethod();

        Thread.currentThread().setContextClassLoader( mainRealm );

        Object ret = mainMethod.invoke( mainClass, new Object[]{args} );

        if ( ret instanceof Integer )
        {
            exitCode = ( (Integer) ret ).intValue();
        }

        Thread.currentThread().setContextClassLoader( systemClassLoader );

    }

    // ------------------------------------------------------------
    //     Class methods
    // ------------------------------------------------------------

    /**
     * Launch the launcher from the command line.
     * Will exit using System.exit with an exit code of 0 for success, 100 if there was an unknown exception,
     * or some other code for an application error.
     *
     * @param args The application command-line arguments.
     */
    public static void main( String[] args )
    {
        try
        {
            int exitCode = mainWithExitCode( args );
            
            System.exit( exitCode );
        }
        catch ( Exception e )
        {
            e.printStackTrace();

            System.exit( 100 );
        }
    }

    /**
     * Launch the launcher.
     *
     * @param args The application command-line arguments.
     * @return an integer exit code
     * @throws Exception If an error occurs.
     */
    public static int mainWithExitCode( String[] args )    throws Exception
    {
        String classbucketsConf = System.getProperty( CLASSBUCKETS_CONF );

        InputStream is;

        Launcher launcher = new Launcher();

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        launcher.setSystemClassLoader( cl );

        if ( classbucketsConf != null )
        {
            is = new FileInputStream( classbucketsConf );
        }
        else
        {
            if ( "true".equals( System.getProperty( "classbucket.bootstrapped" ) ) )
            {
                is = cl.getResourceAsStream( UBERJAR_CONF_DIR + CLASSBUCKETS_CONF );
            }
            else
            {
                is = cl.getResourceAsStream( CLASSBUCKETS_CONF );
            }
        }

        if ( is == null )
        {
            throw new Exception( "classbuckets configuration not specified nor found in the classpath" );
        }

        launcher.configure( is );

        is.close();

        try
        {
            launcher.launch( args );
            
            if(launcher.isMainLoop()) {
        		while(true){
        			try {
        				Thread.sleep(100000L);
        			} catch (InterruptedException e) {
        				e.printStackTrace();
        			}
        		}
            }
        }
        catch ( InvocationTargetException e )
        {
            ClassRealm realm = launcher.getClassBucket().getRealm( launcher.getMainRealmName() );

            URL[] constituents = realm.getURLs();

            System.out.println( "---------------------------------------------------" );

            for ( int i = 0; i < constituents.length; i++ )
            {
                System.out.println( "constituent[" + i + "]: " + constituents[i] );
            }

            System.out.println( "---------------------------------------------------" );

            // Decode ITE (if we can)
            Throwable t = e.getTargetException();

            if ( t instanceof Exception )
            {
                throw (Exception) t;
            }
            if ( t instanceof Error )
            {
                throw (Error) t;
            }

            // Else just toss the ITE
            throw e;
        }

        return launcher.getExitCode();
    }
}
