package com.javaweb.repository;

import com.javaweb.entity.BuildingEntity;
import com.javaweb.repository.custom.BuildingRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface BuildingRepository extends JpaRepository<BuildingEntity,Long>, BuildingRepositoryCustom {
    void deleteByIdIn(List<Long> ids);

    /** Trang chủ: lấy tất cả tòa nhà, mới nhất trước. */
    List<BuildingEntity> findAllByOrderByIdDesc();

    /** Trang chủ: lọc theo danh sách mã quận (truy vấn có tham số, không nối chuỗi SQL). */
    List<BuildingEntity> findByDistrictInOrderByIdDesc(Collection<String> districts);
}
