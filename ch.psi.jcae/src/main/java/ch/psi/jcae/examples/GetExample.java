/**
 *  Copyright (C) 2010 Paul Scherrer Institute
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetExample {

    public static void main(String[] args) throws CAException, InterruptedException {

        // Get channel factory
        ChannelBeanFactory factory = ChannelBeanFactory.getFactory();

        // Connect to channel
        ChannelBean<String> bean = factory.createChannelBean(String.class, "ARIDI-PCT:CURRENT", true);

        // Get value
        String value = bean.getValue();
        Logger.getLogger(GetExample.class.getName()).log(Level.INFO, "{0}", value);

        // Disconnect from channel
        bean.destroy();

        // Close all connections
        ChannelBeanFactory.getFactory().getChannelFactory().destroyContext();
    }
}