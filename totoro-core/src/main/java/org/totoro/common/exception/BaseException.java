package org.totoro.common.exception;


/**
 * 异常类基类
 * @author ChangLF 2023-05-16
 */
public class BaseException extends RuntimeException {

    private String code;

    public BaseException(String code, String desc) {
        super(desc);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
