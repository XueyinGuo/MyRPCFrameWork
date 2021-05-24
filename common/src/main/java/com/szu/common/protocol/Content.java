package com.szu.common.protocol;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/23 22:37
 */

import java.io.Serializable;

public class Content implements Serializable {

    Class<?>  clazz;
    String methodName;
    Class<?>[] paramTypes;
    Object[] args;
    Object result;

    public Content(Object result) {
        this.result = result;
    }

    public Content(Class clazz, String methodName, Class<?>[] paramTypes, Object[] args) {
        this.clazz = clazz;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.args = args;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClassName(Class<?>  clazz) {
        this.clazz = clazz;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
