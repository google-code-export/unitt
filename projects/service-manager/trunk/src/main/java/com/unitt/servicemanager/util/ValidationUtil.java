package com.unitt.servicemanager.util;

public class ValidationUtil
{
    public static String appendMessage(String aOriginalMessage, String aMessageToAppend)
    {
        String message = aOriginalMessage;
     
        //make sure we have an actual message to add
        if (aMessageToAppend != null)
        {
            //make sure we arent null
            if (message == null)
            {
                message = "";
            }
            
            //append value
            message += aMessageToAppend;
        }
        
        return message;
    }
}
