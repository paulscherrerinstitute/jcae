/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl.handler;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.event.PutListener;

/**
 * @author ebner
 *
 */
public class BooleanArrayHandler implements Handler<boolean[]>{

	/* (non-Javadoc)
	 * @see ch.psi.jcae.impl.handler.Handler#setValue(gov.aps.jca.Channel, java.lang.Object, gov.aps.jca.event.PutListener)
	 */
	@Override
	public void setValue(Channel channel, Object value, PutListener listener) throws CAException {
		boolean[] values = (boolean[]) value;
		int[] v = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			v[i] = values[i] ? 1 : 0;
		}
		channel.put(((int[]) v), listener);
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.impl.handler.Handler#getValue(gov.aps.jca.dbr.DBR)
	 */
	@Override
	public boolean[] getValue(DBR dbr) throws CAStatusException {
		int[] v = ((DBR_Int) dbr.convert(DBR_Int.TYPE)).getIntValue();
		boolean[] b = new boolean[v.length];
		for(int i=0;i<v.length;i++){
			b[i] = (v[i]>0);
		}
		return b;
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.impl.handler.Handler#getDBRType()
	 */
	@Override
	public DBRType getDBRType() {
		return DBR_Int.TYPE;
	}

}
