package com.pilicat.jlauncher.core.exception;

import com.pilicat.jlauncher.core.ClassBucket;

public class ClassBucketException extends Exception{

    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /**
     * The classBucket.
     */
    private ClassBucket classBucket;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     *
     * @param classBucket The classBucket.
     */
    public ClassBucketException( final ClassBucket classBucket )
    {
        this.classBucket = classBucket;
    }

    /**
     * Construct.
     *
     * @param classBucket The classBucket.
     * @param msg   The detail message.
     */
    public ClassBucketException( final ClassBucket classBucket, final String msg )
    {
        super( msg );
        this.classBucket = classBucket;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the classBucket.
     *
     * @return The classBucket.
     */
    public ClassBucket getClassBucket()
    {
        return this.classBucket;
    }


    
}
