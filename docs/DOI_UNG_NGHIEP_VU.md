# Khách hàng ↔ Quản lý ↔ Cơ sở dữ liệu

Mọi thao tác bên dưới được ghi vào MySQL trong ứng dụng Java/JSP; hai giao diện đọc cùng dữ liệu. Admin có quyền toàn hệ thống, Manager chỉ có quyền ở các tòa được phân công.

| Tình huống | Khách hàng | Quản lý | Bảng dữ liệu chính |
|---|---|---|---|
| Tài khoản | Đăng ký bằng email, đăng nhập, cập nhật hồ sơ, đổi/quên mật khẩu | Tạo tài khoản, phân quyền và phân công tòa | `nn_accounts`, `nn_reset_tokens`, `nn_building_staff` |
| Danh mục | Tìm thành phố/khu vực/loại căn/giá/kỳ trống; xem ảnh, tầng, phí, tiện nghi | Thêm/sửa tòa và căn, ảnh, giá/phí, trạng thái sẵn sàng/bảo trì, công khai | `nn_buildings`, `nn_units` |
| Xem thực tế | Gửi lịch xem/liên hệ và theo dõi phản hồi | Xác nhận lịch, cập nhật kết quả, phản hồi | `nn_contacts` |
| Giữ chỗ | Chọn kỳ, xem báo giá, chấp nhận điều khoản | Theo dõi đơn đang giữ/chờ đối chiếu/hết hạn | `nn_bookings`, `nn_payments` |
| Tiền nhà | Chuyển khoản theo mã/QR, báo thông tin chuyển | Đối chiếu ngân hàng, nhập thực nhận và mã giao dịch | `nn_receipts`, `nn_bank_references`, `nn_payments` |
| Nhận phòng | Xem hợp đồng PDF, ký bản giấy khi nhận | Kiểm tra đủ tiền, xác nhận ký/bàn giao, nhập chỉ số đầu | `nn_leases`, `nn_bookings`, `nn_units` |
| Hóa đơn | Xem chỉ số, khoản phí, thanh toán và tải biên lai | Lập theo công tơ; sửa hóa đơn mới nhất chưa thu có lý do; đối chiếu tiền | `nn_invoices`, `nn_payments`, `nn_receipts` |
| Sửa chữa | Gửi sự cố, trao đổi, yêu cầu mở lại | Phân công người phụ trách/kỹ thuật viên, cập nhật tiến độ | `nn_tickets`, `nn_ticket_events` |
| Gia hạn | Chọn số tháng tiếp theo và thanh toán trước | Kiểm tra lịch và thực nhận; chỉ tăng kỳ sau đủ tiền | `nn_bookings`, `nn_leases`, `nn_receipts` |
| Hủy | Hủy giữ chỗ chưa có tiền hoặc gửi yêu cầu | Duyệt/từ chối; xác định khoản phải hoàn | `nn_lease_requests`, `nn_bookings`, `nn_leases`, `nn_refunds` |
| Trả phòng | Gửi ngày dự kiến và lý do | Chốt FINAL, kiểm tra công nợ, bàn giao, khấu trừ/hoàn, chuyển căn sẵn sàng/bảo trì | `nn_lease_requests`, `nn_invoices`, `nn_leases`, `nn_units`, `nn_refunds` |
| Hoàn tiền | Theo dõi khoản hoàn và mã ngân hàng | Chuyển thực tế rồi ghi mã; giới hạn theo tiền còn được hoàn | `nn_refunds`, `nn_bank_references` |
| Thông báo | Đọc thông báo và email theo cấu hình | Theo dõi email lỗi/thử lại và nhật ký | `nn_notifications`, `nn_mail_outbox`, `nn_audit_log` |
| Báo cáo | Tra cứu lịch sử riêng của mình | KPI tháng/năm, biểu đồ, lọc theo ngày/tòa/căn/loại thu, xuất Excel/PDF | Tổng hợp từ đơn, hợp đồng, hóa đơn, phiếu thu và hoàn tiền |

Các khác biệt có chủ ý so với bản ý tưởng ban đầu:

- Đặt thuê dùng trang từng bước thay vì phụ thuộc vào một popup dài; trên điện thoại vẫn xem đủ điều khoản, báo giá và thanh toán.
- Căn chỉ được giữ sau khi chấp nhận báo giá/điều khoản; mở trang chi tiết hoặc liên hệ không chiếm căn.
- Trạng thái lịch thuê, vận hành căn, thanh toán và hợp đồng được tách riêng để không gán nhầm “đã thuê” chỉ vì khách báo đã chuyển khoản.
- Hợp đồng PDF được tạo từ dữ liệu đã chốt khi đủ tiền; quản lý xác nhận chữ ký giấy khi bàn giao.
- Tiền thu trước, tiền cọc và doanh thu phân bổ được tách để tránh đếm toàn bộ tiền nhiều tháng vào doanh thu một tháng.
- Hết ngày thuê không tự coi là khách đã trả căn; cần kiểm kê, hóa đơn cuối kỳ và quyết toán.

Dữ liệu mẫu phục vụ chạy thử. Ảnh, tên tòa, địa chỉ và đơn giá cần thay bằng dữ liệu thực tế của đơn vị khi sử dụng.
