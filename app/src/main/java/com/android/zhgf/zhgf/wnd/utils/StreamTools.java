package com.android.zhgf.zhgf.wnd.utils;/**
 * Created by Administrator on 2016/4/28.
 */

/*******************************************************************************************************
 * Class Name:WndProject - StreamTools
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title StreamTools
 * @Package com.jiechu.wnd.utils
 * @date 2016/4/28 10:24
 * @Description 流工具
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.InputStream;

public class StreamTools {


    public static byte[] isToData(InputStream is) throws IOException{
        // 字节输出流
        ByteArrayOutputStream bops = new ByteArrayOutputStream();
        // 读取数据的缓存区
        byte buffer[] = new byte[1024];
        // 读取长度的记录
        int len = 0;
        // 循环读取
        while ((len = is.read(buffer)) != -1) {
            bops.write(buffer, 0, len);
        }
        // 把读取的内容转换成byte数组
        byte data[] = bops.toByteArray();

        bops.flush();
        bops.close();
        is.close();
        return data;
    }
}