package com.baki.backer.domain.image;

import java.util.Locale;

public enum ImageType {
    POST, PROFILE;

    public static ImageType fromName(String type) {
        return ImageType.valueOf(type.toUpperCase(Locale.ENGLISH));
    }
}
