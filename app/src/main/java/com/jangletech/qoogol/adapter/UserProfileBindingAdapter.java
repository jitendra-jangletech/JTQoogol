package com.jangletech.qoogol.adapter;

public class UserProfileBindingAdapter {

    public static String getButtonText(String text) {
        String buttonText = "Verify";
        if (text != null && !text.isEmpty()) {
            buttonText = "Verified";
        }
        return buttonText;
    }

    /*public static String decryptedText(String text){
        return AESSecurities.getInstance().decrypt(text);
    }*/
}
