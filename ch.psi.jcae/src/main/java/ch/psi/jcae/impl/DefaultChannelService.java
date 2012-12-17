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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelDescriptor;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.ChannelService;
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
public class DefaultChannelService implements ChannelService {
	
	private static final Logger logger = Logger.getLogger(DefaultChannelService.class.getName());

	private final JCAChannelFactory channelFactory;

	/**
	 * Constructor - Create ChannelBeanFactory object. The constructor will initialize a 
	 * default ChannelFactory factory and read the <code>jca.properties</code> file to 
	 * determine the default timeout for channel put and get methods. If no timeout is set
	 * the default timeout of 10000ms will take effect.
	 * 
	 * @throws CAException
	 */
	public DefaultChannelService(){
		try{
			channelFactory = new JCAChannelFactory();
		}
		catch(CAException e){
			throw new RuntimeException("Unable to initialize internal channel factory",e);
		}
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
	@Override
	public <T> Channel<T> createChannel(ChannelDescriptor<T> descriptor) throws ChannelException, InterruptedException, TimeoutException {
		try{
			gov.aps.jca.Channel channel = channelFactory.createChannel(descriptor.getName());
			DefaultChannel<T> bean = new DefaultChannel<T>(descriptor.getType(), channel, null, descriptor.getMonitored());
			return(bean);
		}
		catch(CAException | ExecutionException e){
			throw new ChannelException("Unable to create channel "+descriptor.getName(),e);
		}
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
	@Override
	public List<Channel<?>> createChannels(List<ChannelDescriptor<?>> list) throws ChannelException, InterruptedException, TimeoutException {
		try{
		List<String> names = new ArrayList<String>();
		for(ChannelDescriptor<?> d: list){
			names.add(d.getName());
		}
		List<gov.aps.jca.Channel> channels = channelFactory.createChannels(names);
		
		List<Channel<?>> beans = new ArrayList<Channel<?>>();
		for(int i=0;i<list.size();i++){
			ChannelDescriptor<?> d = list.get(i);
			beans.add(new DefaultChannel<>(d.getType(), channels.get(i), null, d.getMonitored()));
		}
		return(beans);
		}
		catch(CAException | ExecutionException e){
			throw new ChannelException("", e);
		}
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
	@Override
	public void createAnnotatedChannels(Object object) throws ChannelException, InterruptedException, TimeoutException {
		createAnnotatedChannels(object, "");
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
	@Override
	public void createAnnotatedChannels(Object object, String baseName) throws ChannelException, InterruptedException, TimeoutException {
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
					if(field.getType().equals(DefaultChannel.class)){
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
			List<gov.aps.jca.Channel> channels = channelFactory.createChannels(channelNames);
			int ct = 0;
			for(Object[] f: fields){
				Field field = (Field) f[0] ;
				boolean accessible = field.isAccessible();
				field.setAccessible(true);
				CaChannel annotation = (CaChannel)f[1];
				if(annotation.name().length>1){
					List<Channel<?>> list = new ArrayList<Channel<?>>();
					for(int x=0;x<annotation.name().length;x++){
						// Create ChannelBean object
						list.add(new DefaultChannel<>(annotation.type(), channels.get(ct), null, annotation.monitor()));
						ct++;
					}
					field.set(object, list);	
				}
				else{
					// Create ChannelBean object
					field.set(object, new DefaultChannel<>(annotation.type(), channels.get(ct), null, annotation.monitor()));
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
			throw new ChannelException("An error occured while using reflection to set object values",e);
		} catch (IllegalAccessException e) {
			throw new ChannelException("An error occured while using reflection to set object values",e);
		} catch (SecurityException e) {
			throw new ChannelException("An error occured while using reflection to set object values",e);
		} catch (InvocationTargetException e) {
			throw new ChannelException("Cannot execute pre/post init function(s)",e);
		} catch (CAException | ExecutionException e) {
			throw new ChannelException("Unable to create channels", e);
		}
	}
	
	/**
	 * Destroy all annotated channel beans in the passed object
	 * @param object
	 * @throws CAException
	 * @throws InterruptedException
	 * @throws ChannelException 
	 */
	@Override
	public void destroyAnnotatedChannels(Object object) throws ChannelException {
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
					if(field.getType().equals(DefaultChannel.class)){
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						((DefaultChannel<?>) field.get(object)).destroy();
						// Set field/attribute value to null
						field.set(object, null);
						field.setAccessible(accessible);
						
					}
					else if(field.getType().equals(List.class)){
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						@SuppressWarnings("unchecked")
						List<DefaultChannel<?>> l = ((List<DefaultChannel<?>>) field.get(object));
						for(DefaultChannel<?> b: l){
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
			throw new ChannelException("An error occured while using reflection to set object values",e);
		} catch (IllegalAccessException e) {
			throw new ChannelException("An error occured while using reflection to set object values",e);
		} catch (SecurityException e) {
			throw new ChannelException("An error occured while using reflection to set object values",e);
		} catch (InvocationTargetException e) {
			throw new ChannelException("Cannot execute pre/post destroy function(s)",e);
		}
	}
	
	/**
	 * Destroy this service and free all hold resources ...
	 */
	@Override
	public void destroy(){
		try{
			channelFactory.destroyContext();
		}
		catch(CAException e){
			throw new RuntimeException("Unable to destroy the internal channel factory instance", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		destroy();
	}
}
