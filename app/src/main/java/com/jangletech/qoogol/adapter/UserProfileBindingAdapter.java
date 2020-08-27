package com.jangletech.qoogol.adapter;

public class UserProfileBindingAdapter {

    public static String getButtonText(String text) {
        String buttonText = "Verify";
        if (text != null && !text.isEmpty()) {
            buttonText = "Verified";
        }
        return buttonText;
    }

    public static boolean isFieldVisible(String fieldValue) {
        if (fieldValue != null && fieldValue.isEmpty())
            return true;
        else
            return false;
    }

    /*public static String decryptedText(String text){
        return AESSecurities.getInstance().decrypt(text);
    }*/
}
