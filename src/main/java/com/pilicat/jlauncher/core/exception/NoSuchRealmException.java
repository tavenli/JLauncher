package com.pilicat.jlauncher.core.exception;

import com.pilicat.jlauncher.core.ClassBucket;


public class NoSuchRealmException extends ClassBucketException
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /**
     * The realm id.
     */
    private String id;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     *
     * @param classBucket The classBucket.
     * @param id    The realm id.
     */
    public NoSuchRealmException( ClassBucket classBucket, String id )
    {
        super( classBucket, id );
        this.id = id;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the invalid realm id.
     *
     * @return The id.
     */
    public String getId()
    {
        return this.id;
    }
}
