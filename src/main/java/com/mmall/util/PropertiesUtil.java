package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * create by Stefan on 2020-01-30
 */
public class PropertiesUtil {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    private static Properties properties = new Properties();

    static {
        String fileName = "mmall.properties";
        try {
            properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("配置文件读取异常", e);
        }
    }

    public static String getProperty(String key) {
        String property = properties.getProperty(key);
        if (StringUtils.isBlank(property)) {
            return null;
        }
        return property.trim();
    }

    public static String getProperty(String key, String defaultValue) {
        String property = properties.getProperty(key);
        if (StringUtils.isBlank(property)) {
            property = defaultValue;
        }
        return property.trim();
    }
}
