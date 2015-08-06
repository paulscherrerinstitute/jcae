package ch.psi.jcae;

import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Utility class for channel functions
 */
public class Channels {
	
	public static <T> void waitForValue(Channel<T> channel, T value) {
		try {
			channel.waitForValue(value);
		} catch (ExecutionException | ChannelException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <T> void waitForValue(Channel<T> channel, T value, Comparator<T> comparator) {
		try {
			channel.waitForValue(value, comparator);
		} catch (InterruptedException | ExecutionException | ChannelException e) {
			throw new RuntimeException();
		}
	}

	public static <T> Future<T> waitForValueAsync(Channel<T> channel, T value){
		try {
			return channel.waitForValueAsync(value);
		} catch (ChannelException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <T> Future<T> waitForValueAsync(Channel<T> channel, T value, Comparator<T> comparator){
		try {
			return channel.waitForValueAsync(value, comparator);
		} catch (ChannelException e) {
			throw new RuntimeException();
		}
	}
	
	public static <T> Channel<T> create(Context context, ChannelDescriptor<T> descriptor){
		try {
			return context.createChannel(descriptor);
		} catch (ChannelException | InterruptedException | TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

}
