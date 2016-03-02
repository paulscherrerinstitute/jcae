package ch.psi.jcae.impl;

import gov.aps.jca.CAStatus;
import java.util.concurrent.ExecutionException;

/**
 *
 */
class ChannelAccessException extends ExecutionException{
    ChannelAccessException(CAStatus status){
        super(status.getMessage());
    }
}
