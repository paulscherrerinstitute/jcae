package ch.psi.jcae.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class RelayPropertyChangeListener implements PropertyChangeListener{

	private final PropertyChangeSupport propertyChangeSupport;
	private final String property;
	
	public RelayPropertyChangeListener(String property, PropertyChangeSupport propertyChangeSupport){
		this.property = property;
		this.propertyChangeSupport = propertyChangeSupport;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		propertyChangeSupport.firePropertyChange(property, evt.getOldValue(), evt.getNewValue());
	}

}
