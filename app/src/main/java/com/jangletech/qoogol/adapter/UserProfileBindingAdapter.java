package com.jangletech.qoogol.adapter;

import com.jangletech.qoogol.util.AESSecurities;

public class UserProfileBindingAdapter {

    public static String getButtonText(String text){
        String buttonText = "Verify";
        if(text!=null && !text.isEmpty()){
            buttonText = "Verified";
        }
        return buttonText;
    }

    public static String decryptedText(String text){
        return AESSecurities.getInstance().decrypt(text);
    }
}
