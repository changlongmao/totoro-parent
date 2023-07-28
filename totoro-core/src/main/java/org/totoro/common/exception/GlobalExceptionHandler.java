package org.totoro.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.totoro.common.javabean.dto.BaseRespDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.StringJoiner;

/**
 * 全局异常拦截器
 * @author changlf 2023-05-16
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public BaseRespDTO<Void> handlerNoFoundException(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return BaseRespDTO.error(HttpStatus.NOT_FOUND.value(), "路径不存在，请检查路径是否正确");
    }

    /**
     * HttpRequestMethodNotSupportedException 异常处理
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public BaseRespDTO<Void> httpRequestMethodNotSupportedExceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return BaseRespDTO.error(HttpStatus.METHOD_NOT_ALLOWED.value(), "请求方法不支持");
    }

    /**
     * HttpMediaTypeNotSupportedException异常处理(http请求媒体类型不支持)
     */
    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    public BaseRespDTO<Void> httpMediaTypeNotSupportedExceptionHandler(HttpMediaTypeNotSupportedException e) {
        log.error(e.getMessage(), e);
        return BaseRespDTO.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "http请求媒体类型错误");
    }

    /**
     * HttpMessageNotReadableException异常处理(http 请求body为空)
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public BaseRespDTO<Void> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return BaseRespDTO.error(HttpStatus.BAD_REQUEST.value(), "http请求body为空或参数解析错误");
    }

    /**
     * 请求Body内字段没有通过注解校验（通过参数级@Valid 启用的参数校验）
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public BaseRespDTO<Void> exceptionHandler(MethodArgumentNotValidException e) {
        StringJoiner msg = new StringJoiner("；", "请求参数不合法：", "。");
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            msg.add(fieldError.getDefaultMessage());
        }

        String errMsg = msg.toString();
        log.error(errMsg);
        return BaseRespDTO.error(HttpStatus.BAD_REQUEST.value(), errMsg);
    }

    /**
     * BaseException异常处理
     */
    @ExceptionHandler(value = BaseException.class)
    public BaseRespDTO<Void> handleBaseException(BaseException e) {
        log.error("业务异常：", e);
        return BaseRespDTO.error(e);
    }

    /**
     * 全局异常处理
     */
    @ExceptionHandler(value = Throwable.class)
    public BaseRespDTO<Void> handleThrowable(Throwable e) {
        log.error("Report Throwable", e);
        return BaseRespDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "操作失败，网络开小差了！");
    }

}
