package com.ali.jar2proxy.generic.facade.annotation;

public @interface BizAction {

  String name() default "";
  String commitMethod() default "";
  String field() default "out";

}
