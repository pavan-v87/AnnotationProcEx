package com.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Pavan.VijayaNar on 9/6/2017.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface KpiLogStart {
    @LogConstants.CATEGORY String category();
    @LogConstants.EVENT String event();
}
