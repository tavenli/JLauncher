package com.pilicat.jlauncher.core.strategy;


import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import com.pilicat.jlauncher.core.realm.ClassRealm;


public interface Strategy
{

    Class<?> loadClass( String name ) throws ClassNotFoundException;

    URL getResource( String name );

    Enumeration<URL> getResources( String name ) throws IOException;

    ClassRealm getRealm();

}
