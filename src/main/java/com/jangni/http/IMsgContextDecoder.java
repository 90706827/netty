package com.jangni.http;

/**
 * Author ZhangGuoQiang
 * Date: 2018/6/28/028
 * Time: 19:09
 * Description:
 */
public interface IMsgContextDecoder {

    public boolean decode(String msg,MsgContext msgContext);
}
