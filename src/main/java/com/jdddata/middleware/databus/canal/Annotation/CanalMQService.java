package com.jdddata.middleware.databus.canal.Annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CanalMQService {
    String value() default "";
}
