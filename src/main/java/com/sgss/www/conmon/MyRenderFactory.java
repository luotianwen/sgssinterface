package com.sgss.www.conmon;

import com.sgss.www.swagger.model.ErrorCode;
import com.jfinal.render.JsonRender;
import com.jfinal.render.Render;
import com.jfinal.render.RenderFactory;

/**
 * @author martins
 */
public class MyRenderFactory extends RenderFactory {
    @Override
    public Render getErrorRender(int errorCode) {
        ReqResponse respBody = new ReqResponse();
        respBody.setCode(1);

        if (ErrorCode.ERROR500==errorCode) {
            respBody.setMsg("系统错误");
            return new JsonRender(respBody);
        }
        else if (ErrorCode.ERROR404==errorCode ) {
            respBody.setMsg("未找到页面");
            return new JsonRender(respBody);
        }
        else if (ErrorCode.ERROR400==errorCode) {
            respBody.setMsg("系统错误");
            return new JsonRender(respBody);
        }
        else if (ErrorCode.ERROR401==errorCode) {
            respBody.setMsg("系统错误");
            return new JsonRender(respBody);
        }
        else if (ErrorCode.ERROR403==errorCode) {
            respBody.setMsg("系统错误");
            return new JsonRender(respBody);
        }

        return super.getErrorRender(errorCode);
    }
}
