package sg.com.jp.generalcargo.util;

//Decompiled by DJ v3.2.2.67 Copyright 2002 Atanas Neshkov  Date: 10/24/2011 2:54:24 PM
//Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
//Decompiler options: packimports(3) 
//Source File Name:   CreateException.java

import java.rmi.RemoteException;

public final class CreateException extends RemoteException {

	public CreateException(String msg) {
		super(msg);
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	private Object key;
}