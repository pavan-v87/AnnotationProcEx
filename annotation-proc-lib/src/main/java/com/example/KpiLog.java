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
public @interface KpiLog {
    @LogConstants.CATEGORY String category();
    @LogConstants.POINT int point();
    @LogConstants.EVENT String event();
}
