package ch.psi.jcae.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to annotate channels
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CaChannel {
	/**
	 * Data type of Channel value
	 * @return Data type of the channel
	 */
	Class<?> type();
	/**
	 * Name of channel
	 * @return Name of the channel
	 */
	String[] name();
	/**
	 * Flag whether to monitor the channel or not (default: false)
	 * If using monitor=true the ChannelBean type must match the type of the Channel Access channel. 
	 * i.e. you must not set a ChannelBean&lt;Double&gt; on monitor when it is bound to a Channel Access channel
	 * of type String.
	 * @return Monitor yes/no
	 */
	boolean monitor() default false;
	
	/**
	 * Size of the array channel. 0 take default size of the channel
	 * @return If its an array data type the size of the array
	 */
	int size() default 0;
}
