package com.marketlogic.app.scheduler.exception;

/**
 * Saurer exception with message code
 */
public class SchedulerServiceException extends RuntimeException {

    private static final long serialVersionUID = 2677995731166966405L;

    private final MessageResultCode messageCode;

    /**
     * Constructor with message text and code
     *
     * @param msg         message text
     * @param messageCode
     */
    public SchedulerServiceException(String msg, MessageResultCode messageCode) {
        super(msg);
        this.messageCode = messageCode;
    }

    public MessageResultCode getMessageCode() {
        return messageCode;
    }
}
