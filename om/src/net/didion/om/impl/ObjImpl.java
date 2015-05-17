package net.didion.om.impl;

import net.didion.om.api.Obj;
import net.didion.om.GUID;

public class ObjImpl implements Obj {
	private GUID _id;

	public ObjImpl(GUID id) {
		_id = id;
	}

	public GUID getId() {
		return _id;
	}
}
