package com.sgss.www.conmon;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileUtils {
    public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        File fileParent = dest.getParentFile();
        if(!fileParent.exists()){
            fileParent.mkdirs();
        }
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }
    public static int copy(InputStream in, OutputStream out) throws IOException {


        int var2;
        try {
            var2 =copy2(in, out);
        } finally {
            try {
                in.close();
            } catch (IOException var12) {
                ;
            }

            try {
                out.close();
            } catch (IOException var11) {
                ;
            }

        }

        return var2;
    }
    public static int copy2(InputStream in, OutputStream out) throws IOException {

        int byteCount = 0;
        byte[] buffer = new byte[4096];

        int bytesRead;
        for(boolean var4 = true; (bytesRead = in.read(buffer)) != -1; byteCount += bytesRead) {
            out.write(buffer, 0, bytesRead);
        }

        out.flush();
        return byteCount;
    }
    public static String decode(String source, String encoding) throws UnsupportedEncodingException {

        int length = source.length();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
        boolean changed = false;

        for(int i = 0; i < length; ++i) {
            int ch = source.charAt(i);
            if (ch == '%') {
                if (i + 2 >= length) {
                    throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                }

                char hex1 = source.charAt(i + 1);
                char hex2 = source.charAt(i + 2);
                int u = Character.digit(hex1, 16);
                int l = Character.digit(hex2, 16);
                if (u == -1 || l == -1) {
                    throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                }

                bos.write((char)((u << 4) + l));
                i += 2;
                changed = true;
            } else {
                bos.write(ch);
            }
        }

        return changed ? new String(bos.toByteArray(), encoding) : source;
    }
}
