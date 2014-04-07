package com.pilicat.jlauncher.core.exception;

import com.pilicat.jlauncher.core.ClassBucket;


public class DuplicateRealmException extends ClassBucketException
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
     * @param world The world.
     * @param id    The realm id.
     */
    public DuplicateRealmException( ClassBucket classBucket, String id )
    {
        super( classBucket, id );
        this.id = id;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the duplicate realm id.
     *
     * @return The id.
     */
    public String getId()
    {
        return this.id;
    }
}
