package com.pilicat.jlauncher.core.launcher;


import java.io.File;
import java.net.URL;

import com.pilicat.jlauncher.core.exception.DuplicateRealmException;
import com.pilicat.jlauncher.core.exception.NoSuchRealmException;

/**
 * Receive notification of the logical content of launcher configuration, independently from parsing.
 *
 */
public interface ConfigurationHandler
{

    /**
     * Define the main class name
     * @param mainClassName the main class name
     * @param mainRealmName the main realm from which the main class is loaded
     */
    void setAppMain( String mainClassName, String mainRealmName );

    /**
     * Define a new realm
     * @param realmName the new realm name
     * @throws DuplicateRealmException
     */
    void addRealm( String realmName )
        throws DuplicateRealmException;

    /**
     * Add an import specification from a realm
     * @param relamName the realm name
     * @param importSpec the import specification
     * @throws NoSuchRealmException
     */
    void addImportFrom( String relamName, String importSpec )
        throws NoSuchRealmException;

    /**
     * Add a file to the realm
     * @param file the file to load content from
     */
    void addLoadFile( File file );

    /**
     * Add an URL to the realm
     * @param url the url to load content from
     */
    void addLoadURL( URL url );

}
