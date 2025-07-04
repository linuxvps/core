package org.example.core.dto;

public enum LoginType {
    MANUAL("manual"),
    OAUTH_GOOGLE("oauth_google"),
    OAUTH_GITHUB("oauth_github"),
    OAUTH_FACEBOOK("oauth_facebook"),
    SSO("sso"),
    MAGIC_LINK("magic_link"),
    OTP("otp"),
    BIOMETRIC("biometric");

    private final String value;

    LoginType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LoginType fromValue(String value) {
        for (LoginType type : LoginType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown login type: " + value);
    }
}
