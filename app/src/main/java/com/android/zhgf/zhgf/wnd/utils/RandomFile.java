package com.android.zhgf.zhgf.wnd.utils;/**
 * Created by Administrator on 2016/5/3.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

/*******************************************************************************************************
 * Class Name:WndProject - RandomFile
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title RandomFile
 * @Package com.jiechu.wnd.utils
 * @date 2016/5/3 14:48
 * @Description
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class RandomFile {
    public String GetDirByDate(){
        return (new SimpleDateFormat("yyyyMMdd")).format(new Date()).toString();
    }
    public String GetFileNameByDateAndRndNum(){
        return (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()).toString();
    }


    public int rand(int n)
    {
        int ans = 0;
        while(Math.log10(ans)+1<n)
            ans = (int)(Math.random()*Math.pow(10, n));
        return ans;
    }

    public String getFileExtName(String strFileName){
        if ((strFileName != null) && (strFileName.length() > 0)) {
            int dot = strFileName.lastIndexOf('.');
            if ((dot >-1) && (dot < (strFileName.length()))) {
                return strFileName.substring(dot+1, strFileName.length());
            }
        }
        return "default";
    }
}
