package com.huyn.demogroup.bahavior.widget;

/**
 * Created by huyaonan on 2017/12/18.
 */

public class SysoutUtil {

    public static void sysout(String... values) {
        StringBuffer sb = new StringBuffer();
        sb.append("++++");
        for(String s : values)
            sb.append(s).append("__");
        System.out.println(sb.toString());
    }

}
