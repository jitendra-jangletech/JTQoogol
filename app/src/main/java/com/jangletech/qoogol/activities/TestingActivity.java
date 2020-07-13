package com.jangletech.qoogol.activities;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ActivityTestingBinding;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.nio.charset.StandardCharsets;

public class TestingActivity extends AppCompatActivity {

    private static final String TAG = "TestingActivity";
    private ActivityTestingBinding mBinding;
    private String encoded = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_testing);

        mBinding.btnEncode.setOnClickListener(v -> {
            String text = mBinding.text.getText().toString().trim();
            encoded = StringUtils.stripAccents(Base64.encodeToString(text.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
            Log.d(TAG, "Encoded : " + encoded);
        });

        mBinding.btnDecode.setOnClickListener(v -> {
            //String text  = mBinding.text.getText().toString();
            String decoded = decodedMessage(encoded);
            Log.d(TAG, "Decoded : " + StringEscapeUtils.unescapeJava(decoded));
            mBinding.text.setText(StringEscapeUtils.unescapeJava(decoded));
        });
    }

//    private String check(String s){
//
//        int len = s.length();
//        for (int i = 0; i < len; i++) {
//            // checks whether the character is neither a letter nor a digit
//            // if it is neither a letter nor a digit then it will return false
//            if ((Character.isLetterOrDigit(s.charAt(i)) == false)) {
//                s.replace("")
//            }
//        }
//        return s;
//    }

    public static String decodedMessage(String message) {
        try {
            byte[] messageBytes = Base64.decode(message, Base64.DEFAULT);
            return new String(messageBytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            Log.e(TAG, "decodedMessage: " + ex.getMessage());
        }
        return "";
    }
}