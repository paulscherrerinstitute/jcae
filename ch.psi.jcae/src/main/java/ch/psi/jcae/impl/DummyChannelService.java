/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
 * @author ebner
 * 
 */
public class DummyChannelService implements ChannelService {

	
	private static final Logger logger = Logger.getLogger(DummyChannelService.class.getName());
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.psi.jcae.ChannelService#createChannel(ch.psi.jcae.ChannelDescriptor)
	 */
	@Override
	public <T> Channel<T> createChannel(ChannelDescriptor<T> descriptor) throws ChannelException, InterruptedException, TimeoutException {
		return new DummyChannel<>(descriptor.getType(), descriptor.getName(), descriptor.getSize(), descriptor.getMonitored());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.ChannelService#createChannels(java.util.List)
	 */
	@Override
	public List<Channel<?>> createChannels(List<ChannelDescriptor<?>> descriptors) throws ChannelException, InterruptedException, TimeoutException {
		List<Channel<?>> l = new ArrayList<>();
		for (ChannelDescriptor<?> d : descriptors) {
			l.add(new DummyChannel<>(d.getType(), d.getName(), d.getSize(), d.getMonitored()));
		}
		return l;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.ChannelService#createAnnotatedChannels(java.lang.Object)
	 */
	@Override
	public void createAnnotatedChannels(Object object) throws ChannelException, InterruptedException, TimeoutException {
		createAnnotatedChannels(object, "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.ChannelService#createAnnotatedChannels(java.lang.Object,
	 * java.lang.String)
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
						list.add(new DummyChannel<>(annotation.type(), channelNames.get(ct), 1, annotation.monitor()));
						ct++;
					}
					field.set(object, list);	
				}
				else{
					// Create ChannelBean object
					field.set(object, new DummyChannel<>(annotation.type(), channelNames.get(ct), 1, annotation.monitor()));
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
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.psi.jcae.ChannelService#destroyAnnotatedChannels(java.lang.Object)
	 */
	@Override
	public void destroyAnnotatedChannels(Object object) throws ChannelException {
		try {

			Class<?> c = object.getClass();

			// Execute pre destroy function (if available)
			for (Method m : c.getDeclaredMethods()) {
				CaPreDestroy a = m.getAnnotation(CaPreDestroy.class);

				if (a != null) {
					m.invoke(object);
				}
			}

			for (Field field : c.getDeclaredFields()) {
				CaChannel annotation = field.getAnnotation(CaChannel.class);
				if (annotation != null) {
					boolean accessible = field.isAccessible();
					field.setAccessible(true);
					// Set field/attribute value to null
					field.set(object, null);
					field.setAccessible(accessible);
				}
			}

			// Execute POST destroy function (if available)
			for (Method m : c.getDeclaredMethods()) {
				CaPostDestroy a = m.getAnnotation(CaPostDestroy.class);

				if (a != null) {
					m.invoke(object);
				}
			}

		} catch (IllegalArgumentException e) {
			throw new ChannelException("An error occured while using reflection to set object values", e);
		} catch (IllegalAccessException e) {
			throw new ChannelException("An error occured while using reflection to set object values", e);
		} catch (SecurityException e) {
			throw new ChannelException("An error occured while using reflection to set object values", e);
		} catch (InvocationTargetException e) {
			throw new ChannelException("Cannot execute pre/post destroy function(s)", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.psi.jcae.ChannelService#destroy()
	 */
	@Override
	public void destroy() {
		// Nothing to be done
	}

}
