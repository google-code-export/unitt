package com.unitt.commons.authorization.jpa;

import com.unitt.commons.persist.ActiveUserHelper;

public class MockActiveUserHelper implements ActiveUserHelper
{
    public long getActiveUserId()
    {
        return 1;
    }
}
