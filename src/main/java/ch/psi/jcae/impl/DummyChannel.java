package ch.psi.jcae.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelException;

/**
 * Dummy implementation of a channel
 */
public class DummyChannel<T> implements Channel<T> {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private String name; // Channel name
	private Integer size;
	private boolean monitored = false;

	private T value = null;

	@SuppressWarnings("unchecked")
	public DummyChannel(Class<T> type, String name, Integer size, boolean monitored) {
		this.name = name;
		this.size = size;
		this.monitored = monitored;
		if (Double.class.equals(type)) {
			value = (T) new Double(0);
		}
		else if (Integer.class.equals(type)) {
			value = (T) new Integer(0);
		}
		else if (String.class.equals(type)) {
			value = (T) new String();
		}
	}

	@Override
	public T getValue() throws InterruptedException, TimeoutException, ChannelException, ExecutionException {
		return value;
	}

	@Override
	public T getValue(boolean force) throws InterruptedException, TimeoutException, ChannelException, ExecutionException {
		return value;
	}

	@Override
	public Future<T> getValueAsync() throws IllegalStateException, ChannelException {
		return getValueAsync(true);
	}

	@Override
	public Future<T> getValueAsync(boolean force) throws IllegalStateException, ChannelException {
		return new Future<T>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean isCancelled() {
				return false;
			}

			@Override
			public boolean isDone() {
				return true;
			}

			@Override
			public T get() throws InterruptedException, ExecutionException {
				return value;
			}

			@Override
			public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
				return value;
			}
		};
	}

	@Override
	public void setValue(T value) throws InterruptedException, ExecutionException, ChannelException {
		propertyChangeSupport.firePropertyChange("value", this.value, this.value = value);
	}

	@Override
	public void setValueNoWait(T value) throws InterruptedException, ExecutionException, ChannelException {
		propertyChangeSupport.firePropertyChange("value", this.value, this.value = value);
	}

	@Override
	public Future<T> setValueAsync(T v) throws ChannelException {
		propertyChangeSupport.firePropertyChange("value", this.value, this.value = v);
		return new Future<T>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean isCancelled() {
				return false;
			}

			@Override
			public boolean isDone() {
				return true;
			}

			@Override
			public T get() throws InterruptedException, ExecutionException {
				return value;
			}

			@Override
			public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
				return value;
			}
		};
	}

	@Override
	public Future<T> waitForValueAsync(T rvalue) throws ChannelException {
		// Default comparator checking for equality
		Comparator<T> comparator = new Comparator<T>() {
			@Override
			public int compare(T o, T o2) {
				if (o.equals(o2)) {
					return 0;
				}
				return -1;
			}
		};
		return waitForValueAsync(rvalue, comparator);
	}

	@Override
	public Future<T> waitForValueAsync(T rvalue, long waitRetryPeriod) throws ChannelException {
		// Default comparator checking for equality
		Comparator<T> comparator = new Comparator<T>() {
			@Override
			public int compare(T o, T o2) {
				if (o.equals(o2)) {
					return 0;
				}
				return -1;
			}
		};
		return waitForValueAsync(rvalue, comparator, waitRetryPeriod);
	}

	@Override
	public Future<T> waitForValueAsync(final T rvalue, final Comparator<T> comparator) throws ChannelException {
		return new Future<T>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean isCancelled() {
				return false;
			}

			@Override
			public boolean isDone() {
				return true;
			}

			@Override
			public T get() throws InterruptedException, ExecutionException {
				if (value != null && comparator.compare(value, rvalue) == 0) {
					return rvalue;
				}
				throw new IllegalStateException();
			}

			@Override
			public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
				if (value != null && comparator.compare(value, rvalue) == 0) {
					return rvalue;
				}
				else {
					throw new TimeoutException("Value not reached in time");
				}
			}
		};
	}

	@Override
	public Future<T> waitForValueAsync(final T rvalue, final Comparator<T> comparator, long waitRetryPeriod) throws ChannelException {
		return new Future<T>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean isCancelled() {
				return false;
			}

			@Override
			public boolean isDone() {
				return true;
			}

			@Override
			public T get() throws InterruptedException, ExecutionException {
				if (value != null && comparator.compare(value, rvalue) == 0) {
					return rvalue;
				}
				throw new IllegalStateException();
			}

			@Override
			public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
				if (value != null && comparator.compare(value, rvalue) == 0) {
					return rvalue;
				}
				else {
					throw new TimeoutException("Value not reached in time");
				}
			}
		};
	}

	@Override
	public T waitForValue(T rvalue) throws InterruptedException, ExecutionException, ChannelException {
		return waitForValueAsync(rvalue).get();
	}

	@Override
	public T waitForValue(T rvalue, long waitRetryPeriod) throws InterruptedException, ExecutionException, ChannelException {
		return waitForValueAsync(rvalue, waitRetryPeriod).get();
	}

	@Override
	public T waitForValue(final T rvalue, final Comparator<T> comparator) throws InterruptedException, ExecutionException, ChannelException {
		return waitForValueAsync(rvalue, comparator).get();
	}

	@Override
	public T waitForValue(final T rvalue, final Comparator<T> comparator, long waitRetryPeriod) throws InterruptedException, ExecutionException, ChannelException {
		return waitForValueAsync(rvalue, comparator, waitRetryPeriod).get();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public Integer getSize() {
		if (size == null) {
			return 1; // Default size
		}
		return size;
	}

	@Override
	public String getSource() {
		return "dummy.ioc.psi.ch";
	}

	@Override
	public boolean isMonitored() {
		return monitored;
	}

	@Override
	public void setMonitored(boolean monitored) throws ChannelException {
		this.monitored = monitored;
	}

	@Override
	public void destroy() throws ChannelException {
		// do nothing
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		if (!monitored) {
			this.monitored = true;
		}
		propertyChangeSupport.addPropertyChangeListener(l);
	}

	@Override
	public void addPropertyChangeListener(String name, PropertyChangeListener l) {
		if (!monitored) {
			this.monitored = true;
		}
		propertyChangeSupport.addPropertyChangeListener(name, l);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(l);
	}

	@Override
	public Class<?> getFieldType() {
		return this.value != null ? this.value.getClass() : Object.class;
	}
}
