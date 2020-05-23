package com.chunruo.easypay;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

/**
 * @Author: Will
 * @Date: 2018/10/18 15:31
 * @Description: 签名工具类
 */
public class ColumnSortHelper<T> {
    /**
     * 返回对象排序后的map的形式
     *
     * @param target
     * @return
     */
    public Map<String, Object> getKVPairs(T target) {
        Map<String, Object> pairs = new HashMap<String, Object>();
        if (target == null) {
            return null;
        }
        Method[] methods = target.getClass().getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("get") && method.getParameterTypes().length == 0
                    && !name.substring("get".length()).toUpperCase().equals("CLASS")
                    && !name.substring("get".length()).toUpperCase().equals("SIGN")
                    && !name.substring("get".length()).toUpperCase().equals("RANDOMKEY")
                    && !name.substring("get".length()).toUpperCase().equals("DATA")) {
                String key = name.substring("get".length()).toLowerCase();
                Object value = null;
                try {
                    value = method.invoke(target);
                } catch (IllegalAccessException e) {
                    //ignore
                } catch (InvocationTargetException e) {
                    //ignore
                }
                if (value != null && !value.equals(""))
                    pairs.put(key, value);
            }
        }
        Map<String, Object> sort = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        sort.putAll(pairs);
        return sort;
    }

    /**
     * 获取待价签字符串
     */
    public String generateSignSource(T target) {
        StringBuffer sb = new StringBuffer();
        Map<String, Object> sortMap = this.getKVPairs(target);
        for (Map.Entry<String, Object> entry : sortMap.entrySet()) {
            System.out.println();
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        return StringUtils.substring(sb.toString(), 0, sb.length() - 1);
    }

}
