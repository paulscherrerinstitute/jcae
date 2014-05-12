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


package ch.psi.jcae.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to annotate a ChannelBean
 * @author ebner
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CaCompositeChannel {
	/**
	 * Data type of ChannelBean value
	 * @return Type of channel
	 */
	Class<?> type();
	
	/**
	 * Name of channel
	 * @return Name of channel
	 */
	String name();
	
	/**
	 * Name of the readback channel
	 * @return Name of readback channel
	 */
	String readback();

	/**
	 * Flag whether to monitor the channel or not (default: false)
	 * If using monitor=true the ChannelBean type must match the type of the Channel Access channel. 
	 * i.e. you must not set a Channel&lt;Double&gt; on monitor when it is bound to a Channel Access channel
	 * of type String.
	 * @return Monitor channel
	 */
	boolean monitor() default false;
	
	/**
	 * Size of the array channel. 0 take default size of the channel
	 * @return Size of channel
	 */
	int size() default 0;
}
