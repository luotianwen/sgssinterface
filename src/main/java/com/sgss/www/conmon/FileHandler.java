package com.sgss.www.conmon;

import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author martins
 */
public class FileHandler  extends Handler {


    @Override
    public void handle(String target, HttpServletRequest req, HttpServletResponse res, boolean[] isHandled) {
        String userfiles="/userfiles/";
        if (target.startsWith(userfiles)) {
            return ;
        }
        next.handle(target, req, res, isHandled);
    }
}
