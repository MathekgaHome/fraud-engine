package za.co.fraud.fraud_service.exceptions;


import lombok.Getter;
import za.co.fraud.fraud_service.constant.ErrorMessagesLocale;

@Getter
public class UserServiceException extends RuntimeException {

    private String message;
    private Object[] args;

    public UserServiceException(final String message, final String arg) {
        super(message);
        this.message =  message;
        args = new Object[]{arg};
    }

    public UserServiceException(final ErrorMessagesLocale message, final String arg) {
        super(message.toString());
        this.message =  message.toString();
        args = new Object[]{arg};
    }

    public UserServiceException(final ErrorMessagesLocale message, final Object[] argss) {
        super(message.toString());
        this.message =  message.toString();
        args = new Object[]{argss};
    }
}
