
package com.legenda.lee.studydrools2;


/**
 * @author Legenda-Lee
 * @date 2019-12-27 14:31:35
 * @description
 */
public class CommonUtil {

    public static boolean isContainsStr(String str, Object chilStr) {
        if (str != null && chilStr != null) {
            return str.contains(chilStr.toString());
        } else {
            return false;
        }
    }

}
