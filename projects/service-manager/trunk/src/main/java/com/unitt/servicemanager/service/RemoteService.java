package com.unitt.servicemanager.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

//@Target(ElementType.TYPE)
public @interface RemoteService
{
    public boolean inherits() default true;
}
