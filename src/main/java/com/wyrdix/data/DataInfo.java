package com.wyrdix.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set a <strong>unique</strong> id to a data type
 * If this id is used in a data structure then it would mean that data is a serialized object of this type
 * Set data parent, if this field is set this data should only exist inside its parent
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataInfo {
    String id();

    Class<? extends Data> parent() default Data.class;
}
