# Cài đặt NNland bằng NetBeans và MySQL

## 1. Chuẩn bị

- Java JDK 8 trở lên. Dự án giữ mã nguồn tương thích Java 8.
- NetBeans hỗ trợ dự án Maven; Maven 3.6.3 trở lên (bản bàn giao dùng 3.9.9). Nếu NetBeans cũ đi kèm Maven quá thấp, chọn Maven mới tại Tools → Options → Java → Maven → Maven Home.
- MySQL đang chạy, thường tại `localhost:3306`.
- Lần build đầu cần Internet để Maven lấy thư viện. Ảnh và font của website đã nằm trong mã nguồn.

Đây là ứng dụng Java/JSP. Không cần npm, React, Docker hoặc tài khoản hosting để chạy trên máy của bạn. Docker chỉ được dùng trong quá trình kiểm thử bản bàn giao.

## 2. Giải nén đúng thư mục

Giải nén toàn bộ tệp ZIP. Thư mục dự án là thư mục chứa `pom.xml`. Không mở riêng `src/` hoặc chỉ sao chép tệp WAR rồi bỏ thư mục cấu hình.

Trong NetBeans chọn **File → Open Project** và chọn thư mục này. Chờ NetBeans nhận Maven project và tải các dependency.

## 3. Điền kết nối MySQL

Sao chép `application-local.properties.example` thành `application-local.properties` ở thư mục chứa `pom.xml`.

Điền đúng ba thông tin:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nnland?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=MAT_KHAU_MYSQL_THAT_TREN_MAY_BAN
```

Nếu MySQL dùng cổng khác, đổi `3306`. Nếu tài khoản của bạn không phải `root`, điền tên thực tế. Nếu tài khoản MySQL không có mật khẩu, để giá trị sau dấu `=` trống.

Ứng dụng mặc định dùng database **nnland**, tách khỏi database **estateadvance** trong dự án cũ. Các bảng mới có tiền tố `nn_`. Không cần import lại các script cũ để chạy NNland.

Nếu tài khoản không có quyền `CREATE DATABASE`, dùng MySQL Workbench hoặc công cụ MySQL để chạy `database/01_create_database.sql` bằng tài khoản có quyền, rồi cấp tài khoản chạy ứng dụng quyền trên database mới.

Ứng dụng tự chạy `nnland-schema.sql` với `CREATE TABLE IF NOT EXISTS`. Không xóa bảng hay đặt lại dữ liệu sau mỗi lần khởi động. Dữ liệu mẫu chỉ được thêm khi chưa có tòa nhà; các tài khoản mẫu chỉ được thêm khi email chưa tồn tại.

Nếu muốn khởi tạo thủ công hoàn toàn, chạy lần lượt `database/01_create_database.sql`, `02_nnland_schema.sql`, rồi `03_nnland_demo_optional.sql` trên database rỗng. Bước 03 là tùy chọn vì ứng dụng cũng tự tạo dữ liệu mẫu lần đầu. Không import tệp mẫu lên database đã có dữ liệu vận hành.

## 4. Chạy trong NetBeans

1. Chọn JDK 8 hoặc mới hơn tại **Project Properties → Java Platform** nếu cần.
2. Chọn **Clean and Build**. Lần đầu có thể mất thời gian tải thư viện.
3. Chọn **Run Project**. Tệp `nbactions.xml` đã đặt lệnh chạy là `spring-boot:run`, dùng Tomcat nhúng.
4. Mở `http://localhost:8081` sau khi nhật ký báo ứng dụng đã khởi động.

Nếu NetBeans vẫn hỏi máy chủ triển khai, mở **Run Maven → Goals** và chạy:

```text
spring-boot:run
```

Lớp chạy chính vẫn là `com.javaweb.SpringBootWebApplication` như cấu trúc gốc. Bạn không cần cài thêm Tomcat ngoài cho cách chạy này.

## 5. Chạy bản WAR có sẵn

Nếu bản ZIP có `target/NNland.war`, bật MySQL, điền cấu hình rồi nhấp `RUN_NNLAND.bat` trên Windows. Hoặc mở terminal tại thư mục dự án:

```bash
java -jar target/NNland.war
```

Trên Linux/macOS có thể dùng `sh run-nnland.sh`. Bản WAR không đòi hỏi Maven khi chỉ chạy; cần Maven/NetBeans khi chỉnh mã và build lại.

Nếu triển khai bằng Tomcat ngoài, dùng Tomcat thuộc hệ `javax.servlet` phù hợp như Tomcat 9; khi thay URL/context, cập nhật `nnland.base-url` cho liên kết email. Cách chạy khuyến nghị để giống hướng dẫn là Tomcat nhúng.

## 6. Đăng nhập và kiểm tra ban đầu

Các tài khoản thử nghiệm và mật khẩu ghi ở README. Đăng nhập quản trị rồi kiểm tra:

1. **Tòa nhà/Căn hộ:** đổi thông tin, giá thuê, ảnh và trạng thái theo dữ liệu của bạn.
2. **Tài khoản & phân quyền:** đổi mật khẩu tài khoản mẫu; tạo người dùng/quản lý và phân công tòa.
3. **Cài đặt hệ thống:** cập nhật ngân hàng, hotline, địa chỉ, email và điều khoản.
4. **Trang khách hàng:** tìm căn, chọn kỳ thuê và thử một chu trình với dữ liệu thử nghiệm.

Để cài đặt sạch không có tài khoản mẫu: đặt `nnland.demo-accounts=false`, cấu hình biến môi trường `NNLAND_ADMIN_EMAIL` và `NNLAND_ADMIN_PASSWORD` trước lần chạy đầu. Tài khoản quản trị ban đầu sẽ được tạo bằng những giá trị đó. Có thể đặt thêm `nnland.seed-data=false` để không thêm 6 tòa/30 căn mẫu.

Không tắt việc tạo tài khoản mẫu rồi bỏ trống mật khẩu quản trị ban đầu trên database rỗng; hệ thống cần ít nhất một tài khoản quản trị.

## 7. Cấu hình ngân hàng

Trong tài khoản quản trị, vào **Cài đặt hệ thống** và nhập:

- Tên ngân hàng.
- BIN ngân hàng, 6 chữ số, ví dụ `970436` là BIN của Vietcombank.
- Số tài khoản thực tế của đơn vị nhận tiền.
- Chủ tài khoản.

BIN là mã ngân hàng, không phải số tài khoản. Sau khi lưu, thử quét QR và đối chiếu tên người nhận/số tiền/nội dung trên ứng dụng ngân hàng trước khi áp dụng với khách thật.

Khách chuyển khoản; quản lý kiểm tra tài khoản ngân hàng và ghi **mã giao dịch thực tế, số tiền thực nhận, thời điểm nhận** trong trang đối soát. Không đánh dấu thanh toán bằng cách sửa trực tiếp database.

## 8. Cấu hình gửi email thật

Có thể điền trong `application-local.properties`:

```properties
nnland.mail.enabled=true
spring.mail.host=SMTP_CUA_DON_VI
spring.mail.port=587
spring.mail.username=TAI_KHOAN_SMTP
spring.mail.password=MAT_KHAU_SMTP_HOAC_MAT_KHAU_UNG_DUNG
nnland.mail.from=DIA_CHI_GUI_DA_XAC_MINH
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
nnland.base-url=http://localhost:8081
```

Hoặc dùng các biến môi trường: `MAIL_ENABLED`, `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`, `MAIL_AUTH`, `MAIL_STARTTLS`, `NNLAND_BASE_URL`.

Chọn thông số đúng với dịch vụ mail bạn sử dụng. Không dùng mật khẩu đăng nhập NNland làm mật khẩu SMTP. Khi website được truy cập bằng một địa chỉ khác, `nnland.base-url` phải là địa chỉ người nhận email có thể mở được.

Thư được xếp hàng trong cùng giao dịch với thao tác nghiệp vụ; bộ gửi xử lý định kỳ, có thử lại khi lỗi. Quản trị viên xem **Email thông báo** để biết thư đang chờ, đã gửi hoặc gửi chưa thành công. Khi SMTP tắt, thư giữ trạng thái chờ và không được báo là đã gửi.

Trong lúc thử nghiệm cục bộ, quản trị viên có thể mở nội dung email trong hàng đợi để kiểm tra liên kết quên mật khẩu hoặc nội dung thông báo. Chức năng này chỉ dành cho quản trị viên.

## 9. Ảnh riêng và sao lưu

Ảnh giao diện nằm trong `src/main/resources/static/nnland/images/`. Trong trang quản lý có thể chọn ảnh thư viện hoặc tải ảnh JPG/PNG riêng, tối đa 5 MB mỗi ảnh. Ảnh tải lên được tối ưu và lưu trong `uploads/` cạnh thư mục chạy ứng dụng.

Sao lưu **cả database MySQL và thư mục uploads/**. Build lại WAR không thay thế bản sao lưu ảnh đã tải lên. Có thể đổi thư mục ảnh bằng `nnland.upload-dir` hoặc cấu hình tương ứng trong tệp local.

## 10. Các lỗi khởi động thường gặp

| Hiện tượng | Cách kiểm tra |
|---|---|
| `Access denied for user` | Kiểm tra tên/mật khẩu MySQL và quyền trên database nnland. |
| `Communications link failure` | Bật MySQL, kiểm tra cổng và host trong JDBC URL. |
| Không tạo được database | Tạo trước bằng script 01 hoặc cấp quyền tạo database. |
| Cổng 8081 đang được dùng | Dừng ứng dụng cũ hoặc đổi `server.port` và `nnland.base-url`. |
| NetBeans chạy giao diện cũ | Mở đúng thư mục NNland mới; Clean and Build; chạy đúng lớp chính hoặc goal spring-boot:run. |
| Maven tải thư viện lỗi | Kiểm tra kết nối/proxy của Maven trong NetBeans; thử Build lại sau khi kết nối hoạt động. |
| Email chưa đến | Kiểm tra MAIL_ENABLED, SMTP, địa chỉ gửi và trạng thái trong Email thông báo. |
| Không thấy QR | Điền đầy đủ ngân hàng trong trang quản trị; kiểm tra đơn còn thời gian và còn tiền cần thanh toán. |
| Không được bàn giao/trả phòng | Kiểm tra ngày thuê, chữ ký giấy, hợp đồng trước còn ở, hóa đơn FINAL và công nợ. |

## 11. Chạy kiểm thử

`mvn test` dùng H2 ở chế độ MySQL cho kiểm thử cô lập, không chạm database vận hành mặc định của bạn. Các biến `NNLAND_TEST_DATABASE_*` và `NNLAND_WORKFLOW_DATABASE_URL` chỉ dùng nếu chủ động chạy bộ kiểm thử với database MySQL thử nghiệm riêng. Không trỏ những biến kiểm thử này vào database đang vận hành vì bộ kiểm thử xóa dữ liệu fixture của chính nó.

Chi tiết môi trường và kết quả bản bàn giao được ghi trong `docs/KIEM_THU.md`.
