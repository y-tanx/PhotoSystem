package com.picture.common;

public class ResultMessage<T> {

    public static final Integer SUCCESS_CODE = 200;
    public static final Integer FAIL_CODE = 400;
    public static final String SUCCESS_MESSAGE = "success";
    public static final String FAIL_MESSAGE = "fail";

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 返回状态码
     */
    private Integer code;
    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    private ResultMessage() {

    }

    public static <T> ResultMessage<T> success() {
        ResultMessage<T> resultMessage = new ResultMessage<>();
        resultMessage.setCode(SUCCESS_CODE);
        resultMessage.setMessage(SUCCESS_MESSAGE);
        return resultMessage;
    }

    public static <T> ResultMessage<T> success(T data) {
        ResultMessage<T> resultMessage = success();
        resultMessage.setData(data);
        return resultMessage;
    }

    public static <T> ResultMessage<T> success(String message, T data) {
        ResultMessage<T> resultMessage = success();
        resultMessage.setMessage(message);
        resultMessage.setData(data);
        return resultMessage;
    }

    public static <T> ResultMessage<T> success(Integer code, String message, T data) {
        ResultMessage<T> resultMessage = new ResultMessage<>();
        resultMessage.setCode(code);
        resultMessage.setMessage(message);
        resultMessage.setData(data);
        return resultMessage;
    }

    public static <T> ResultMessage<T> fail() {
        ResultMessage<T> resultMessage = new ResultMessage<>();
        resultMessage.setCode(FAIL_CODE);
        resultMessage.setMessage(FAIL_MESSAGE);
        return resultMessage;
    }

    public static <T> ResultMessage<T> fail(T data) {
        ResultMessage<T> resultMessage = fail();
        resultMessage.setData(data);
        return resultMessage;
    }

    public static <T> ResultMessage<T> fail(String message, T data) {
        ResultMessage<T> resultMessage = fail();
        resultMessage.setMessage(message);
        resultMessage.setData(data);
        return resultMessage;
    }

    public static <T> ResultMessage<T> fail(Integer code, String message) {
        ResultMessage<T> resultMessage = fail();
        resultMessage.setCode(code);
        resultMessage.setMessage(message);
        return resultMessage;
    }

    public static <T> ResultMessage<T> fail(Integer code, String message, T data) {
        ResultMessage<T> resultMessage = new ResultMessage<>();
        resultMessage.setCode(code);
        resultMessage.setMessage(message);
        resultMessage.setData(data);
        return resultMessage;
    }
}