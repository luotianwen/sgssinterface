package com.sgss.www.conmon;

import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.upload.UploadFile;

import java.io.File;

public class BaseController extends Controller {

    public String getFilePath(String coverImgSrc, UploadFile uf){
        System.out.println(coverImgSrc);
        File tmpFile = new File(PropKit.get("userfiles.basedir") +coverImgSrc);
        if(!tmpFile.exists()){
            tmpFile.mkdirs();
        }

        File file = new File(PropKit.get("userfiles.basedir") +coverImgSrc+ uf.getFileName());
        if(file.exists()){
            String path = file.getPath();
            /** 下面的while（）判断文件不存在退出 */
            while (file.exists()) {
                String si[] = path.split("\\.");
                path = "";
                /**
                 * si[si.length-2]=si[si.length-2]+"_1." *
                 * 给重复增加文件名，因为文件切割时不保存“ . ”，所以还要加上
                 */
                si[si.length - 2] = si[si.length - 2] + "_1.";
                for (int i = 0; i < si.length; i++) {
                    path = path + si[i];
                }
                /** file2=new File(path); * 重新创建对象 */
                file = new File(path);
            }
        }
        coverImgSrc=coverImgSrc+file.getName();
        uf.getFile().renameTo(file);
        System.out.println(coverImgSrc);
        return coverImgSrc;
    }
}
