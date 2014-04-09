package com.pilicat.jlauncher.core.launcher;


import java.io.InputStream;
import java.util.Properties;

/**
 * Event based launcher configuration parser, delegating effective configuration handling to ConfigurationHandler.
 *
 */
public abstract class ConfigurationParser
{

    protected ConfigurationHandler handler;

    protected Properties systemProperties;

    public ConfigurationParser( ConfigurationHandler handler, Properties systemProperties )
    {
        this.handler = handler;
        this.systemProperties = systemProperties;
    }

    /**
     * Parse launcher configuration file and send events to the handler.
     */
    public abstract void parse( InputStream is )  throws Exception;
    
}
