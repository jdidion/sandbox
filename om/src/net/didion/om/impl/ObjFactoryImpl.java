package net.didion.om.impl;

import net.didion.om.api.Obj;
import net.didion.om.api.ObjFactory;
import net.didion.om.impl.ObjImpl;
import net.didion.om.GUID;

public class ObjFactoryImpl implements ObjFactory {
	public Obj createObj() {
		return new ObjImpl(GUID.nextGUID());
	}
}