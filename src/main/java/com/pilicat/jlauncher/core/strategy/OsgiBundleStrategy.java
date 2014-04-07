package com.pilicat.jlauncher.core.strategy;


import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import com.pilicat.jlauncher.core.realm.ClassRealm;

public class OsgiBundleStrategy extends AbstractStrategy
{

    public OsgiBundleStrategy( ClassRealm realm )
    {
        super( realm );
    }

    public Class<?> loadClass( String name )
        throws ClassNotFoundException
    {
        Class<?> clazz = realm.loadClassFromImport( name );

        if ( clazz == null )
        {
            clazz = realm.loadClassFromSelf( name );

            if ( clazz == null )
            {
                clazz = realm.loadClassFromParent( name );

                if ( clazz == null )
                {
                    throw new ClassNotFoundException( name );
                }
            }
        }

        return clazz;
    }

    public URL getResource( String name )
    {
        URL resource = realm.loadResourceFromImport( name );

        if ( resource == null )
        {
            resource = realm.loadResourceFromSelf( name );

            if ( resource == null )
            {
                resource = realm.loadResourceFromParent( name );
            }
        }

        return resource;
    }

    public Enumeration<URL> getResources( String name ) throws IOException
    {
        Enumeration<URL> imports = realm.loadResourcesFromImport( name );
        Enumeration<URL> self = realm.loadResourcesFromSelf( name );
        Enumeration<URL> parent = realm.loadResourcesFromParent( name );

        return combineResources( imports, self, parent );
    }

}
