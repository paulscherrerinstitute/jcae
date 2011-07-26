/*
 *  Copyright (C) 2011 Paul Scherrer Institute
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.psi.jcae.examples;

import ch.psi.jcae.ChannelBean;
import ch.psi.jcae.ChannelBeanFactory;
import gov.aps.jca.CAException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example
 */
public class MonitorExample {

    public static void main(String[] args) throws CAException, InterruptedException {
        // Get channel factory
        ChannelBeanFactory factory = ChannelBeanFactory.getFactory();

        // Create ChannelBean
        ChannelBean<String> bean = factory.createChannelBean(String.class, "ARIDI-PCT:CURRENT", true);

        // Add PropertyChangeListener to ChannelBean to get value updates
        bean.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent pce) {
                if (pce.getPropertyName().equals(ChannelBean.PROPERTY_VALUE)) {
                    Logger.getLogger(MonitorExample.class.getName()).log(Level.INFO, "Current: {0}", pce.getNewValue());
                }
            }
        });

        // Monitor the Channel for 10 seconds
        Thread.sleep(10000);

        // Destroy ChannelBean
        bean.destroy();

        // Destroy context of the factory
        ChannelBeanFactory.getFactory().getChannelFactory().destroyContext();
    }
}