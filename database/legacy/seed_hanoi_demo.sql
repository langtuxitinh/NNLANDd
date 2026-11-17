-- =====================================================================
--  Du lieu mau them cho tim kiem o trang chu (Ha Noi + them vai toa o TP.HCM)
--  Chay 1 lan tren database `estateadvance`. Chay lai se xoa va tao lai
--  chinh cac dong mau nay (danh dau createdby = 'seed-demo'), khong anh huong du lieu cu.
-- =====================================================================
SET NAMES utf8mb4;

DELETE FROM `rentarea` WHERE `buildingid` IN (SELECT `id` FROM (SELECT `id` FROM `building` WHERE `createdby` = 'seed-demo') AS t);
DELETE FROM `building` WHERE `createdby` = 'seed-demo';
INSERT INTO `building`
  (`name`,`street`,`ward`,`district`,`structure`,`numberofbasement`,`floorarea`,`direction`,`level`,
   `rentprice`,`rentpricedescription`,`servicefee`,`carfee`,`motofee`,`overtimefee`,`waterfee`,`electricityfee`,
   `deposit`,`payment`,`renttime`,`decorationtime`,`brokeragefee`,`type`,`note`,`managername`,`managerphone`,
   `createddate`,`createdby`)
VALUES
  ('Thăng Long Tower','18 Liễu Giai','Phường Liễu Giai','HN_BA_DINH','Bê tông cốt thép',2,1800,'Đông Nam','A',
   38,'38 USD/m2','Miễn phí','120.000đ/tháng','60.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.800đ/kWh',
   '3 tháng','Đóng theo tháng','2 năm','1 tháng',2,'VAN_PHONG','Văn phòng hạng A, gần Lăng Bác','Anh Hải','0901112233',
   NOW(),'seed-demo');
SET @bid = LAST_INSERT_ID();
INSERT INTO `rentarea` (`value`,`buildingid`,`createddate`,`createdby`) VALUES
  (120, @bid, NOW(), 'seed-demo'), (250, @bid, NOW(), 'seed-demo'), (400, @bid, NOW(), 'seed-demo');
INSERT INTO `building`
  (`name`,`street`,`ward`,`district`,`structure`,`numberofbasement`,`floorarea`,`direction`,`level`,
   `rentprice`,`rentpricedescription`,`servicefee`,`carfee`,`motofee`,`overtimefee`,`waterfee`,`electricityfee`,
   `deposit`,`payment`,`renttime`,`decorationtime`,`brokeragefee`,`type`,`note`,`managername`,`managerphone`,
   `createddate`,`createdby`)
VALUES
  ('Hoàn Kiếm Center','25 Tràng Tiền','Phường Tràng Tiền','HN_HOAN_KIEM','Nguyên khối',1,900,'Nam','A',
   60,'60 USD/m2','Miễn phí','120.000đ/tháng','60.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.800đ/kWh',
   '3 tháng','Đóng theo tháng','2 năm','1 tháng',2,'MAT_BANG','Mặt tiền phố cổ, phù hợp showroom và cửa hàng','Chị Thu','0902223344',
   NOW(),'seed-demo');
SET @bid = LAST_INSERT_ID();
INSERT INTO `rentarea` (`value`,`buildingid`,`createddate`,`createdby`) VALUES
  (60, @bid, NOW(), 'seed-demo'), (100, @bid, NOW(), 'seed-demo'), (180, @bid, NOW(), 'seed-demo');
INSERT INTO `building`
  (`name`,`street`,`ward`,`district`,`structure`,`numberofbasement`,`floorarea`,`direction`,`level`,
   `rentprice`,`rentpricedescription`,`servicefee`,`carfee`,`motofee`,`overtimefee`,`waterfee`,`electricityfee`,
   `deposit`,`payment`,`renttime`,`decorationtime`,`brokeragefee`,`type`,`note`,`managername`,`managerphone`,
   `createddate`,`createdby`)
VALUES
  ('Hai Bà Trưng Plaza','120 Bạch Mai','Phường Bạch Mai','HN_HAI_BA_TRUNG','Bê tông cốt thép',1,1100,'Tây Nam','B',
   22,'22 USD/m2','Miễn phí','120.000đ/tháng','60.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.800đ/kWh',
   '3 tháng','Đóng theo tháng','2 năm','1 tháng',2,'NOI_THAT','Văn phòng đã hoàn thiện nội thất','Anh Long','0903334455',
   NOW(),'seed-demo');
SET @bid = LAST_INSERT_ID();
INSERT INTO `rentarea` (`value`,`buildingid`,`createddate`,`createdby`) VALUES
  (80, @bid, NOW(), 'seed-demo'), (150, @bid, NOW(), 'seed-demo'), (300, @bid, NOW(), 'seed-demo');
INSERT INTO `building`
  (`name`,`street`,`ward`,`district`,`structure`,`numberofbasement`,`floorarea`,`direction`,`level`,
   `rentprice`,`rentpricedescription`,`servicefee`,`carfee`,`motofee`,`overtimefee`,`waterfee`,`electricityfee`,
   `deposit`,`payment`,`renttime`,`decorationtime`,`brokeragefee`,`type`,`note`,`managername`,`managerphone`,
   `createddate`,`createdby`)
VALUES
  ('Đống Đa Office','76 Tây Sơn','Phường Trung Liệt','HN_DONG_DA','Nguyên khối',2,1300,'Bắc','B',
   25,'25 USD/m2','Miễn phí','120.000đ/tháng','60.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.800đ/kWh',
   '3 tháng','Đóng theo tháng','2 năm','1 tháng',2,'VAN_PHONG,TANG_TRET','Gần trường đại học, giao thông thuận tiện','Chị Hà','0904445566',
   NOW(),'seed-demo');
SET @bid = LAST_INSERT_ID();
INSERT INTO `rentarea` (`value`,`buildingid`,`createddate`,`createdby`) VALUES
  (100, @bid, NOW(), 'seed-demo'), (200, @bid, NOW(), 'seed-demo'), (350, @bid, NOW(), 'seed-demo');
INSERT INTO `building`
  (`name`,`street`,`ward`,`district`,`structure`,`numberofbasement`,`floorarea`,`direction`,`level`,
   `rentprice`,`rentpricedescription`,`servicefee`,`carfee`,`motofee`,`overtimefee`,`waterfee`,`electricityfee`,
   `deposit`,`payment`,`renttime`,`decorationtime`,`brokeragefee`,`type`,`note`,`managername`,`managerphone`,
   `createddate`,`createdby`)
VALUES
  ('Cầu Giấy Green Tower','82 Duy Tân','Phường Dịch Vọng Hậu','HN_CAU_GIAY','Bê tông cốt thép',3,2600,'Đông','A',
   32,'32 USD/m2','Miễn phí','120.000đ/tháng','60.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.800đ/kWh',
   '3 tháng','Đóng theo tháng','2 năm','1 tháng',2,'NGUYEN_CAN','Cho thuê nguyên tòa, có hầm đỗ xe rộng','Anh Quân','0905556677',
   NOW(),'seed-demo');
SET @bid = LAST_INSERT_ID();
INSERT INTO `rentarea` (`value`,`buildingid`,`createddate`,`createdby`) VALUES
  (500, @bid, NOW(), 'seed-demo'), (900, @bid, NOW(), 'seed-demo'), (1500, @bid, NOW(), 'seed-demo');
INSERT INTO `building`
  (`name`,`street`,`ward`,`district`,`structure`,`numberofbasement`,`floorarea`,`direction`,`level`,
   `rentprice`,`rentpricedescription`,`servicefee`,`carfee`,`motofee`,`overtimefee`,`waterfee`,`electricityfee`,
   `deposit`,`payment`,`renttime`,`decorationtime`,`brokeragefee`,`type`,`note`,`managername`,`managerphone`,
   `createddate`,`createdby`)
VALUES
  ('Thanh Xuân Complex','35 Nguyễn Trãi','Phường Thượng Đình','HN_THANH_XUAN','Nguyên khối',1,700,'Tây','C',
   16,'16 USD/m2','Miễn phí','120.000đ/tháng','60.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.800đ/kWh',
   '3 tháng','Đóng theo tháng','2 năm','1 tháng',2,'TANG_TRET','Tầng trệt mặt đường lớn, khu dân cư đông','Chị Linh','0906667788',
   NOW(),'seed-demo');
SET @bid = LAST_INSERT_ID();
INSERT INTO `rentarea` (`value`,`buildingid`,`createddate`,`createdby`) VALUES
  (70, @bid, NOW(), 'seed-demo'), (120, @bid, NOW(), 'seed-demo');
INSERT INTO `building`
  (`name`,`street`,`ward`,`district`,`structure`,`numberofbasement`,`floorarea`,`direction`,`level`,
   `rentprice`,`rentpricedescription`,`servicefee`,`carfee`,`motofee`,`overtimefee`,`waterfee`,`electricityfee`,
   `deposit`,`payment`,`renttime`,`decorationtime`,`brokeragefee`,`type`,`note`,`managername`,`managerphone`,
   `createddate`,`createdby`)
VALUES
  ('Tây Hồ Riverside','28 Xuân Diệu','Phường Quảng An','HN_TAY_HO','Bê tông cốt thép',2,1500,'Đông Bắc','A',
   35,'35 USD/m2','Miễn phí','120.000đ/tháng','60.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.800đ/kWh',
   '3 tháng','Đóng theo tháng','2 năm','1 tháng',2,'VAN_PHONG','View Hồ Tây, môi trường làm việc xanh','Anh Đức','0907778899',
   NOW(),'seed-demo');
SET @bid = LAST_INSERT_ID();
INSERT INTO `rentarea` (`value`,`buildingid`,`createddate`,`createdby`) VALUES
  (150, @bid, NOW(), 'seed-demo'), (300, @bid, NOW(), 'seed-demo'), (450, @bid, NOW(), 'seed-demo');
INSERT INTO `building`
  (`name`,`street`,`ward`,`district`,`structure`,`numberofbasement`,`floorarea`,`direction`,`level`,
   `rentprice`,`rentpricedescription`,`servicefee`,`carfee`,`motofee`,`overtimefee`,`waterfee`,`electricityfee`,
   `deposit`,`payment`,`renttime`,`decorationtime`,`brokeragefee`,`type`,`note`,`managername`,`managerphone`,
   `createddate`,`createdby`)
VALUES
  ('Mỹ Đình Pearl','15 Lê Đức Thọ','Phường Mỹ Đình 1','HN_NAM_TU_LIEM','Bê tông cốt thép',2,2100,'Nam','A',
   28,'28 USD/m2','Miễn phí','120.000đ/tháng','60.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.800đ/kWh',
   '3 tháng','Đóng theo tháng','2 năm','1 tháng',2,'NOI_THAT,VAN_PHONG','Gần khu liên hợp thể thao quốc gia','Chị Mai','0908889900',
   NOW(),'seed-demo');
SET @bid = LAST_INSERT_ID();
INSERT INTO `rentarea` (`value`,`buildingid`,`createddate`,`createdby`) VALUES
  (130, @bid, NOW(), 'seed-demo'), (260, @bid, NOW(), 'seed-demo'), (520, @bid, NOW(), 'seed-demo');
INSERT INTO `building`
  (`name`,`street`,`ward`,`district`,`structure`,`numberofbasement`,`floorarea`,`direction`,`level`,
   `rentprice`,`rentpricedescription`,`servicefee`,`carfee`,`motofee`,`overtimefee`,`waterfee`,`electricityfee`,
   `deposit`,`payment`,`renttime`,`decorationtime`,`brokeragefee`,`type`,`note`,`managername`,`managerphone`,
   `createddate`,`createdby`)
VALUES
  ('Tân Bình Business Hub','56 Cộng Hòa','Phường 12','QUAN_TB','Bê tông cốt thép',2,1700,'Đông Nam','A',
   33,'33 USD/m2','Miễn phí','120.000đ/tháng','60.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.800đ/kWh',
   '3 tháng','Đóng theo tháng','2 năm','1 tháng',2,'VAN_PHONG','Gần sân bay Tân Sơn Nhất','Anh Phúc','0911223344',
   NOW(),'seed-demo');
SET @bid = LAST_INSERT_ID();
INSERT INTO `rentarea` (`value`,`buildingid`,`createddate`,`createdby`) VALUES
  (110, @bid, NOW(), 'seed-demo'), (240, @bid, NOW(), 'seed-demo'), (380, @bid, NOW(), 'seed-demo');
INSERT INTO `building`
  (`name`,`street`,`ward`,`district`,`structure`,`numberofbasement`,`floorarea`,`direction`,`level`,
   `rentprice`,`rentpricedescription`,`servicefee`,`carfee`,`motofee`,`overtimefee`,`waterfee`,`electricityfee`,
   `deposit`,`payment`,`renttime`,`decorationtime`,`brokeragefee`,`type`,`note`,`managername`,`managerphone`,
   `createddate`,`createdby`)
VALUES
  ('Gò Vấp Central','120 Quang Trung','Phường 10','QUAN_GV','Nguyên khối',1,850,'Tây','B',
   20,'20 USD/m2','Miễn phí','120.000đ/tháng','60.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.800đ/kWh',
   '3 tháng','Đóng theo tháng','2 năm','1 tháng',2,'MAT_BANG','Mặt bằng kinh doanh khu dân cư sầm uất','Chị Ngọc','0922334455',
   NOW(),'seed-demo');
SET @bid = LAST_INSERT_ID();
INSERT INTO `rentarea` (`value`,`buildingid`,`createddate`,`createdby`) VALUES
  (90, @bid, NOW(), 'seed-demo'), (160, @bid, NOW(), 'seed-demo'), (260, @bid, NOW(), 'seed-demo');
INSERT INTO `building`
  (`name`,`street`,`ward`,`district`,`structure`,`numberofbasement`,`floorarea`,`direction`,`level`,
   `rentprice`,`rentpricedescription`,`servicefee`,`carfee`,`motofee`,`overtimefee`,`waterfee`,`electricityfee`,
   `deposit`,`payment`,`renttime`,`decorationtime`,`brokeragefee`,`type`,`note`,`managername`,`managerphone`,
   `createddate`,`createdby`)
VALUES
  ('Thủ Đức Tech Park','7 Lê Văn Việt','Phường Hiệp Phú','TP_THU_DUC','Bê tông cốt thép',3,3000,'Nam','A',
   26,'26 USD/m2','Miễn phí','120.000đ/tháng','60.000đ/tháng','Tính theo giờ','Theo giá nhà nước','3.800đ/kWh',
   '3 tháng','Đóng theo tháng','2 năm','1 tháng',2,'NGUYEN_CAN,VAN_PHONG','Gần khu công nghệ cao, phù hợp công ty IT','Anh Khoa','0933445566',
   NOW(),'seed-demo');
SET @bid = LAST_INSERT_ID();
INSERT INTO `rentarea` (`value`,`buildingid`,`createddate`,`createdby`) VALUES
  (300, @bid, NOW(), 'seed-demo'), (600, @bid, NOW(), 'seed-demo'), (1200, @bid, NOW(), 'seed-demo');
