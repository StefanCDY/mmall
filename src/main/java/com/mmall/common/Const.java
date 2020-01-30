package com.mmall.common;

/**
 * create by Stefan on 2020-01-29
 */
public class Const {

    public static final String CURRENT_USER = "CURRENT_USER";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface Role {
        int CUSTOMER = 0;// 普通用户
        int ADMIN = 1;// 管理员
    }

}
