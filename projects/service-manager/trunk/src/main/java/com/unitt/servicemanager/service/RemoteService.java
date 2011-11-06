package com.unitt.servicemanager.service;


//@Target(ElementType.TYPE)
public @interface RemoteService
{
    public boolean inherits() default true;
}
