package com.javaweb.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum ProvinceCode {
    HN("Hà Nội"),
    HCM("TP. Hồ Chí Minh");

    private final String provinceName;

    ProvinceCode(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    /** Danh sách tỉnh/thành theo đúng thứ tự khai báo (dùng cho ô chọn ở trang chủ). */
    public static Map<String, String> getProvinces() {
        Map<String, String> map = new LinkedHashMap<>();
        for (ProvinceCode item : ProvinceCode.values()) {
            map.put(item.name(), item.getProvinceName());
        }
        return map;
    }
}
