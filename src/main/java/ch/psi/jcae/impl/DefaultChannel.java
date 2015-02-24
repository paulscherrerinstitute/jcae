package ch.psi.jcae.impl;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Channel;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.Context;
import gov.aps.jca.Monitor;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.psi.jcae.ChannelException;
import ch.psi.jcae.impl.handler.Handlers;
import ch.psi.jcae.impl.type.ArrayValueHolder;

/**
 * Wrapper for the JCA Channel class. Introduces an additional layer of
 * abstraction and hides all Channel Access related things from the
 * user/developer.
 * 
 * The class also provides PropertyChangeSupport for the channel value and
 * connection state. The keys are defined in the static variables
 * <code>PROPERTY_VALUE</code> and <code>PROPERTY_CONNECTION</code>
 * 
 * @param <E>
 *            Type of ChannelBean value
 */
public class DefaultChannel<E> implements ch.psi.jcae.Channel<E> {

	private static Logger logger = Logger.getLogger(DefaultChannel.class.getName());
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private Class<E> type;
	private Monitor monitor;
	private ConnectionListener listener;
	private Channel channel;
	private Integer elementCount;

	private final AtomicReference<E> value = new AtomicReference<E>();

	private boolean connected = false;
	private boolean monitored = false;

	/**
	 * Constructor - Create a ChannelBean for the specified Channel. A Monitor
	 * is attached to the Channel if the <code>monitored</code> parameter is
	 * true.
	 * 
	 * @param type
	 *            Data type
	 * @param channel
	 *            Channel
	 * @param size
	 *            Retries for set/get operations if something fails during an
	 *            operation
	 * @param monitored
	 *            Attach a Monitor to the Channel
	 * @throws InterruptedException
	 *             -
	 * @throws ChannelException
	 *             -
	 * @throws TimeoutException
	 *             -
	 * @throws ExecutionException
	 *             -
	 */
	public DefaultChannel(Class<E> type, Channel channel, Integer size, boolean monitored) throws InterruptedException, TimeoutException, ChannelException, ExecutionException {

		// Check whether type is supported
		if (!Handlers.HANDLERS.containsKey(type)) {
			throw new IllegalArgumentException("Type " + type.getName() + " not supported");
		}

		this.type = type;
		this.channel = channel;
		this.connected = channel.getConnectionState().isEqualTo(ConnectionState.CONNECTED);

		// Set channel size
		updateSize(size);

		attachConnectionListener();

		setMonitored(monitored);
	}

	/**
	 * Get current value of the channel.
	 * 
	 * @return Value of the channel in the type of the ChannelBean
	 * @throws InterruptedException
	 *             -
	 * @throws ChannelException
	 *             -
	 * @throws TimeoutException
	 *             -
	 * @throws ExecutionException
	 *             -
	 */
	@Override
	public E getValue() throws InterruptedException, TimeoutException, ChannelException, ExecutionException {
		return (getValue(false));
	}

	/**
	 * Get current value of the channel and force the API to directly fetch it
	 * from the network.
	 * 
	 * @param force
	 *            Force the library to get the value via the network
	 * @return Value of the channel in the type of the ChannelBean
	 * @throws InterruptedException
	 *             -
	 * @throws ChannelException
	 *             -
	 * @throws TimeoutException
	 *             -
	 * @throws ExecutionException
	 *             -
	 */
	@Override
	public E getValue(boolean force) throws InterruptedException, TimeoutException, ChannelException, ExecutionException {
		return (getValueAsync(force).get());
	}

	/**
	 * Get value asynchronously
	 * 
	 * @return Future
	 * @throws IllegalStateException
	 *             -
	 * @throws ChannelException
	 *             -
	 */
	@Override
	public Future<E> getValueAsync() throws IllegalStateException, ChannelException {
		return getValueAsync(false);
	}

	/**
	 * Get value in an asynchronous way
	 * 
	 * @param force
	 *            Force to get the value
	 * @return Future to retrieve the value
	 * @throws IllegalStateException
	 *             -
	 * @throws ChannelException
	 *             -
	 */
	@Override
	public Future<E> getValueAsync(boolean force) throws IllegalStateException, ChannelException {
		if (!force && monitored) { // If monitored return future holding actual
									// value
			return new GetMonitoredFuture<E>(value.get());
		}
		else {
			try {
				GetFuture<E> listener = new GetFuture<E>(this.type);
				channel.get(Handlers.HANDLERS.get(type).getDBRType(), elementCount, listener);
				channel.getContext().flushIO();
				return listener;
			} catch (CAException e) {
				throw new ChannelException("Unable to set value to channel: " + channel.getName(), e);
			}
		}
	}

	/**
	 * Set value synchronously
	 * 
	 * @param value
	 *            Value to set
	 * @throws InterruptedException
	 *             -
	 * @throws ExecutionException
	 *             -
	 * @throws ChannelException
	 *             -
	 */
	@Override
	public void setValue(E value) throws InterruptedException, ExecutionException, ChannelException {
		setValueAsync(value).get();
	}

	@Override
	public void setValueNoWait(E value) throws InterruptedException, ExecutionException, ChannelException {
		try {
			Handlers.HANDLERS.get(type).setValue(channel, value);
			channel.getContext().flushIO();
		} catch (CAException e) {
			throw new ChannelException("Unable to set value to channel", e);
		}
	}

	/**
	 * Set value asynchronously
	 * 
	 * @param value
	 *            Value to set
	 * @return Future to determine when set is done ...
	 * @throws ChannelException
	 *             Unable to set value
	 */
	@Override
	public Future<E> setValueAsync(E value) throws ChannelException {
		try {
			SetFuture<E> listener = new SetFuture<E>(value);
			Handlers.HANDLERS.get(type).setValue(channel, value, listener);
			channel.getContext().flushIO();
			return listener;
		} catch (CAException e) {
			throw new ChannelException("Unable to set value to channel", e);
		}
	}

	/**
	 * Wait until channel has reached the specified value.
	 * 
	 * @param rvalue
	 *            Value the channel should reach
	 * @throws ChannelException
	 *             -
	 */
	public Future<E> waitForValueAsync(E rvalue) throws ChannelException {

		// Default comparator checking for equality
		Comparator<E> comparator = new Comparator<E>() {
			@Override
			public int compare(E o, E o2) {
				if (o.equals(o2)) {
					return 0;
				}
				return -1;
			}
		};
		return waitForValueAsync(rvalue, comparator);
	}

	/**
	 * Wait until channel has reached the specified value. Re-establish the
	 * monitor after the specified waitRetryPeriod
	 * 
	 * @param rvalue
	 *            Value to wait for
	 * @param waitRetryPeriod
	 *            Period between retries
	 * @return Future to retrieve value
	 * @throws ChannelException
	 *             -
	 */
	public Future<E> waitForValueAsync(E rvalue, long waitRetryPeriod) throws ChannelException {
		// Default comparator checking for equality
		Comparator<E> comparator = new Comparator<E>() {
			@Override
			public int compare(E o, E o2) {
				if (o.equals(o2)) {
					return 0;
				}
				return -1;
			}
		};
		return waitForValueAsync(rvalue, comparator, waitRetryPeriod);
	}

	/**
	 * Wait for channel to meet condition specified by the comparator
	 * 
	 * @param rvalue
	 *            Value to wait for
	 * @param comparator
	 *            Implementation of the Comparator interface that defines when a
	 *            value is reached. The Comparator need to return 0 if the
	 *            condition is met. The first argument of the comparator is the
	 *            value of the channel, the second the expected value.
	 * @throws ChannelException
	 *             -
	 */
	public Future<E> waitForValueAsync(E rvalue, Comparator<E> comparator) throws ChannelException {
		return new WaitFuture<E>(channel, elementCount, rvalue, comparator);
	}

	/**
	 * Wait until channel has reached the specified value. Re-establish the
	 * monitor after the specified waitRetryPeriod
	 * 
	 * @param rvalue
	 *            Value to wait for
	 * @param comparator
	 *            Comparator to use
	 * @param waitRetryPeriod
	 *            Period between retries
	 * @return Future to retrieve value
	 * @throws ChannelException
	 *             -
	 */
	public Future<E> waitForValueAsync(E rvalue, Comparator<E> comparator, long waitRetryPeriod) throws ChannelException {
		return new WaitRetryFuture<E>(channel, elementCount, rvalue, comparator, waitRetryPeriod);
	}

	@Override
	public E waitForValue(E rvalue) throws InterruptedException, ExecutionException, ChannelException {
		return waitForValueAsync(rvalue).get();
	}

	@Override
	public E waitForValue(E rvalue, long timeout) throws InterruptedException, ExecutionException, ChannelException, TimeoutException {
		return waitForValueAsync(rvalue).get(timeout, TimeUnit.MILLISECONDS);
	}

	@Override
	public E waitForValue(final E rvalue, final Comparator<E> comparator) throws InterruptedException, ExecutionException, ChannelException {
		return waitForValueAsync(rvalue, comparator).get();
	}

	@Override
	public E waitForValue(final E rvalue, final Comparator<E> comparator, long waitRetryPeriod) throws InterruptedException, ExecutionException, ChannelException {
		return waitForValueAsync(rvalue, comparator, waitRetryPeriod).get();
	}

	/**
	 * Check whether the channel is connected. Flag that indicates that data is
	 * valid (connected) or not (not connected)
	 * 
	 * @return Connection status of the channel managed by this ChannelBean
	 */
	@Override
	public boolean isConnected() {
		return (connected);
	}

	/**
	 * Get the name of the Channel that is managed by this ChannelBean object
	 * 
	 * @return Name of the managed channel
	 */
	@Override
	public String getName() {
		return (channel.getName());
	}

	/**
	 * Get the number of elements of the channel. This function returns the
	 * number of elements of the managed channel if the channel is array typed.
	 * If not the function will return 1.
	 * 
	 * @return In the case of an array channel the number of elements, for a
	 *         scalar channel 1.
	 */
	@Override
	public Integer getSize() {
		return elementCount;
	}

    @Override
    public void setSize(Integer size) throws ChannelException {
        if(size == null | size != elementCount) {
            updateSize(size);

            if (monitor != null) {
                attachMonitor();
            }
        }
    }

    private void updateSize(Integer size){
        int csize = channel.getElementCount();
        if (size != null && size > 0) {
            if (size > 0 && size <= csize) {
                this.elementCount = size;
            }
            else {
                throw new IllegalArgumentException("Specified channel size [" + size + "]  is not applicable. Maximum size is " + csize);
            }
        }
        else {
            // instead of using a marker interface one could also query type's
            // getValue method for its return value and check if it is an array
            // (in this case the check for
            // ByteArrayString.class.isAssignableFrom(type) is still necessary)
            if (type.isArray() || ArrayValueHolder.class.isAssignableFrom(type)) {
                this.elementCount = csize; // the size of the array may vary
                // over time (always take the actual
                // size of the channel)
            }
            else {
                this.elementCount = 1; // if it is not an array type size is
                // always 1
            }
        }

    }

	/**
	 * Get Hostname of the IOC the channel is served
	 * 
	 * @return Name of the IOC hosting the managed channel
	 */
	@Override
	public String getSource() {
		return (channel.getHostName());
	}

	/**
	 * Get whether the channel is monitored
	 * 
	 * @return the monitored
	 */
	@Override
	public boolean isMonitored() {
		return monitored;
	}

	/**
	 * Set whether the channel is monitored. If the channel is not monitored and
	 * it should be monitored then a new monitor is added to the underlying
	 * channel. If the channel is set to be not monitored but was monitored
	 * before this function will remove the monitors added to the underlying
	 * channel.
	 * 
	 * @param monitored
	 *            the monitored to set
	 * @throws ChannelException
	 *             -
	 */
	@Override
	public void setMonitored(boolean monitored) throws ChannelException {
		if (monitored && !this.monitored) {
			attachMonitor();
			try {
				value.set(getValue(true)); // Get initial value
			} catch (Exception e) {
				throw new ChannelException("Unable to get initial value after setting channel to monitored ", e);
			}
		}
		else if (!monitored && this.monitored) {
			removeMonitor();
		}
		this.monitored = monitored;
	}

	private void attachConnectionListener() throws ChannelException {
		try {
			listener = new ConnectionListener() {
				@Override
				public void connectionChanged(ConnectionEvent event) {
					propertyChangeSupport.firePropertyChange(PROPERTY_CONNECTED, connected, connected = event.isConnected());
				}
			};
			channel.addConnectionListener(listener);
		} catch (CAException e) {
			throw new ChannelException("Unable to attach connection listener to channel", e);
		}
	}

	private void removeConnectionListener() throws ChannelException {
		try {
			channel.removeConnectionListener(listener);
			channel.getContext().flushIO();
		} catch (CAException e) {
			throw new ChannelException("Unable to remove connection listener", e);
		}
	}

	private void attachMonitor() throws ChannelException {

		if (monitor != null) {
			logger.warning("There is already an monitor attached - removing old one and attaching new");
			removeMonitor();
		}

		try {

			monitor = channel.addMonitor(Handlers.HANDLERS.get(type).getDBRType(), elementCount, Monitor.VALUE, new MonitorListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void monitorChanged(MonitorEvent event) {
					if (event.getStatus() == CAStatus.NORMAL) {
						try {
							E v = (E) Handlers.HANDLERS.get(type).getValue(event.getDBR());
							propertyChangeSupport.firePropertyChange(PROPERTY_VALUE, value.getAndSet(v), v);
						} catch (Exception e) {
							logger.log(Level.WARNING, "Exception occured while calling callback", e);
						}
					} else {
						if (!((Channel) event.getSource()).getConnectionState().equals(ConnectionState.CLOSED)) {
							logger.severe("Monitor fired but CAStatus is not NORMAL - CAStatus: " + event.getStatus() + " - Channel: " + event.getSource().toString());
						}
						logger.severe("Monitor fired but there is something else");
					}

				}

			});

			channel.getContext().flushIO();
		} catch (CAException e) {
			throw new ChannelException("Unable to attach monitor to channel", e);
		}
	}

	private void removeMonitor() throws ChannelException {
		try {
			if (monitor != null) {
				logger.finest("Clear monitor - " + monitor.hashCode());
				monitor.clear();
				channel.getContext().flushIO();
			}
		} catch (CAException e) {
			throw new ChannelException("Unable to remove monitor to channel");
		} finally {
			monitor = null;
		}
	}

	/**
	 * Destroy channel bean. Method will detach a possible monitor of this bean
	 * for the channel and destroy the channel of the bean.
	 * 
	 * @throws ChannelException
	 *             -
	 */
	@Override
	public void destroy() throws ChannelException {

		removeMonitor();
		removeConnectionListener();

		try {
			Context c = channel.getContext();
			channel.destroy();
			c.flushIO();
		} catch (CAException e) {
			throw new ChannelException("Unable to destroy channel", e);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		// Cleanup Object state
		destroy();
	}

	/**
	 * Add/register a property change listener for this object If the channel is
	 * not set to monitored it will be automatically set to be monitored!
	 * 
	 * @param l
	 *            Listener object
	 */
	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		try {
			if (!isMonitored()) {
				setMonitored(true);
			}
			propertyChangeSupport.addPropertyChangeListener(l);
		} catch (ChannelException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addPropertyChangeListener(String name, PropertyChangeListener l) {
		try {
			if (!isMonitored()) {
				setMonitored(true);
			}
			propertyChangeSupport.addPropertyChangeListener(name, l);
		} catch (ChannelException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Remove property change listener from this object
	 * 
	 * @param l
	 *            Listener object
	 */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(l);
	}

	@Override
	public Class<?> getFieldType() {
		return Handlers.getFieldType(this.channel.getFieldType(), this.elementCount > 1);
	}
}

