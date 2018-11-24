package com.sgss.www.conmon;

import com.jfinal.kit.PropKit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 查看CK上传的图片
 * @author ThinkGem
 * @version 2014-06-25
 */
public class UserfilesDownloadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 上传文件基础虚拟路径
     */
    public static final String USERFILES_BASE_URL = "/userfiles/";

    public void fileOutputStream(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String filepath = req.getRequestURI();
        int index = filepath.indexOf(USERFILES_BASE_URL);
        if(index >= 0) {
            filepath = filepath.substring(index +USERFILES_BASE_URL.length());
        }
        try {
            filepath = FileUtils.decode(filepath, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }
        File file = new File(PropKit.get("userfiles.basedir") + USERFILES_BASE_URL + filepath);
        try {
            FileUtils.copy(new FileInputStream(file), resp.getOutputStream());
            resp.setHeader("Content-Type", "application/octet-stream");
            return;
        } catch (FileNotFoundException e) {
            req.setAttribute("exception", new FileNotFoundException("请求的文件不存在"));
            req.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(req, resp);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        fileOutputStream(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        fileOutputStream(req, resp);
    }
}