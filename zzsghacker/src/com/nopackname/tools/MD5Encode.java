package com.nopackname.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encode {

    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
            "e", "f" };

    /*
     * 信息摘要是安全的单向哈希函数，它接收任意大小的数据，输出固定长度的哈希值。
     */
    public MD5Encode() {
    }

    private static MessageDigest md = null;

    private static MessageDigest getDigest() {
        if (null == md) {
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return md;
    }

    public static String update(String pwd) {
        if (pwd == null) {
            return "";
        }
        // 创建具有指定算法名称的信息摘要
        MessageDigest md = getDigest();
        // 使用指定的字节更新摘要
        md.update(pwd.getBytes());
        // 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
        return byteArrayToHexString(md.digest());
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static void main(String[] args) {
        String s0 = "jsonp1410599368611";
        System.out.println(MD5Encode.update(s0));
    }
}
