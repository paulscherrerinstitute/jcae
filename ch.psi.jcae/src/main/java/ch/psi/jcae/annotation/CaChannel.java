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
public @interface CaChannel {
	/**
	 * Data type of ChannelBean value
	 */
	Class<?> type();
	/**
	 * Name of channel
	 */
	String[] name();
	/**
	 * Flag whether to monitor the channel or not (default: false)
	 * If using monitor=true the ChannelBean type must match the type of the Channel Access channel. 
	 * i.e. you must not set a ChannelBean&lt;Double> on monitor when it is bound to a Channel Access channel
	 * of type String.
	 */
	boolean monitor() default false;
}
