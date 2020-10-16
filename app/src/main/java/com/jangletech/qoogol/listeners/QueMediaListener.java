package com.jangletech.qoogol.listeners;

import android.content.Intent;
import android.net.Uri;

/**
 * Created by Pritali on 10/16/2020.
 */
public interface QueMediaListener {
    void onMediaReceived(int requestCode, int resultCode, Intent data, Uri mphotouri);
    void onScanImageClick(Uri uri);
}
