package com.jangletech.qoogol.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ActivityTestingBinding;

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
            String text  = mBinding.text.getText().toString();
             encoded = Base64.encodeToString(text.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
            Log.d(TAG, "Encoded : "+ StringEscapeUtils.escapeJava(encoded));

        });

        mBinding.btnDecode.setOnClickListener(v -> {
            //String text  = mBinding.text.getText().toString();
            String decoded = decodedMessage(encoded);
            Log.d(TAG, "Decoded : "+StringEscapeUtils.unescapeJava(decoded));
            mBinding.text.setText(StringEscapeUtils.unescapeJava(decoded));
        });
    }

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