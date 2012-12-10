/*
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

import ch.psi.jcae.converter.AbstractConverterBean;
import ch.psi.jcae.impl.ChannelBean;

/**
 *
 * @author ebner
 */
public class ExampleConverter extends AbstractConverterBean<Double, String>{

    public ExampleConverter(ChannelBean<Double> bean){
        super(bean);
    }

    @Override
    protected String convertForward(Double e) {
        String v = "";
        // Put your conversion code here
        return(v);
    }

    @Override
    protected Double convertReverse(String t) {
        Double v = 0d;
        // Put your conversion code here
        return(v);
    }

}