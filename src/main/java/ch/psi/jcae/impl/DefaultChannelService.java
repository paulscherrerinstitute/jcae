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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelDescriptor;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.ChannelService;
import ch.psi.jcae.CompositeChannelDescriptor;
import ch.psi.jcae.Descriptor;
import ch.psi.jcae.DummyChannelDescriptor;
import ch.psi.jcae.annotation.CaChannel;
import ch.psi.jcae.annotation.CaCompositeChannel;
import ch.psi.jcae.annotation.CaPostDestroy;
import ch.psi.jcae.annotation.CaPostInit;
import ch.psi.jcae.annotation.CaPreDestroy;
import ch.psi.jcae.annotation.CaPreInit;

/**
 * Factory class for creating ChannelBean objects (more easily).
 * Internally the factory uses the <code>ChannelFactory</code> Singleton to create JCA channel objects.
 */
public class DefaultChannelService implements ChannelService {
	
	private static final Logger logger = Logger.getLogger(DefaultChannelService.class.getName());

	private final JCAChannelFactory channelFactory;
	
	private boolean dryrun;
	private Map<String,String> globalMacros = new HashMap<String,String>();
	
	
	public DefaultChannelService(){
		this(false);
	}
	
	/**
	 * Constructor - Create ChannelBeanFactory object. The constructor will initialize a 
	 * default ChannelFactory factory and read the <code>jca.properties</code> file to 
	 * determine the default timeout for channel put and get methods. If no timeout is set
	 * the default timeout of 10000ms will take effect.
	 * 
	 * @param dryrun Flag to indicate whether using service in dryrun mode
	 */
	public DefaultChannelService(boolean dryrun){
		this.dryrun = dryrun;
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
	 * @param descriptor	Channel descriptor
	 * @return				Channel
	 * 
	 * @throws InterruptedException 	Interrupted 
	 * @throws ChannelException 		Could not create channel
	 * @throws TimeoutException 		Timeout occurred
	 */
	@Override
	public <T> Channel<T> createChannel(Descriptor<T> descriptor) throws ChannelException, InterruptedException, TimeoutException {

		Channel<T> ca;
		if (descriptor instanceof ChannelDescriptor) {
			ChannelDescriptor<T> d = (ChannelDescriptor<T>) descriptor;
			try {
				gov.aps.jca.Channel channel = channelFactory.createChannel(d.getName());
				ca = new DefaultChannel<T>(d.getType(), channel, d.getSize(), d.getMonitored());
			} catch (CAException e){
				throw new ChannelException("Unable to create channel " + d.getName(), e);
			} catch( ExecutionException e){
				throw new ChannelException("Unable to create channel " + d.getName(), e);
			}
		} else if(descriptor instanceof CompositeChannelDescriptor){
			CompositeChannelDescriptor<T> d = (CompositeChannelDescriptor<T>) descriptor;
			try{
				List<String> names = new ArrayList<String>();
				names.add(d.getName());
				names.add(d.getReadback());
				
				List<gov.aps.jca.Channel> channels = channelFactory.createChannels(names);
				
				ca = new CompositeChannel<T>(new DefaultChannel<T>(d.getType(), channels.get(0), d.getSize(), false), new DefaultChannel<T>(d.getType(), channels.get(1), d.getSize(), d.getMonitored()));
			} catch (CAException e){
				throw new ChannelException("Unable to create channel " + d.getName(), e);
			} catch( ExecutionException e){
				throw new ChannelException("Unable to create channel " + d.getName(), e);
			}
		} else if (descriptor instanceof DummyChannelDescriptor) {
			DummyChannelDescriptor<T> d = (DummyChannelDescriptor<T>) descriptor;
			ca = new DummyChannel<T>(d.getType(), d.getName(), d.getSize(), d.getMonitored());
		} else {
			throw new IllegalArgumentException("Descriptor of type " + descriptor.getClass().getName() + " is not supported");
		}
		return (ca);

	}
	
	
	
	/**
	 * Create multiple ChannelBean objects of the same type at once. This function comes with an
	 * performance advantage compared to calling <code>createChannelBean</code> for each ChannelBean
	 * because all channels will be created in parallel.
	 * 
	 * @param list	List of channel descriptors

	 * @return		List of channels

	 * @throws InterruptedException 	Interrupted
	 * @throws ChannelException 		Could not create channel
	 * @throws TimeoutException 		Timeout occurred
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Channel<?>> createChannels(List<Descriptor<?>> list) throws ChannelException, InterruptedException, TimeoutException {
		List<Channel<?>> channelObject = new ArrayList<Channel<?>>();
		
		try{
			List<String> names = new ArrayList<String>();
			for(Descriptor<?> d: list){
				if(d instanceof ChannelDescriptor){
					ChannelDescriptor<?> dd = (ChannelDescriptor<?>)d;
					names.add(dd.getName());
				}
				else if(d instanceof CompositeChannelDescriptor){
					CompositeChannelDescriptor<?> dd = (CompositeChannelDescriptor<?>)d;
					names.add(dd.getName());
					names.add(dd.getReadback());
				}
			}
			List<gov.aps.jca.Channel> channels = channelFactory.createChannels(names);
			
			int ccount = 0;
			for(Descriptor<?> d: list){
				if(d instanceof ChannelDescriptor){
					channelObject.add(new DefaultChannel(d.getType(), channels.get(ccount), d.getSize(), d.getMonitored()));
					ccount++;
				}
				else if(d instanceof CompositeChannelDescriptor){
					
					DefaultChannel<?> c = new DefaultChannel(d.getType(), channels.get(ccount), d.getSize(), false);
					ccount++;
					DefaultChannel<?> cr = new DefaultChannel(d.getType(), channels.get(ccount), d.getSize(), d.getMonitored());
					
					channelObject.add(new CompositeChannel(c, cr));
					ccount++;
				}
				else if(d instanceof DummyChannelDescriptor){
					DummyChannelDescriptor<?> dd = (DummyChannelDescriptor<?>) d;
					channelObject.add(new DummyChannel(dd.getType(), dd.getName(), dd.getSize(), dd.getMonitored()));
				}
			}
		} catch (CAException e){
			throw new ChannelException("", e);
		}
		catch(ExecutionException e) {
			throw new ChannelException("", e);
		}
		
		return channelObject;
	}
	
	@Override
	public void createAnnotatedChannels(Object object) throws ChannelException, InterruptedException, TimeoutException {
		createAnnotatedChannels(object, new HashMap<String,String>(), this.dryrun);
	}
	
	@Override
	public void createAnnotatedChannels(Object object, Map<String,String> macros) throws ChannelException, InterruptedException, TimeoutException {
		createAnnotatedChannels(object,macros,this.dryrun);
	}
	
	/**
	 * Create all ChannelBeans that are annotated of the passed object
	 * Also register all annotated functions as monitors for the specified ChannelBean.
	 *  
	 * @param object		Annotated object
	 * @param dryrun		Flag to indicate whether dummy channels should be created
	 * 
	 * @throws InterruptedException 	Interrupted
	 * @throws ChannelException 		Could not create all channels
	 * @throws TimeoutException 		Timeout occurred
	 */
	@Override
	public void createAnnotatedChannels(Object object, boolean dryrun) throws ChannelException, InterruptedException, TimeoutException {
		createAnnotatedChannels(object, new HashMap<String,String>(), dryrun);
	}
	
	/**
	 * Connect passed bean to all specified epics channels, i.e. create a ChannelBean
	 * for each channel.
	 * Also register all annotated functions as monitors for the specified channel.
	 *  
	 * @param object		Annotated object
	 * @param macros		Macros to replace macros in annotations
	 * @param dryrun		Flag to indicate whether dummy channels should be created
	 * 
	 * @throws InterruptedException Interrupted
	 * @throws ChannelException Could not create channel
	 * @throws TimeoutException Timeout occurred
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void createAnnotatedChannels(Object object, Map<String,String> macros, boolean dryrun) throws ChannelException, InterruptedException, TimeoutException {
		
		logger.info("Create annotated channels of class "+object.getClass().getName()+" "+ (dryrun?"[dryrun]":""));
		
		try{
			// Merge global macros and passed macros
			Map<String,String> mac = new HashMap<String,String>();
			mac.putAll(globalMacros);
			mac.putAll(macros);
			
			Class<?> c = object.getClass();
			
			// Execute pre init function (if available)
			for(Method m: c.getDeclaredMethods()){
				CaPreInit a = m.getAnnotation(CaPreInit.class);
				
				if(a != null){
					m.invoke(object);
				}
			}
			
			
			// Parse annotations
			List<Field> fieldList = new ArrayList<Field>();
			Map<Field, Integer> sizeMap = new HashMap<Field,Integer>(); // Map holding the number of channels that are associated to the field
			List<Descriptor<?>> descriptorList = new ArrayList<Descriptor<?>>();

			for (Field field : c.getDeclaredFields()) {
				CaChannel annotation = field.getAnnotation(CaChannel.class);
				CaCompositeChannel compositeAnnotation = field.getAnnotation(CaCompositeChannel.class);
				if(annotation!=null){
					if (annotation.name().length==1 && field.getType().isAssignableFrom(Channel.class)) {
						fieldList.add(field);
						sizeMap.put(field, 1);
						if(dryrun){
							descriptorList.add(new DummyChannelDescriptor(annotation.type(), MacroResolver.format(annotation.name()[0], mac), annotation.monitor(), annotation.size()));
						}
						else{
							descriptorList.add(new ChannelDescriptor(annotation.type(), MacroResolver.format(annotation.name()[0], mac), annotation.monitor(), annotation.size()));
						}
					}
					else if (annotation.name().length >0 && field.getType().isAssignableFrom(List.class)) {
						fieldList.add(field);
						sizeMap.put(field, annotation.name().length);
						for (String n : annotation.name()) {
							if(dryrun){
								descriptorList.add(new DummyChannelDescriptor(annotation.type(), MacroResolver.format(n, mac), annotation.monitor(), annotation.size()));
							}
							else{
								descriptorList.add(new ChannelDescriptor(annotation.type(), MacroResolver.format(n, mac), annotation.monitor(), annotation.size()));
							}
						}
					} else {
						logger.warning("Annotation @" + CaChannel.class.getSimpleName() + " not applicable for field '" + field.getName() + "' of type '" + field.getType().getName() + "'");
					}
				}
				else if (compositeAnnotation!=null){
					if(field.getType().isAssignableFrom(Channel.class)){
						fieldList.add(field);
						sizeMap.put(field, 1);
						if(dryrun){
							descriptorList.add(new DummyChannelDescriptor(compositeAnnotation.type(), MacroResolver.format(compositeAnnotation.name(), mac), compositeAnnotation.monitor(), compositeAnnotation.size()));
						}
						else{
							descriptorList.add(new CompositeChannelDescriptor(compositeAnnotation.type(), MacroResolver.format(compositeAnnotation.name(), mac), MacroResolver.format(compositeAnnotation.readback(), mac), compositeAnnotation.monitor(), compositeAnnotation.size()));
						}
					}
				}
			}
			
			// Create all channels
			List<Channel<?>> channelList = createChannels(descriptorList);
			
			// Set channels
			int ccount =0;
			for(int fc=0;fc<fieldList.size();fc++){
				Field f = fieldList.get(fc);
				boolean accessible = f.isAccessible();
				f.setAccessible(true);
				int fsize = sizeMap.get(f);
				if(fsize==1 && f.getType().isAssignableFrom(Channel.class)){ // There might be a list of one element therefor we need the second check
					f.set(object, channelList.get(ccount));
					ccount++;
				}
				else{
					List<Channel<?>> list = new ArrayList<Channel<?>>();
					for(int i=0;i<sizeMap.get(f);i++){
						list.add(channelList.get(ccount));
						ccount++;
					}
					f.set(object, list);
				}
				f.setAccessible(accessible);
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
	
	/**
	 * Destroy all annotated channel beans in the passed object
	 * @param object Annotated object

	 * @throws ChannelException Could not create all channels
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
					if(field.getType().isAssignableFrom(Channel.class)){
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						((Channel<?>) field.get(object)).destroy();
						// Set field/attribute value to null
						field.set(object, null);
						field.setAccessible(accessible);
						
					}
					else if(field.getType().isAssignableFrom(List.class)){
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						@SuppressWarnings("unchecked")
						List<Channel<?>> l = ((List<Channel<?>>) field.get(object));
						for(Channel<?> b: l){
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
	public void destroy() {
		try {
			channelFactory.destroyContext();
		} catch (CAException e) {
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

	
	public void setMacros(Map<String, String> macros) {
		this.globalMacros = macros;
	}
	
	@Override
	public Map<String,String> getMacros(){
		return globalMacros;
	}
	
	@Override
	public void setDryrun(boolean dryrun){
		this.dryrun = dryrun;
	}
	
	public boolean isDryrun(){
		return dryrun;
	}
}
