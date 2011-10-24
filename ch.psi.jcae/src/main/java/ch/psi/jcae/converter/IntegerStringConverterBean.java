/**
 * 
 * Copyright 2010 Paul Scherrer Institute. All rights reserved.
 * 
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful,
 * but without any warranty; without even the implied warranty of
 * merchantability or fitness for a particular purpose. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this code. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package ch.psi.jcae.converter;

import ch.psi.jcae.ChannelBean;

/**
 * @author ebner
 *
 */
public class IntegerStringConverterBean extends AbstractConverterBean<Integer,String> {


	/**
	 * @param channelBean
	 */
	public IntegerStringConverterBean(ChannelBean<Integer> channelBean) {
		super(channelBean);
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.AbstractConverterBean#convertForward(java.lang.Object)
	 */
	@Override
	protected String convertForward(Integer value) {
        return(value.toString());
	}

	/* (non-Javadoc)
	 * @see ch.psi.jcae.AbstractConverterBean#convertReverse(java.lang.Object)
	 */
	@Override
	protected Integer convertReverse(String value) {
        return(new Integer(value));
	}

}