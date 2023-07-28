package org.totoro.generator.javabean.dto;


import java.io.Serializable;
import java.util.Objects;

/**
 * 返回实体包装类
 *
 * @author ChangLF 2023-05-16
 */
public class BaseRespDTO<V> implements Serializable {

    private static final long serialVersionUID = -1112042902806267142L;

    /**
     * 请求成功的编码
     */
    protected static final String successCode = "0";

    /**
     * 编码
     */
    protected String code;

    /**
     * 信息
     */
    protected String msg;

    /**
     * 返回的内容
     */
    protected V data;


    public BaseRespDTO() {
        this.code = successCode;
        this.msg = "ok";
    }

    public BaseRespDTO(V data) {
        this.code = successCode;
        this.msg = "ok";
        this.data = data;
    }

    /**
     * 获取接口请求是否成功标志
     * @author ChangLF 2023/6/7 10:16
     * @return java.lang.Boolean true是成功
     **/
    public Boolean flag() {
        return successCode.equals(this.code);
    }

    public static <V> BaseRespDTO<V> success(V data) {
        return new BaseRespDTO<>(data);
    }

    public static BaseRespDTO<Void> success() {
        return new BaseRespDTO<>();
    }

    public static <V> BaseRespDTO<V> error(String code, String msg) {
        BaseRespDTO<V> resDTO = new BaseRespDTO<>();
        resDTO.setCode(code);
        resDTO.setMsg(msg);
        return resDTO;
    }

    public static <V> BaseRespDTO<V> error(Integer code, String msg) {
        BaseRespDTO<V> resDTO = new BaseRespDTO<>();
        resDTO.setCode(String.valueOf(code));
        resDTO.setMsg(msg);
        return resDTO;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public V getData() {
        return data;
    }

    public void setData(V data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseRespDTO<?> that = (BaseRespDTO<?>) o;
        return Objects.equals(code, that.code) && Objects.equals(msg, that.msg) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, msg, data);
    }
}
