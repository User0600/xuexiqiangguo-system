package org.example.new2.entity;

import lombok.Data;

@Data
public class ResponseMessage<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ResponseMessage<T> success(T data) {
        ResponseMessage<T> response = new ResponseMessage<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> ResponseMessage<T> error(String message) {
        ResponseMessage<T> response = new ResponseMessage<>();
        response.setCode(400);
        response.setMessage(message);
        response.setData(null);
        return response;
    }

    public static <T> ResponseMessage<T> unauthorized(String message) {
        ResponseMessage<T> response = new ResponseMessage<>();
        response.setCode(401);
        response.setMessage(message);
        response.setData(null);
        return response;
    }
}