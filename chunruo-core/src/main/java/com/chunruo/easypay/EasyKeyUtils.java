package com.chunruo.easypay;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 生成环境
 * @author chunruo
 */
public class EasyKeyUtils {
    public static final String DEFAULT_PARTNER = "900029000022756";
    public static final String MERCHANT_PRIVATE_KEY="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJ0BQGGc+YPK+EhDfqKPDPI7mq/28iZq2lUuufqU+ZUdEKet2xUHGx+KTAxAjM7A2I1Q4TRLG9nDo99A2ffOvwOAMH1X05XzorsYPsAyu7d7eTDlZ4yOQAylP7OJHMBxORNaQ8+U8WBzxrC57odLFLkOM4Ca6p4y7c3/WH1m8KOHAgMBAAECgYAOImq1XvhnkQJBHzJrWA2GUS9f4A90vfHh8U707Cx77B3vuosanYUnlb+66qTCiEH2lu1vU8OyGxbJpoD4+jff6HNtLCXm+RUx34mTUSqmxVmza7siiG0/69Yf8fx50lTRqkQgeoH4h7i6MompGIbh91zxgvYSJf0qIN/hmo5ZYQJBAOHtDRTD9j39Tdhln7xdDKw4KG6gPmEVDQDuyw73PGPJrF+Tz3XdIxFCaLva1d5i5yt0mz4D9REDowkx0JhaGFUCQQCx544h84ue5xPVp11vsGqXsdArK1nS7TJGKHpviokjMGZ/jmJqTCrqAxHftdA5Jf8WVxHMWB7NmD4jXOQ9fphrAkAmT1UdhVE3F8HghPL+NOUWOvuYLrIFMlWfJ97k7cWewi7pkh3mxZXRsmoiGKyVZj3+32oHrRIcTXqS75CIlRZ1AkAuiSqFuQZBp6JpleD8EUKgsZOJZ0qUwRUQgxAb0zoyKTv2i9E0iv4CvpDTWuS/vi+usVHniPltwDAo4eiWKWJHAkAjEdbZIIaKYqca1f+HdUMUw4jCG3w37FlAqDDNvj6Jg5+kIjGd2VsEv/LDqpLP4XFPExTdEGtH38Q2S2FgV8kW";
    public static final String EASYPAY_PUBLIC_KEY="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC2WTfvas1JvvaRuJWIKmKlBLmkRvr2O7Fu3k/zvhJs+X1JQorPWq/yZduY6HKu0up7Qi3T6ULHWyKBS1nRqhhHpmLHnI3sIO8E/RzNXJiTd9/bpXMv+H8F8DW5ElLxCIVuwHBROkBLWS9fIpslkFPt+r13oKFnuWhXgRr+K/YkJQIDAQAB";
    public static final String DEFAULT_MERCHANT_ID = "900029000022756";
    public static final String SC_CUSTOM_URL = "https://crossborder.bhecard.com/api/order/import";
    public static final String SC_CUSTOM_QUERY_URL = "https://crossborder.bhecard.com/api/order/query";
    public static final String SC_URL = "https://newpay.bhecard.com/api_gateway.do";
    public static final String SC_DES_ENCODE_KEY = "yZjNvjK3eE33MQulPuygKxh1";
    public static final String DEFAULT_ENCODE_TYPE = "RSA";
    public static final String DEFAULT_CHARSET = "UTF-8";

    public static String getSign(String key, String charset, String bizContent) throws Exception {
        return AlipaySignature.rsaSign(bizContent, key, charset);
    }

    public static String getOutTradeNo(){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(d) +  System.currentTimeMillis();
    }
}
