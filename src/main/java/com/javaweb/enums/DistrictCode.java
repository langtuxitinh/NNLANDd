package com.javaweb.enums;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public enum DistrictCode {
    // ---- TP. Hồ Chí Minh ----
    QUAN_1("Quận 1", ProvinceCode.HCM),
    QUAN_3("Quận 3", ProvinceCode.HCM),
    QUAN_4("Quận 4", ProvinceCode.HCM),
    QUAN_5("Quận 5", ProvinceCode.HCM),
    QUAN_6("Quận 6", ProvinceCode.HCM),
    QUAN_7("Quận 7", ProvinceCode.HCM),
    QUAN_8("Quận 8", ProvinceCode.HCM),
    QUAN_PN("Quận Phú Nhuận", ProvinceCode.HCM),
    QUAN_BTH("Quận Bình Thạnh", ProvinceCode.HCM),
    QUAN_TB("Quận Tân Bình", ProvinceCode.HCM),
    QUAN_GV("Quận Gò Vấp", ProvinceCode.HCM),
    TP_THU_DUC("TP. Thủ Đức", ProvinceCode.HCM),

    // ---- Hà Nội ----
    HN_BA_DINH("Quận Ba Đình", ProvinceCode.HN),
    HN_HOAN_KIEM("Quận Hoàn Kiếm", ProvinceCode.HN),
    HN_HAI_BA_TRUNG("Quận Hai Bà Trưng", ProvinceCode.HN),
    HN_DONG_DA("Quận Đống Đa", ProvinceCode.HN),
    HN_CAU_GIAY("Quận Cầu Giấy", ProvinceCode.HN),
    HN_THANH_XUAN("Quận Thanh Xuân", ProvinceCode.HN),
    HN_TAY_HO("Quận Tây Hồ", ProvinceCode.HN),
    HN_NAM_TU_LIEM("Quận Nam Từ Liêm", ProvinceCode.HN);

    private final String districtName;
    private final ProvinceCode province;

    DistrictCode(String districtName, ProvinceCode province) {
        this.districtName = districtName;
        this.province = province;
    }

    public String getDistrictName() {
        return districtName;
    }

    public ProvinceCode getProvince() {
        return province;
    }

    /** Toàn bộ quận/huyện (giữ nguyên hành vi cũ, dùng cho trang admin). */
    public static Map<String, String> getDistrict() {
        Map<String, String> map = new TreeMap<>();
        for (DistrictCode x : DistrictCode.values()) {
            map.put(x.toString(), x.getDistrictName());
        }
        return map;
    }

    /** Quận/huyện thuộc một tỉnh/thành, theo thứ tự khai báo. */
    public static Map<String, String> getDistrictByProvince(String provinceCode) {
        Map<String, String> map = new LinkedHashMap<>();
        for (DistrictCode x : DistrictCode.values()) {
            if (x.getProvince().name().equals(provinceCode)) {
                map.put(x.name(), x.getDistrictName());
            }
        }
        return map;
    }

    /** Mã các quận/huyện thuộc một tỉnh/thành. */
    public static List<String> getCodesByProvince(String provinceCode) {
        return new ArrayList<>(getDistrictByProvince(provinceCode).keySet());
    }

    /** Tìm quận theo mã, không có thì trả về null. */
    public static DistrictCode fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (DistrictCode x : DistrictCode.values()) {
            if (x.name().equals(code)) {
                return x;
            }
        }
        return null;
    }
}
