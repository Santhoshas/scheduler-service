package com.marketlogic.app.scheduler.exception;

/**
 * Result codes for STOMP messages
 */
public enum MessageResultCode {
    OK("0"),
    UNEXPECTED("1"),
    /**
     * Is returned if the request is incomplete/invalid. Generic code. This is a programming error
     * when making a call.
     */
    INVALID_REQUEST("2");

    private final String code;

    MessageResultCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static MessageResultCode getCode(String code) {
        for (MessageResultCode e : MessageResultCode.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

}
