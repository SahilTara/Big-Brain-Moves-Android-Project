package com.uottawa.bigbrainmoves.servio.util;

import android.content.Context;
import android.content.Intent;

import com.uottawa.bigbrainmoves.servio.activities.AdminMainActivity;
import com.uottawa.bigbrainmoves.servio.activities.MainActivity;
import com.uottawa.bigbrainmoves.servio.activities.ServiceMainActivity;
import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.util.enums.AccountType;

public class UiUtil {
    public static Intent getIntentFromType(Context context, AccountType userType) {
        switch (userType) {
            case ADMIN:
                Intent intent = new Intent(context, AdminMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                return intent;
            case SERVICE_PROVIDER:
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
