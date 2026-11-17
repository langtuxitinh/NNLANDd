-- =====================================================================
--  estateadvance.sql  -  Schema + du lieu mau cho du an Manage-Estate-Project
--  Tuong thich MySQL 5.7 / 8.x
--  Cach dung:  mo MySQL Workbench / phpMyAdmin / dong lenh  ->  chay toan bo file nay
--  Sau do mo project trong NetBeans va Run (application.properties giu nguyen:
--  localhost:3306/estateadvance, root / 123456)
--
--  TAI KHOAN DANG NHAP (mat khau tat ca: 123456)
--     manager   -> role MANAGER  (vao trang /admin/home)
--     staff1    -> role STAFF    (vao trang /admin/home)
--     staff2    -> role STAFF
--     user1     -> role USER     (vao trang /trang-chu)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `estateadvance`
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `estateadvance`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `assignmentbuilding`;
DROP TABLE IF EXISTS `assignmentcustomer`;
DROP TABLE IF EXISTS `user_role`;
DROP TABLE IF EXISTS `rentarea`;
DROP TABLE IF EXISTS `transaction`;
DROP TABLE IF EXISTS `building`;
DROP TABLE IF EXISTS `customer`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `role`;

-- ---------------------------------------------------------------------
--  ROLE
-- ---------------------------------------------------------------------
CREATE TABLE `role` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT,
  `name`         VARCHAR(255) NULL,
  `code`         VARCHAR(255) NULL,
  `createddate`  DATETIME     NULL,
  `createdby`    VARCHAR(255) NULL,
  `modifieddate` DATETIME     NULL,
  `modifiedby`   VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
--  USER
-- ---------------------------------------------------------------------
CREATE TABLE `user` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT,
  `username`     VARCHAR(150) NOT NULL,
  `password`     VARCHAR(255) NOT NULL,
  `fullname`     VARCHAR(255) NOT NULL,
  `status`       INT          NOT NULL DEFAULT 1,
  `email`        VARCHAR(255) NULL,
  `createddate`  DATETIME     NULL,
  `createdby`    VARCHAR(255) NULL,
  `modifieddate` DATETIME     NULL,
  `modifiedby`   VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  UNIQUE KEY `uk_user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
--  USER_ROLE
-- ---------------------------------------------------------------------
CREATE TABLE `user_role` (
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
--  BUILDING
-- ---------------------------------------------------------------------
CREATE TABLE `building` (
  `id`                   BIGINT       NOT NULL AUTO_INCREMENT,
  `name`                 VARCHAR(255) NULL,
  `street`               VARCHAR(255) NULL,
  `ward`                 VARCHAR(255) NULL,
  `district`             VARCHAR(100) NULL,
  `structure`            VARCHAR(255) NULL,
  `numberofbasement`     INT          NULL,
  `floorarea`            INT          NULL,
  `direction`            VARCHAR(100) NULL,
  `level`                VARCHAR(100) NULL,
  `rentprice`            INT          NULL,
  `rentpricedescription` TEXT         NULL,
  `servicefee`           VARCHAR(255) NULL,
  `carfee`               VARCHAR(255) NULL,
  `motofee`              VARCHAR(255) NULL,
  `overtimefee`          VARCHAR(255) NULL,
  `waterfee`             VARCHAR(255) NULL,
  `electricityfee`       VARCHAR(255) NULL,
  `deposit`              VARCHAR(255) NULL,
  `payment`              VARCHAR(255) NULL,
  `renttime`             VARCHAR(255) NULL,
  `decorationtime`       VARCHAR(255) NULL,
  `brokeragefee`         INT          NULL,
  `type`                 VARCHAR(255) NULL,
  `note`                 TEXT         NULL,
  `linkofbuilding`       VARCHAR(500) NULL,
  `map`                  VARCHAR(500) NULL,
  `avatar`               VARCHAR(500) NULL,
  `managername`          VARCHAR(255) NULL,
  `managerphone`         VARCHAR(50)  NULL,
  `image`                VARCHAR(500) NULL,
  `createddate`          DATETIME     NULL,
  `createdby`            VARCHAR(255) NULL,
  `modifieddate`         DATETIME     NULL,
  `modifiedby`           VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
--  RENTAREA
-- ---------------------------------------------------------------------
CREATE TABLE `rentarea` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT,
  `value`        INT          NULL,
  `buildingid`   BIGINT       NULL,
  `createddate`  DATETIME     NULL,
  `createdby`    VARCHAR(255) NULL,
  `modifieddate` DATETIME     NULL,
  `modifiedby`   VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_rentarea_building` (`buildingid`),
  CONSTRAINT `fk_rentarea_building` FOREIGN KEY (`buildingid`) REFERENCES `building` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
--  ASSIGNMENTBUILDING  (tòa nhà <-> nhân viên phụ trách)
-- ---------------------------------------------------------------------
CREATE TABLE `assignmentbuilding` (
  `buildingid` BIGINT NOT NULL,
  `staffid`    BIGINT NOT NULL,
  PRIMARY KEY (`buildingid`, `staffid`),
  CONSTRAINT `fk_ab_building` FOREIGN KEY (`buildingid`) REFERENCES `building` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ab_staff`    FOREIGN KEY (`staffid`)    REFERENCES `user` (`id`)     ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
--  CUSTOMER
-- ---------------------------------------------------------------------
CREATE TABLE `customer` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT,
  `fullname`     VARCHAR(255) NULL,
  `phone`        VARCHAR(50)  NULL,
  `email`        VARCHAR(255) NULL,
  `demand`       TEXT         NULL,
  `status`       VARCHAR(100) NULL,
  `is_active`    TINYINT(1)   NOT NULL DEFAULT 1,
  `companyname`  VARCHAR(255) NULL,
  `createddate`  DATETIME     NULL,
  `createdby`    VARCHAR(255) NULL,
  `modifieddate` DATETIME     NULL,
  `modifiedby`   VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
--  ASSIGNMENTCUSTOMER  (khách hàng <-> nhân viên phụ trách)
-- ---------------------------------------------------------------------
CREATE TABLE `assignmentcustomer` (
  `customerid` BIGINT NOT NULL,
  `staffid`    BIGINT NOT NULL,
  PRIMARY KEY (`customerid`, `staffid`),
  CONSTRAINT `fk_ac_customer` FOREIGN KEY (`customerid`) REFERENCES `customer` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ac_staff`    FOREIGN KEY (`staffid`)    REFERENCES `user` (`id`)     ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
--  TRANSACTION  (lịch sử chăm sóc khách hàng)
-- ---------------------------------------------------------------------
CREATE TABLE `transaction` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT,
  `code`         VARCHAR(50)  NULL,
  `note`         TEXT         NULL,
  `staffid`      BIGINT       NULL,
  `customerid`   BIGINT       NULL,
  `createddate`  DATETIME     NULL,
  `createdby`    VARCHAR(255) NULL,
  `modifieddate` DATETIME     NULL,
  `modifiedby`   VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_transaction_customer` (`customerid`),
  CONSTRAINT `fk_transaction_customer` FOREIGN KEY (`customerid`) REFERENCES `customer` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
--  DU LIEU MAU
-- =====================================================================

-- Roles (code phai la MANAGER / STAFF / USER)
INSERT INTO `role` (`id`, `name`, `code`, `createddate`, `createdby`) VALUES
  (1, 'Quản lý',   'MANAGER', NOW(), 'system'),
  (2, 'Nhân viên', 'STAFF',   NOW(), 'system'),
  (3, 'Người dùng','USER',    NOW(), 'system');

-- Users  (mat khau 123456, ma hoa BCrypt)
INSERT INTO `user` (`id`, `username`, `password`, `fullname`, `status`, `email`, `createddate`, `createdby`) VALUES
  (1, 'manager', '$2a$10$pXTKDNnXNjzyMqVZyxE8m.j2imqkYakpG68mZqUQXWOa53GfTfFO2', 'Nguyễn Văn Quản Lý', 1, 'manager@estate.local', NOW(), 'system'),
  (2, 'staff1',  '$2a$10$pXTKDNnXNjzyMqVZyxE8m.j2imqkYakpG68mZqUQXWOa53GfTfFO2', 'Trần Thị Nhân Viên', 1, 'staff1@estate.local',  NOW(), 'system'),
  (3, 'staff2',  '$2a$10$pXTKDNnXNjzyMqVZyxE8m.j2imqkYakpG68mZqUQXWOa53GfTfFO2', 'Lê Văn Hùng',        1, 'staff2@estate.local',  NOW(), 'system'),
  (4, 'user1',   '$2a$10$pXTKDNnXNjzyMqVZyxE8m.j2imqkYakpG68mZqUQXWOa53GfTfFO2', 'Phạm Khách Hàng',    1, 'user1@estate.local',   NOW(), 'system');

INSERT INTO `user_role` (`user_id`, `role_id`) VALUES
  (1, 1),
  (2, 2),
  (3, 2),
  (4, 3);

-- Buildings  (district: QUAN_1, QUAN_3, ... | type: NOI_THAT, TANG_TRET, NGUYEN_CAN)
INSERT INTO `building`
  (`id`,`name`,`street`,`ward`,`district`,`structure`,`numberofbasement`,`floorarea`,`direction`,`level`,
   `rentprice`,`rentpricedescription`,`servicefee`,`carfee`,`motofee`,`overtimefee`,`waterfee`,`electricityfee`,
   `deposit`,`payment`,`renttime`,`decorationtime`,`brokeragefee`,`type`,`note`,`managername`,`managerphone`,
   `createddate`,`createdby`)
VALUES
  (1,'ACM Tower','96 Cao Thắng','Phường 4','QUAN_3','Nguyên khối',2,1200,'Đông Nam','A',
   30,'30 USD/m2','Miễn phí','100.000đ/tháng','50.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.500đ/kWh',
   '3 tháng','Đóng theo tháng','2 năm','1 tháng',2,'NOI_THAT','Gần trung tâm','Anh Minh','0901234567',NOW(),'system'),
  (2,'Nam Giao Building','59 Phan Xích Long','Phường 2','QUAN_PN','Nguyên khối',1,800,'Tây','B',
   25,'25 USD/m2','100.000đ/m2','120.000đ/tháng','60.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.500đ/kWh',
   '2 tháng','Đóng theo quý','1 năm','2 tuần',1,'TANG_TRET','Mặt tiền đường lớn','Chị Lan','0912345678',NOW(),'system'),
  (3,'Sunrise Tower','12 Nguyễn Hữu Thọ','Tân Hưng','QUAN_7','Bê tông cốt thép',3,2000,'Nam','A',
   35,'35 USD/m2','Miễn phí','150.000đ/tháng','70.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.800đ/kWh',
   '3 tháng','Đóng theo tháng','3 năm','1 tháng',3,'NGUYEN_CAN','Có hầm đỗ xe rộng','Anh Tuấn','0923456789',NOW(),'system'),
  (4,'Landmark Office','208 Nguyễn Hữu Cảnh','Phường 22','QUAN_BTH','Nguyên khối',2,1500,'Bắc','A',
   40,'40 USD/m2','Miễn phí','130.000đ/tháng','60.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.800đ/kWh',
   '3 tháng','Đóng theo tháng','2 năm','1 tháng',2,'NOI_THAT','View sông','Chị Hoa','0934567890',NOW(),'system'),
  (5,'Phố Cổ Tower','45 Nguyễn Trãi','Phường 2','QUAN_5','Nguyên khối',1,600,'Đông','C',
   18,'18 USD/m2','50.000đ/m2','100.000đ/tháng','40.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.500đ/kWh',
   '1 tháng','Đóng theo tháng','1 năm','1 tuần',1,'TANG_TRET','Gần chợ','Anh Dũng','0945678901',NOW(),'system'),
  (6,'Bến Thành Plaza','30 Lê Lợi','Bến Nghé','QUAN_1','Nguyên khối',3,2500,'Tây Nam','A',
   55,'55 USD/m2','Miễn phí','200.000đ/tháng','80.000đ/tháng','Tính theo giờ','Theo giá nhà nước','4.000đ/kWh',
   '4 tháng','Đóng theo tháng','3 năm','1 tháng',4,'NGUYEN_CAN','Vị trí trung tâm','Chị Mai','0956789012',NOW(),'system');

-- Dien tich thue (rentarea.value = m2)
INSERT INTO `rentarea` (`value`, `buildingid`, `createddate`, `createdby`) VALUES
  (100, 1, NOW(), 'system'), (200, 1, NOW(), 'system'), (300, 1, NOW(), 'system'),
  (150, 2, NOW(), 'system'), (250, 2, NOW(), 'system'),
  (200, 3, NOW(), 'system'), (400, 3, NOW(), 'system'), (600, 3, NOW(), 'system'),
  (120, 4, NOW(), 'system'), (300, 4, NOW(), 'system'),
  (80,  5, NOW(), 'system'), (160, 5, NOW(), 'system'),
  (250, 6, NOW(), 'system'), (500, 6, NOW(), 'system'), (800, 6, NOW(), 'system');

-- Giao toa nha cho nhan vien
INSERT INTO `assignmentbuilding` (`buildingid`, `staffid`) VALUES
  (1, 2), (2, 2), (3, 3), (4, 3), (5, 2), (6, 3);

-- Customers  (status: CHUA_XU_LY, DANG_XU_LY, DA_XU_LY)
INSERT INTO `customer`
  (`id`,`fullname`,`phone`,`email`,`demand`,`status`,`is_active`,`companyname`,`createddate`,`createdby`)
VALUES
  (1,'Nguyễn Thị Hương','0987654321','huong@abc.com','Cần thuê văn phòng 150m2 quận 3','CHUA_XU_LY',1,'Công ty ABC',NOW(),'system'),
  (2,'Trần Văn Bình','0976543210','binh@xyz.com','Tìm mặt bằng tầng trệt kinh doanh','DANG_XU_LY',1,'Công ty XYZ',NOW(),'system'),
  (3,'Lê Hoàng Nam','0965432109','nam@demo.com','Thuê nguyên căn làm trụ sở','DA_XU_LY',1,'Công ty Demo',NOW(),'system'),
  (4,'Phạm Thu Trang','0954321098','trang@shop.com','Cần văn phòng nhỏ 80m2','CHUA_XU_LY',1,'Shop Trang',NOW(),'system');

INSERT INTO `assignmentcustomer` (`customerid`, `staffid`) VALUES
  (1, 2), (2, 2), (3, 3), (4, 3);

-- Transactions  (code: CSKH = cham soc khach hang, DDX = dan di xem)
INSERT INTO `transaction` (`code`, `note`, `staffid`, `customerid`, `createddate`, `createdby`) VALUES
  ('CSKH', 'Gọi điện tư vấn lần 1', 2, 1, NOW(), 'staff1'),
  ('DDX',  'Dẫn khách đi xem ACM Tower', 2, 2, NOW(), 'staff1'),
  ('CSKH', 'Gửi báo giá qua email', 3, 3, NOW(), 'staff2');
