package com.application.fusamate.configuration;

public class Constants {
    // GHN API
    public static final String GHN_TOKEN = "531c855f-15f2-11ed-8636-7617f3863de9";
    public static final Integer SHOP_ID = 3153358;

    // COMMON CONFIG
    // public static final String BASE_URL_FRONTEND = "localhost:3000";
    public static final String BASE_URL_FRONTEND = "cms-fusamate.site";
//    public static final String BASE_URL_IMAGE = "http://fusamate.site/static/";
    public static final String BASE_URL_IMAGE = "/Users/abc/Downloads/Workspace/projectFPT/fusamate-be/src/main/resources/static/image/";

    public static final String SUFFIX_PASSWORD_DEFAULT = "@Fusamate2022";
    public static String MY_EMAIL = "hungdv21082002@gmail.com";
    public static String MY_PASSWORD = "alomnfohjmtscyvl";
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 3;
    //
    public static String DUPLICATED_USER = "DUPLICATED_USER";
    public static String DUPLICATED_PRODUCT_SET = "DUPLICATED_PRODUCT_SET";
    public static String DUPLICATED_BRAND = "DUPLICATED_BRAND";
    public static String DUPLICATED_CATEGORY = "DUPLICATED_CATEGORY";
    public static String DUPLICATED_COLOR = "DUPLICATED_COLOR";
    public static String DUPLICATED_MADE_IN = "DUPLICATED_MADE_IN";
    public static String DUPLICATED_SIZE = "DUPLICATED_SIZE";
    public static String DUPLICATED_PRODUCT = "DUPLICATED_PRODUCT";
    public static String DUPLICATED_PRODUCT_DETAIL = "DUPLICATED_PRODUCT_DETAIL";
    public static String DUPLICATED_PROMOTION_PRODUCT = "DUPLICATED_PROMOTION_PRODUCT";
    public static String DUPLICATED_PROMOTION_CATEGORY = "DUPLICATED_PROMOTION_CATEGORY";
    public static String DUPLICATED_USER_EMAIL = "DUPLICATED_USER_EMAIL";
    public static String DUPLICATED_USER_PHONE = "DUPLICATED_USER_PHONE";
    public static String DUPLICATED_USER_IDENTITY_CARD = "DUPLICATED_USER_IDENTITY_CARD";

    public static String NOT_AUTHORIZED = "NOT_AUTHORIZED";

    public static String USER_LOCKED = "USER_LOCKED";
    public static String USERNAME_OR_PASSWORD_NOT_MATCH = "USERNAME_OR_PASSWORD_NOT_MATCH";
    public static String SAME_OLD_PASSWORD = "SAME_OLD_PASSWORD";

    public static String WRONG_PASSWORD = "WRONG_PASSWORD";
    public static String INVALID_EMAIL = "INVALID_EMAIL";
    public static String BAD_REQUEST = "BAD_REQUEST";
    // REGEX
    public final static String REGEX_PHONE = "^(0|84)(2(0[3-9]|1[0-6|8|9]|2[0-2|5-9]|3[2-9]|4[0-9]|5[1|2|4-9]|6[0-3|9]|7[0-7]|8[0-9]|9[0-4|6|7|9])|3[2-9]|5[5|6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])([0-9]{7})$";
    public final static String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

}

