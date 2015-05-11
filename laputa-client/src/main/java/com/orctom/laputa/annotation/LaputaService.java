package com.orctom.laputa.annotation;

import java.lang.annotation.*;

/**
 * Created by hao on 4/28/15.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface LaputaService {

	String value();
}
