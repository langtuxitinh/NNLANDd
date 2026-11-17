# Kiểm thử bản bàn giao NNland

Ngày thực hiện: **11/11/2026**. Các kiểm tra chạy trên cơ sở dữ liệu thử nghiệm riêng, không kết nối MySQL trên máy của bạn.

## Môi trường

| Thành phần | Phiên bản / cách chạy |
|---|---|
| Java của bản build cuối | OpenJDK Temurin **1.8.0_512** |
| Maven | **3.9.9** |
| Spring Boot / Tomcat nhúng | **2.7.18 / 9.0.83** |
| MySQL kiểm tra thực tế | **8.0.46**, InnoDB, utf8mb4 |
| MySQL Connector/J | **8.0.33** |
| Cơ sở dữ liệu kiểm thử nhanh | H2, chế độ tương thích MySQL |
| PDF | OpenPDF **1.3.43**, phông DejaVu Sans nhúng |
| Giao diện | JSP/JSTL, CSS/JavaScript, ảnh/font/SVG cục bộ |

## Kết quả bộ kiểm thử

Lệnh build cuối: `mvn clean package`, chạy bằng Java 8 và trỏ hai bộ kiểm thử vào hai database MySQL riêng biệt.

**36 bài kiểm tra: 0 thất bại, 0 lỗi, 0 bỏ qua. BUILD SUCCESS.**

- `RentalWorkflowTest`: **34** bài kiểm tra nghiệp vụ, đồng thời, phân quyền, tài liệu và email.
- `RenderedPagesTest`: **2** bài kiểm tra tích hợp HTTP thực tế qua Tomcat/Jasper. Đăng nhập bằng cookie và CSRF, dựng các trang công khai và trang chính của khách hàng/quản trị; không chỉ kiểm tra tên view.
- Cùng bộ **36 bài** cũng chạy lại thành công bằng Java 8 với H2/MySQL-mode theo cấu hình mặc định; đây là cấu hình kiểm thử khi bạn Build trong NetBeans. Java 21 cũng được dùng kiểm tra trong quá trình phát triển.

Những tình huống đã kiểm tra:

1. Tính tháng lịch tại ngày 31/01; thu trước 100% tiền thuê và tách cọc.
2. Chấp nhận điều khoản; báo giá thay đổi giá/phí/nội dung điều khoản bị yêu cầu xác nhận lại; đơn và hợp đồng giữ bản dữ liệu đã chốt.
3. Hai khách đồng thời giữ cùng căn; giữ chỗ hết hạn; transaction nhìn thấy lịch vừa được transaction khác commit sau khi chờ khóa.
4. Tiền đến trễ hoặc kỳ thuê đã qua được lưu thực nhận nhưng không chiếm lịch khác.
5. Nhận một phần/đủ/thừa, xác nhận lặp, mã ngân hàng trùng trên hai thanh toán hoặc giữa thu và hoàn; kiểm tra cả tình huống đồng thời.
6. Gia hạn bắt đầu đúng ngày kết thúc hiện tại; không gia hạn đè lịch người khác và không kích hoạt khi yêu cầu kết thúc đang mở.
7. Bàn giao cần đúng ngày, ký giấy, căn sẵn sàng và xử lý yêu cầu hủy đang chờ trước.
8. Hóa đơn theo chênh lệch công tơ, phí và kỳ; không lập trùng; chỉ sửa hóa đơn mới nhất chưa có tiền thực nhận.
9. Trả phòng cần hóa đơn FINAL đã thanh toán, đúng ngày và chỉ số; khấu trừ cọc giới hạn khoản hoàn. Hóa đơn FINAL đã thu có thể được nối tiếp bằng FINAL-001 khi dời ngày bàn giao trong cùng tháng.
10. Tiền đến sau khi hợp đồng đã đóng và đã hoàn hết vẫn phải đối soát/hoàn mới; không giữ nhầm trạng thái đã hoàn và không mở lại hợp đồng.
11. Khách không xem dữ liệu người khác; quản lý không xem/xuất dữ liệu tòa ngoài phạm vi; chặn POST thiếu CSRF, tự nâng quyền và khóa quản trị viên cuối.
12. Quên mật khẩu lưu hash token, hết hạn/dùng một lần; không báo email đã gửi khi SMTP tắt.
13. Email có người nhận/chủ đề tiếng Việt và PDF hợp đồng đính kèm; lỗi gửi chuyển FAILED, đợi thời gian thử lại rồi mới SENT. Phần vận chuyển email dùng bộ gửi mô phỏng trong kiểm thử, không gửi thư ra ngoài.
14. PDF và Excel là tài liệu có cấu trúc thật; PDF nhúng font tiếng Việt. PNG QR được giải mã lại bằng bộ đọc QR độc lập; mã thanh toán có ngân hàng, nội dung và số tiền.
15. Ảnh tải lên được chuẩn hóa, nội dung không phải ảnh bị từ chối.
16. Tìm kiếm mặc định loại căn đang bị chiếm; kỳ tương lai được kiểm tra riêng. Báo cáo lọc theo căn/tòa/loại thu, phân biệt tiền thực nhận và doanh thu, không phân bổ vượt ngày hiện tại.

## Kiểm tra tệp và mã giao diện

- Kiểm tra cú pháp JavaScript bằng `node --check`; mã CSS/JavaScript được định dạng để dễ chỉnh sửa.
- Đối chiếu các đường dẫn ảnh/CSS/JS/SVG cố định và tên biểu tượng trong **41 tệp JSP NNland**: không thiếu tài nguyên được tham chiếu.
- Đối chiếu SQL chạy tự động với `database/02_nnland_schema.sql`; bản import có thêm `USE nnland`.
- WAR có `WarLauncher`, đúng lớp chạy gốc và `Build-Jdk-Spec: 1.8`.

## Kiểm tra bản WAR đóng gói

Chạy **tệp WAR cuối bằng Java 8 trong một thư mục riêng không có cây mã nguồn** và một database MySQL mới. HTTP kiểm tra trang chủ, danh sách căn/tòa, chi tiết căn, đăng nhập và báo cáo quản trị đều trả 200. Ảnh WebP và font PDF được tải từ chính WAR. Database mới có đúng 3 tài khoản mẫu, 6 tòa, 30 căn và không có phiếu thu/hoàn giả. Ứng dụng được dừng sau kiểm tra.

Script SQL cấu trúc và dữ liệu mẫu tùy chọn đã được import vào một database MySQL rỗng khác; xác nhận đúng số tài khoản/tòa/căn.

## Phạm vi xác nhận

Kiểm tra HTTP/Jasper xác nhận trang dựng thành công và đúng các nội dung chính. Chưa có kiểm tra bố cục bằng trình duyệt đồ họa trên các thiết bị; không coi kiểm tra HTTP là bằng chứng mọi kích thước màn hình đều đã được xem trực quan.

Không thực hiện chuyển khoản ngân hàng thật hoặc gửi tới SMTP thật của bạn. Cần nhập ngân hàng và SMTP của đơn vị, rồi thử quét QR/gửi email với thông tin đó. Mã nguồn đã có quy trình chuyển khoản thủ công, đối soát, hàng đợi email, retry và thông báo trong tài khoản.

Không truy cập NetBeans/MySQL trên máy của bạn. Dự án giữ Maven WAR, JDK 8, cấu trúc nguồn và lớp khởi động của thư mục gốc; hướng dẫn điền kết nối và chạy bằng NetBeans nằm trong `HUONG_DAN_CAI_DAT.md`.

## Chạy lại kiểm tra

Chạy `mvn test` theo mặc định dùng H2 cô lập. Nếu chủ động dùng MySQL thử nghiệm, cấu hình `NNLAND_TEST_DATABASE_DRIVER`, `NNLAND_TEST_DATABASE_USER`, `NNLAND_TEST_DATABASE_PASSWORD`, `NNLAND_TEST_DATABASE_URL` và `NNLAND_WORKFLOW_DATABASE_URL`; dùng **hai database chỉ dành cho kiểm thử**. Các fixture có xóa dữ liệu thử nghiệm, tuyệt đối không trỏ chúng vào database đang vận hành.
