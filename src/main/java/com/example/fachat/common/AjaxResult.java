package com.example.fachat.common;

import lombok.Data;

import java.io.Serializable;


/***
 *
 *   统一返回对象json
 */

@Data
public class AjaxResult implements Serializable {
    private int code;
    private String msg;
    private Object data;


    /**
     *
     *返回成功
     * @param data
     * @return
     */
    public static AjaxResult success(Object data){
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(200);
        ajaxResult.setMsg("");
        ajaxResult.setData(data);
        return ajaxResult;
    }

    public static AjaxResult success(Object data,String msg){
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(200);
        ajaxResult.setMsg(msg);
        ajaxResult.setData(data);
        return ajaxResult;
    }


    /**
     * 返回失败
     * @param code
     * @param msg
     * @return
     */
    public static AjaxResult fail(int code,String msg){
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(code);
        ajaxResult.setMsg(msg);
        ajaxResult.setData("");
        return ajaxResult;
    }

    public static AjaxResult fail(int code,String msg,Object data){
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(code);
        ajaxResult.setMsg(msg);
        ajaxResult.setData(data);
        return ajaxResult;
    }
}
