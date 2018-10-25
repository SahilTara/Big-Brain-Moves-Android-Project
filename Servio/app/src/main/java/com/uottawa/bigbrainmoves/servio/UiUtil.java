package com.uottawa.bigbrainmoves.servio;

import android.content.Context;
import android.content.Intent;

public class UiUtil {
    public static Intent getIntentFromType(Context context, String userType) {
        switch (userType) {
            case "admin":
                Intent intent = new Intent(context, AdminMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                return intent;
            case "service":
                Intent service = new Intent(context, ServiceMainActivity.class);
                service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                return service;
            default:
                    Intent user = new Intent(context, MainActivity.class);
                    user.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    return user;
        }

    }
}
