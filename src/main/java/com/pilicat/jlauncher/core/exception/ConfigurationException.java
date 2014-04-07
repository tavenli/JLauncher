package com.pilicat.jlauncher.core.exception;


public class ConfigurationException extends Exception
{
    /**
     * Construct.
     *
     * @param msg The message.
     */
    public ConfigurationException( String msg )
    {
        super( msg );
    }

    /**
     * Construct.
     *
     * @param msg    The message.
     * @param lineNo The number of configuraton line where the problem occured.
     * @param line   The configuration line where the problem occured.
     */
    public ConfigurationException( String msg, int lineNo, String line )
    {
        super( msg + " (" + lineNo + "): " + line );
    }

    protected ConfigurationException( Exception cause )
    {
        super( cause );
    }
}
