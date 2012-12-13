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

package ch.psi.jcae.impl;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import ch.psi.jcae.ChannelException;
import ch.psi.jcae.annotation.CaChannel;
import ch.psi.jcae.annotation.CaPostDestroy;
import ch.psi.jcae.annotation.CaPostInit;
import ch.psi.jcae.annotation.CaPreDestroy;
import ch.psi.jcae.annotation.CaPreInit;

/**
 * Factory class for creating ChannelBean objects (more easily).
 * Internally the factory uses the <code>ChannelFactory</code> Singleton to create JCA channel objects.
 * @author ebner
 *
 */
public class ChannelServiceImpl {
	
	private static final Logger logger = Logger.getLogger(ChannelServiceImpl.class.getName());
	private static HashMap<String,ChannelServiceImpl> factories = new HashMap<String,ChannelServiceImpl>();
	private static final String defaultFactoryKey = "default";

	private JCAChannelFactory channelFactory;

	/**
	 * Constructor - Create ChannelBeanFactory object. The constructor will initialize a 
	 * default ChannelFactory factory and read the <code>jca.properties</code> file to 
	 * determine the default timeout for channel put and get methods. If no timeout is set
	 * the default timeout of 10000ms will take effect.
	 * 
	 * @throws CAException
	 */
	private ChannelServiceImpl() throws CAException{
		
		// Create ChannelFactory object
		channelFactory = new JCAChannelFactory();
	}
	
	/**
	 * Get default instance of ChannelBeanFactory
	 * @return		Instance of the ChannelBeanFactory
	 * @throws CAException
	 */
	public static ChannelServiceImpl getFactory() throws CAException{
		if(!factories.containsKey(defaultFactoryKey)){
			factories.put(defaultFactoryKey, new ChannelServiceImpl());
		}
		return(factories.get(defaultFactoryKey));
	}
	
	/**
	 * Get a specific ChannelBeanFactory identified by the given key. If no factory is registered for the 
	 * given key, a new one is created.
	 * ChannelBeans created with factories of different keys will have different
	 * Contexts, ...
	 * 
	 * @param factoryKey
	 * @return		Instance of the ChannelBeanFactory
	 * @throws CAException
	 */
	public static ChannelServiceImpl getFactory(String factoryKey) throws CAException{
		if(!factories.containsKey(factoryKey)){
			factories.put(factoryKey, new ChannelServiceImpl());
		}
		return(factories.get(factoryKey));
	}
	
	/**
	 * Create ChannelBean object of the specified type and the given channel.
	 * 
	 * @param <T>
	 * @param type
	 * @param channel
	 * @param monitor
	 * @return		Typed ChannelBean object
	 * @throws CAException
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	public <T> ChannelImpl<T> createChannelBean(Class<T> type, Channel channel, boolean monitor) throws InterruptedException, TimeoutException, ChannelException, ExecutionException{
		ChannelImpl<T> bean = new ChannelImpl<T>(type, channel, null, monitor);
		return(bean);
	}
	
	
	/**
	 * Create ChannelBean object of the specified type. A new Channel object will be created 
	 * for each new bean (even if the channel theoretically already exists)
	 * The created channel will be automatically destroyed if the ChannelBeans destroy() or finalize() method
	 * is called.
	 * @param <T>
	 * @param type
	 * @param channelName
	 * @param monitor
	 * @return		Typed ChannelBean object
	 * @throws CAException
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	public <T> ChannelImpl<T> createChannelBean(Class<T> type, String channelName, boolean monitor) throws InterruptedException, TimeoutException, ChannelException, CAException, ExecutionException{
		Channel channel = channelFactory.createChannel(channelName);
		
		ChannelImpl<T> bean = new ChannelImpl<T>(type, channel, null, monitor);
		return(bean);
	}
	
	
	
	/**
	 * Create multiple ChannelBean objects of the same type at once. This function comes with an
	 * performance advantage compared to calling <code>createChannelBean</code> for each ChannelBean
	 * because all channels will be created in parallel.
	 * 
	 * @param <T>
	 * @param type
	 * @param channelNames
	 * @param monitor
	 * @return		List of typed ChannelBean objects
	 * @throws CAException
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	public <T> List<ChannelImpl<T>> createChannelBeans(Class<T> type, List<String> channelNames, boolean monitor) throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException{
		List<Channel> channels = channelFactory.createChannels(channelNames);
		
		List<ChannelImpl<T>> beans = new ArrayList<ChannelImpl<T>>();
		for(Channel channel: channels){
			ChannelImpl<T> bean = new ChannelImpl<T>(type, channel, null, monitor);
			beans.add(bean);
		}
		return(beans);
	}
	
	
	/**
	 * Create all ChannelBeans that are annotated of the passed object
	 * Also register all annotated functions as monitors for the specified ChannelBean.
	 *  
	 * @param object			Object to manage
	 * @throws CAException		Something went wrong while bringing the object under management
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	public void createChannelBeans(Object object) throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException{
		createChannelBeans(object, "");
	}
	
	/**
	 * Connect passed bean to all specified epics channels, i.e. create a ChannelBean
	 * for each channel.
	 * Also register all annotated functions as monitors for the specified channel.
	 *  
	 * @param object		Object to manage
	 * @param baseName		String that gets added (before) to the annotated name of the channel (before the name)
	 * @throws CAException	Something went wrong while bringing the object under management
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	public void createChannelBeans(Object object, String baseName) throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException{
		try{
			Class<?> c = object.getClass();
			
			// Execute pre init function (if available)
			for(Method m: c.getDeclaredMethods()){
				CaPreInit a = m.getAnnotation(CaPreInit.class);
				
				if(a != null){
					m.invoke(object);
				}
			}
			
			// Connect ChannelBeans
			List<Object[]> fields = new ArrayList<Object[]>();
			List<String> channelNames = new ArrayList<String>();
			
			
			for(Field field: c.getDeclaredFields()){
				CaChannel annotation = field.getAnnotation(CaChannel.class);
				if(annotation != null){
					if(field.getType().equals(ChannelImpl.class)){
						fields.add(new Object[] {field, annotation});
						channelNames.add(baseName+annotation.name()[0]);
					}
					else if(field.getType().equals(List.class)){
						fields.add(new Object[] {field, annotation});
						for(String n: annotation.name()){
							channelNames.add(baseName+n);
						}
					}
					else{
						logger.warning("Annotation @"+CaChannel.class.getSimpleName()+" not applicable for field '"+field.getName()+"' of type '"+field.getType().getName()+"'");
					}
				}
			}
			
			// Create and set ChannelBean object of the given bean object
			List<Channel> channels = channelFactory.createChannels(channelNames);
			int ct = 0;
			for(Object[] f: fields){
				Field field = (Field) f[0] ;
				boolean accessible = field.isAccessible();
				field.setAccessible(true);
				CaChannel annotation = (CaChannel)f[1];
				if(annotation.name().length>1){
					List<ChannelImpl<?>> list = new ArrayList<ChannelImpl<?>>();
					for(int x=0;x<annotation.name().length;x++){
						// Create ChannelBean object
						list.add(createChannelBean(annotation.type(), channels.get(ct), annotation.monitor()));
						ct++;
					}
					field.set(object, list);	
				}
				else{
					// Create ChannelBean object
					field.set(object, createChannelBean(annotation.type(), channels.get(ct), annotation.monitor()));
					ct++;
				}
				
				field.setAccessible(accessible);
			}
			
			// Execute POST init function (if available)
			for(Method m: c.getDeclaredMethods()){
				CaPostInit a = m.getAnnotation(CaPostInit.class);
				
				if(a != null){
					m.invoke(object);
				}
			}
			
			
		} catch (IllegalArgumentException e) {
			throw new CAException("An error occured while using reflection to set object values",e);
		} catch (IllegalAccessException e) {
			throw new CAException("An error occured while using reflection to set object values",e);
		} catch (SecurityException e) {
			throw new CAException("An error occured while using reflection to set object values",e);
		} catch (InvocationTargetException e) {
			throw new CAException("Cannot execute pre/post init function(s)",e);
		}
	}
	
	/**
	 * Destroy all annotated channel beans in the passed object
	 * @param object
	 * @throws CAException
	 * @throws InterruptedException
	 * @throws ChannelException 
	 */
	public void destroyChannelBeans(Object object) throws CAException, InterruptedException, ChannelException{
		try{
			
			Class<?> c = object.getClass();
			
			// Execute pre destroy function (if available)
			for(Method m: c.getDeclaredMethods()){
				CaPreDestroy a = m.getAnnotation(CaPreDestroy.class);
				
				if(a != null){
					m.invoke(object);
				}
			}
			
			
			
			for(Field field: c.getDeclaredFields()){
				CaChannel annotation = field.getAnnotation(CaChannel.class);
				if(annotation != null){
					if(field.getType().equals(ChannelImpl.class)){
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						((ChannelImpl<?>) field.get(object)).destroy();
						// Set field/attribute value to null
						field.set(object, null);
						field.setAccessible(accessible);
						
					}
					else if(field.getType().equals(List.class)){
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						@SuppressWarnings("unchecked")
						List<ChannelImpl<?>> l = ((List<ChannelImpl<?>>) field.get(object));
						for(ChannelImpl<?> b: l){
							b.destroy();
						}
						// Set field/attribute value to null
						field.set(object, null);
						field.setAccessible(accessible);
					}
					else{
						logger.warning("Annotation @"+CaChannel.class.getSimpleName()+" not applicable for field '"+field.getName()+"' of type '"+field.getType().getName()+"'");
					}
				}
			}
			
			
			// Execute POST destroy function (if available)
			for(Method m: c.getDeclaredMethods()){
				CaPostDestroy a = m.getAnnotation(CaPostDestroy.class);
				
				if(a != null){
					m.invoke(object);
				}
			}
			
			
		} catch (IllegalArgumentException e) {
			throw new CAException("An error occured while using reflection to set object values",e);
		} catch (IllegalAccessException e) {
			throw new CAException("An error occured while using reflection to set object values",e);
		} catch (SecurityException e) {
			throw new CAException("An error occured while using reflection to set object values",e);
		} catch (InvocationTargetException e) {
			throw new CAException("Cannot execute pre/post destroy function(s)",e);
		}
	}
	
	
	
	/**
	 * Set ChannelFactory to use for creating new ChannelBean objects.
	 * @param channelFactory
	 */
	public void setChannelFactory(JCAChannelFactory channelFactory){
		this.channelFactory = channelFactory;
	}

	/**
	 * Get current ChannelFactory used to create new ChannelBean objects.
	 * @return		Get used instance of the ChannelFactory
	 */
	public JCAChannelFactory getChannelFactory() {
		return channelFactory;
	}
}
