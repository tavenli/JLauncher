package com.pilicat.jlauncher.core.strategy;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;

import com.pilicat.jlauncher.core.realm.ClassRealm;
import com.pilicat.jlauncher.core.utils.UrlUtils;


public abstract class AbstractStrategy implements Strategy    
{

    protected ClassRealm realm;

    public AbstractStrategy( ClassRealm realm )
    {
        this.realm = realm;
    }

    protected String getNormalizedResource( String name )
    {
        return UrlUtils.normalizeUrlPath( name );
    }

    protected Enumeration<URL> combineResources( Enumeration<URL> en1, Enumeration<URL> en2, Enumeration<URL> en3 )
    {
        Collection<URL> urls = new LinkedHashSet<URL>();

        addAll( urls, en1 );
        addAll( urls, en2 );
        addAll( urls, en3 );

        return Collections.enumeration( urls );
    }

    private void addAll( Collection<URL> target, Enumeration<URL> en )
    {
        if ( en != null )
        {
            while ( en.hasMoreElements() )
            {
                target.add( en.nextElement() );
            }
        }
    }

	public ClassRealm getRealm() {
		return realm;
	}



}
