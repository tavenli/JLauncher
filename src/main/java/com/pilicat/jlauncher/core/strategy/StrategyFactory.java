package com.pilicat.jlauncher.core.strategy;


import com.pilicat.jlauncher.core.realm.ClassRealm;


public class StrategyFactory
{

    public static Strategy getStrategy( ClassRealm realm )
    {
        return getStrategy( realm, "default" );
    }

    public static Strategy getStrategy( ClassRealm realm, String hint )
    {
        return new SelfFirstStrategy( realm );
    }

}
