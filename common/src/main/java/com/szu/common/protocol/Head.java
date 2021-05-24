package com.szu.common.protocol;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 *
 *
 *
 * @Date 2021/5/23 22:51
 */

import com.szu.common.enmu.CodeEnum;

import java.io.Serializable;
import java.util.Random;

public class Head implements Serializable {

    static Random random;

    static {
        random = new Random();
    }

    CodeEnum code;
    long id;
    int  dataLength;

    public Head(CodeEnum code, int dataLength) {
        this.code = code;
        this.id = random.nextLong();
        this.dataLength = dataLength;
    }

    public CodeEnum getCode() {
        return code;
    }

    public void setCode(CodeEnum code) {
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

}
