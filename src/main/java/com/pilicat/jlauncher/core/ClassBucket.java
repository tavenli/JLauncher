package com.pilicat.jlauncher.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.pilicat.jlauncher.core.exception.DuplicateRealmException;
import com.pilicat.jlauncher.core.exception.NoSuchRealmException;
import com.pilicat.jlauncher.core.listener.ClassBucketListener;
import com.pilicat.jlauncher.core.realm.ClassRealm;


public class ClassBucket
{
    private Map<String, ClassRealm> realms;

    private final List<ClassBucketListener> listeners = new ArrayList<ClassBucketListener>();

    public ClassBucket( String realmId, ClassLoader classLoader )
    {
        this();

        try
        {
            newRealm( realmId, classLoader );
        }
        catch ( DuplicateRealmException e )
        {
            // Will never happen as we are just creating the world.
        }
    }

    public ClassBucket()
    {
        this.realms = new LinkedHashMap<String, ClassRealm>();
    }

    public ClassRealm newRealm( String id )
        throws DuplicateRealmException
    {
        return newRealm( id, getClass().getClassLoader() );
    }

    public synchronized ClassRealm newRealm( String id, ClassLoader classLoader )
        throws DuplicateRealmException
    {
        if ( realms.containsKey( id ) )
        {
            throw new DuplicateRealmException( this, id );
        }

        ClassRealm realm;

        realm = new ClassRealm( this, id, classLoader );

        realms.put( id, realm );

        for ( ClassBucketListener listener : listeners )
        {
            listener.realmCreated( realm );
        }

        return realm;
    }

    public synchronized void disposeRealm( String id )
        throws NoSuchRealmException
    {
        ClassRealm realm = (ClassRealm) realms.remove( id );

        if ( realm != null )
        {
            closeIfJava7( realm );
            for ( ClassBucketListener listener : listeners )
            {
                listener.realmDisposed( realm );
            }
        }
    }

    private void closeIfJava7( ClassRealm realm )
    {
        try
        {
            //noinspection ConstantConditions
            if ( realm instanceof Closeable )
            {
                //noinspection RedundantCast
                ( (Closeable) realm ).close();
            }
        }
        catch ( IOException ignore )
        {
        }
    }

    public synchronized ClassRealm getRealm( String id )
        throws NoSuchRealmException
    {
        if ( realms.containsKey( id ) )
        {
            return (ClassRealm) realms.get( id );
        }

        throw new NoSuchRealmException( this, id );
    }

    public synchronized Collection<ClassRealm> getRealms()
    {
        return Collections.unmodifiableList( new ArrayList<ClassRealm>( realms.values() ) );
    }

    // from exports branch
    public synchronized ClassRealm getClassRealm( String id )
    {
        if ( realms.containsKey( id ) )
        {
            return realms.get( id );
        }

        return null;
    }

    public synchronized void addListener( ClassBucketListener listener )
    {
        // TODO ideally, use object identity, not equals
        if ( !listeners.contains( listener ) )
        {
            listeners.add( listener );
        }
    }

    public synchronized void removeListener( ClassBucketListener listener )
    {
        listeners.remove( listener );
    }
}