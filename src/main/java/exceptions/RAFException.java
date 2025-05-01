//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package exceptions;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;

/**
 * General purpose exception , when framework catch some exception but don't have special type for it than throw this one
 */
public class RAFException extends RuntimeException {

    private static final long serialVersionUID = 5696341877105038735L;

    public RAFException(Exception e, Class clazz) {
        super(e);
        if (!(e instanceof RAFException)) {
            LogManager.getLogger(clazz.getSimpleName()).error(ExceptionUtils.getMessage(e));
        }
    }

    public RAFException(String errorMsg, Class clazz) {
        super(errorMsg);
        LogManager.getLogger(clazz.getSimpleName()).error(errorMsg);
    }

}
