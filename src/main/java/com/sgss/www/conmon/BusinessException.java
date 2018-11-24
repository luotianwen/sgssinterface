package com.sgss.www.conmon;

public class BusinessException extends RuntimeException {
    private final String errMsg;
    public BusinessException(String s) {
        super(s);
        this.errMsg=s;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
