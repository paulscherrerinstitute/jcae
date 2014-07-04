package ch.psi.jcae.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to annotate a function that is executed before the ChannelBeans
 * that are annotated with @CaChannel are destroyed. If multiple methods are 
 * annotated with this annotation there is no guarantee in which order the methods are
 * called.
 * The annotated function must not have parameters.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CaPreDestroy {
}
