package com.netflix.yunai;

/**
 * 非源码
 * 笔者用来标记方法是否理解
 */
public @interface Done {

    /**
     * @return 进度，max 100
     */
    int progress() default 0;

}