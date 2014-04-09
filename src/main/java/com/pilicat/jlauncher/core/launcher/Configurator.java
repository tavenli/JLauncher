package com.pilicat.jlauncher.core.launcher;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pilicat.jlauncher.core.ClassBucket;
import com.pilicat.jlauncher.core.Launcher;
import com.pilicat.jlauncher.core.exception.ConfigurationException;
import com.pilicat.jlauncher.core.exception.DuplicateRealmException;
import com.pilicat.jlauncher.core.exception.NoSuchRealmException;
import com.pilicat.jlauncher.core.realm.ClassRealm;

/**
 * <code>Launcher</code> configurator.
 *
 */
public class Configurator implements ConfigurationHandler
{
    /**
     * The launcher to configure.
     */
    private Launcher launcher;

    private ClassBucket classBucket;

    /**
     * Processed Realms.
     */
    private Map<String, ClassRealm> configuredRealms;

    /**
     * Current Realm.
     */
    private ClassRealm curRealm;

    private ClassLoader foreignClassLoader = null;
    
    /**
     * Construct.
     *
     * @param launcher The launcher to configure.
     */
    public Configurator( Launcher launcher )
    {
        this.launcher = launcher;

        configuredRealms = new HashMap<String, ClassRealm>();

        if ( launcher != null )
        {
            this.foreignClassLoader = launcher.getSystemClassLoader();
        }
    }

    /**
     * Construct.
     *
     * @param world The classWorld to configure.
     */
    public Configurator( ClassBucket classBucket )
    {
        setClassBucket( classBucket );
    }

    /**
     * set classBucket.
     * this setter is provided so you can use the same configurator to configure several "classBuckets"
     *
     * @param classBucket The classBucket to configure.
     */
    public void setClassBucket( ClassBucket classBucket )
    {
        this.classBucket = classBucket;

        configuredRealms = new HashMap<String, ClassRealm>();
    }

    /**
     * Configure from a file.
     *
     * @param is The config input stream
     * @throws Exception 
     * @throws MalformedURLException   If the config file contains invalid URLs.
     */
    public void configure( InputStream is )
        throws Exception
    {
        if ( classBucket == null )
        {
        	classBucket = new ClassBucket();
        }

        curRealm = null;

        foreignClassLoader = null;

        if ( this.launcher != null )
        {
            foreignClassLoader = this.launcher.getSystemClassLoader();
        }
        
        //文本文件配置方式
        //ConfigurationParser parser = new TextConfigParser( this, System.getProperties() );
        //xml文件配置方式
        ConfigurationParser parser = new XmlConfigParser( this, System.getProperties() );

        parser.parse( is );

        // Associate child realms to their parents.
        associateRealms();

        if ( this.launcher != null )
        {
            this.launcher.setClassBucket( classBucket );
        }

    }

    // TODO return this to protected when the legacy wrappers can be removed.
    /**
     * Associate parent realms with their children.
     */
    public void associateRealms()
    {
        List<String> sortRealmNames = new ArrayList<String>( configuredRealms.keySet() );

        // sort by name
        Comparator<String> comparator = new Comparator<String>()
        {
            public int compare( String g1,
                                String g2 )
            {
                return g1.compareTo( g2 );
            }
        };

        Collections.sort( sortRealmNames, comparator );

        // So now we have something like the following for defined
        // realms:
        //
        // root
        // root.maven
        // root.maven.plugin
        //
        // Now if the name of a realm is a superset of an existing realm
        // the we want to make child/parent associations.

        for ( String realmName : sortRealmNames )
        {
            int j = realmName.lastIndexOf( '.' );

            if ( j > 0 )
            {
                String parentRealmName = realmName.substring( 0, j );

                ClassRealm parentRealm = configuredRealms.get( parentRealmName );

                if ( parentRealm != null )
                {
                    ClassRealm realm = configuredRealms.get( realmName );

                    realm.setParentRealm( parentRealm );
                }
            }
        }
    }

    public void addImportFrom( String relamName, String importSpec )
        throws NoSuchRealmException
    {
        curRealm.importFrom( relamName, importSpec );
    }

    public void addLoadFile( File file )
    {
        try
        {
            curRealm.addURL( file.toURI().toURL() );
        }
        catch ( MalformedURLException e )
        {
            // can't really happen... or can it?
        }
    }

    public void addLoadURL( URL url )
    {
        curRealm.addURL( url );
    }

    public void addRealm( String realmName )
        throws DuplicateRealmException
    {
        curRealm = classBucket.newRealm( realmName, foreignClassLoader );

        // Stash the configured realm for subsequent association processing.
        configuredRealms.put( realmName, curRealm );
    }

    public void setAppMain( String mainClassName, String mainRealmName )
    {
        if ( this.launcher != null )
        {
            this.launcher.setAppMain( mainClassName, mainRealmName );
        }
    }
}
