package org.totoro.common.exception;

/**
 * @author ChangLF 2023/08/15
 */
public class AuthException extends BaseException {

    public AuthException(String code, String desc) {
        super(code, desc);
    }
}
