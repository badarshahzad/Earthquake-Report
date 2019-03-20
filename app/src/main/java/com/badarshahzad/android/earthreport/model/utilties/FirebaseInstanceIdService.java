package com.badarshahzad.android.earthreport.model.utilties;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by root on 8/19/18.
 */

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {
    private static final String TAG = "FirebaseInstanceIdServi";

    @Override
    public void onTokenRefresh() {

        //Getting registration token

        String refreshToken = FirebaseInstanceId.getInstance().getToken();

    }
}
