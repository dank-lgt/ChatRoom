package com.example.fachat.common;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


public class PasswordTool {
    /**
     * 加盐加密
     *
     * @param password 明文密码
     * @return 加盐加密的密码
     */
    public static String encrypt(String password){
        //1.产生盐值
        String salt = UUID.randomUUID().toString().replace("-","");
        //2.将盐值和加密后的密码共同返回（合并盐值和密码）
        String finalPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes(StandardCharsets.UTF_8));
        String dbpassword = salt + "$" + finalPassword;
        return  dbpassword;
    }

    public static String encrypt(String password,String salt){
        String finalPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes(StandardCharsets.UTF_8));
        String dbpassword = salt + "$" + finalPassword;
        return  dbpassword;
    }


    /**
     * 验证加盐加密密码
     *
     * @param password   明文密码（不一定对，需要验证明文密码）
     * @param dbPassword 数据库存储的密码（包含：salt+$+加盐加密密码）
     * @return true=密码正确
     */

    public static boolean decrypt(String password, String dbPassword){
        boolean result = false;
        if (StringUtils.hasLength(password) && StringUtils.hasLength(dbPassword)
            && dbPassword.length() == 65 &&dbPassword.contains("$")){
            String[] PwdArr = dbPassword.split("\\$");

            String salt = PwdArr[0];

            String finalPwd = PwdArr[1];

            String checkPwd = PasswordTool.encrypt(password,salt);
            if (dbPassword.equals(checkPwd)){
                return  true;
            }
        }

        return result;
    }
    public static void main(String[] args) {
        String password = "123";
        String dbPwd = PasswordTool.encrypt(password);
        String dbPwd2 = PasswordTool.encrypt(password);
        String dbPwd3 = PasswordTool.encrypt(password);

        System.out.println(dbPwd);
        System.out.println(dbPwd2);
        System.out.println(dbPwd3);

        String cPwd = "12345";
        boolean result = PasswordTool.decrypt(cPwd, dbPwd2);
        System.out.println("错误对比结果 -> " + result);


        boolean result2 = PasswordTool.decrypt(password, dbPwd2);
        System.out.println("正确对比结果 -> " + result2);
    }

}
