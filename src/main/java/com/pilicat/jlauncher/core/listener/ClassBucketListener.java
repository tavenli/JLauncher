package com.pilicat.jlauncher.core.listener;

import com.pilicat.jlauncher.core.realm.ClassRealm;

public interface ClassBucketListener {
	
    public void realmCreated( ClassRealm realm );

    public void realmDisposed( ClassRealm realm );

    
}
