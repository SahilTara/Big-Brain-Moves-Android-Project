package com.uottawa.bigbrainmoves.servio.util;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.pchmn.materialchips.model.ChipInterface;

public class ServiceTypeChip implements ChipInterface {
    private final String info;
    private final String label;
    @Override
    public Object getId() {
        return null;
    }

    @Override
    public Uri getAvatarUri() {
        return null;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getInfo() {
        return info;
    }

    public ServiceTypeChip(String info, String label) {
        this.info = info;
        this.label = label;
    }
    @Override
    public boolean equals(Object object) {
        return object instanceof ServiceTypeChip && ((ServiceTypeChip) object).label.equals(label);
    }
}
