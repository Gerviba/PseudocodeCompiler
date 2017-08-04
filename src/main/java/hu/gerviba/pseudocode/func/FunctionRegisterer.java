package hu.gerviba.pseudocode.func;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(CLASS)
@Target(TYPE)
public @interface FunctionRegisterer {
	
	public String path() default "$";
	public String name();
	public String[] aliases() default {};
	public String[] usage() default {};
	
}
