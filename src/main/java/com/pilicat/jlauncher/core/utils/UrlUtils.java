package com.pilicat.jlauncher.core.utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;


public class UrlUtils
{
    public static String normalizeUrlPath( String name )
    {
        if ( name.startsWith( "/" ) )
        {
            name = name.substring( 1 );
        }

        int i = name.indexOf( "/.." );

        if ( i > 0 )
        {
            int j = name.lastIndexOf( "/", i - 1 );

            if ( j >= 0 )
            {
                name = name.substring( 0, j ) + name.substring( i + 3 );
            }
        }

        return name;
    }

    public static Set<URL> getURLs( URLClassLoader loader )
    {
        Set<URL> ret = new HashSet<URL>();

        for ( URL url : loader.getURLs() )
        {
            ret.add( url );
        }

        return ret;
    }
}
