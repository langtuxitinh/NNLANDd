# Sử dụng NNland theo một chu trình hoàn chỉnh

## 1. Chuẩn bị với tài khoản quản trị

Vào `/admin`, kiểm tra thông tin tòa nhà/căn hộ và ngân hàng trước khi nhận đơn thật. Mỗi căn có mã riêng trong tòa nhà; Studio dùng số phòng ngủ bằng 0. Đơn giá điện/nước, dịch vụ, gửi xe và cọc được cấu hình theo từng căn.

Trang tài khoản có thể tạo thêm khách hàng/quản lý/quản trị viên. Sau khi đặt vai trò Quản lý, phân công tòa nhà cho tài khoản đó. Quản lý chỉ xử lý và xuất báo cáo dữ liệu các tòa được giao. Quản trị viên nhìn thấy toàn hệ thống.

Để thử hai phía đồng thời, mở hai trình duyệt hoặc một cửa sổ ẩn danh: một phía khách hàng, một phía quản lý. Không cần sửa database để tạo đơn, xác nhận tiền hay thay đổi trạng thái.

## 2. Tìm căn hộ và đặt lịch xem

Khách có thể xem trang chủ, tòa nhà và chi tiết căn hộ trước khi đăng nhập. Danh sách căn hộ mặc định tìm lịch trống từ hôm nay trong một tháng; mở bộ lọc thời gian để chọn kỳ khác. Các bộ lọc gồm thành phố, quận/huyện, số phòng ngủ, loại căn hộ, ngân sách và thời gian; có sắp xếp/phân trang.

Trang chi tiết có ảnh phóng lớn, tiện nghi, tầng, diện tích, mã căn, giá thuê, cọc và phí sinh hoạt. Nút **Đặt lịch xem căn hộ** mở biểu mẫu liên hệ gắn với căn đó. Gửi liên hệ không giữ chỗ và không tạo hợp đồng.

Quản lý xem **Lịch hẹn & liên hệ**, ghi rõ thời gian/phản hồi và cập nhật trạng thái. Liên hệ chung chưa chọn căn do quản trị viên xử lý. Khách đăng nhập khi gửi sẽ thấy lịch sử trong tài khoản.

## 3. Đặt thuê

Khách tạo tài khoản, đăng nhập và chọn **Chọn kỳ thuê**:

1. Chọn ngày nhận và số tháng, nhấn kiểm tra kỳ thuê.
2. Xem ngày trả, tiền thuê toàn kỳ, cọc và các phí thu riêng.
3. Đọc điều khoản và xác nhận đồng ý.
4. Nhấn tiếp tục để tạo đơn giữ chỗ và yêu cầu thanh toán có mã riêng.

Tiền nhà = giá tháng × số tháng; thu trước 100%. Cọc, nếu có, là khoản riêng. Ngày trả là ngày không tính tiền thuê nữa: một tháng từ 31/01 kết thúc 28/02; hai tháng từ 31/01 kết thúc 31/03.

Giá, cọc, đơn giá phí và phiên bản điều khoản được chốt theo báo giá được chấp nhận. Nếu quản lý đổi giá/phí trước khi khách gửi xác nhận, khách phải xem lại báo giá. Đơn đã tạo không bị thay giá theo cấu hình căn hộ mới.

Hệ thống kiểm tra lịch và giữ chỗ trong giao dịch cơ sở dữ liệu; hai khách không thể tạo hai kỳ thuê trùng cho cùng một căn.

## 4. Chuyển khoản và xác nhận tiền

Trang thanh toán hiển thị mã thanh toán, số tiền còn thiếu, tài khoản ngân hàng và QR khi đã cấu hình. Khách chuyển đúng nội dung và có thể báo thông tin chuyển khoản cho quản lý.

**Báo đã chuyển khoản không đồng nghĩa đã thanh toán.** Quản lý phải kiểm tra tiền thực nhận tại ngân hàng, mở **Thanh toán & đối soát**, nhập số tiền, mã giao dịch, thời điểm và xác nhận đã kiểm tra.

Các tình huống:

| Tình huống | Kết quả |
|---|---|
| Nhận chưa đủ | Lưu phiếu thu một phần; chưa tạo/xác nhận hợp đồng mới. |
| Nhận đủ, kỳ còn hợp lệ và còn lịch | Xác nhận đơn, tạo hợp đồng chờ nhận phòng hoặc cập nhật gia hạn. |
| Nhận thừa | Lưu toàn bộ tiền thực nhận; lập khoản hoàn phần thừa riêng. |
| Nhập lại cùng mã ngân hàng, cùng đơn và số tiền | Trả kết quả đã có, không cộng tiền thêm. |
| Dùng cùng mã cho đơn khác hoặc cho cả thu/hoàn | Bị chặn. |
| Tiền đến khi giữ chỗ hết hạn, lịch đã bị đặt hoặc kỳ không hợp lệ | Ghi thực nhận và chuyển đối soát; không chiếm lịch khách khác. |

Đơn giữ chỗ ban đầu hết hạn theo số phút cấu hình. Khách báo chuyển trong hạn được chờ đối chiếu tối đa 24 giờ một lần. Báo thêm hoặc sửa nội dung không gia hạn tiếp. Cần xử lý các khoản chờ đối soát trong vận hành hàng ngày.

Với đơn đến muộn nhưng vẫn còn kỳ/lịch hợp lệ, quản lý có thể dùng **Kiểm tra & xác nhận lại** sau khi đủ tiền. Nếu không còn phù hợp, xử lý hủy/hoàn theo hồ sơ thực tế.

Phiếu thu đã xác nhận là lịch sử tài chính và không có nút xóa/sửa. Hãy đối chiếu đúng giao dịch trước khi lưu; khoản hoàn chỉ dùng khi thực sự phải trả tiền lại, không dùng để che một lỗi nhập liệu.

## 5. Hợp đồng và nhận căn hộ

Sau khi đủ tiền, khách thấy hợp đồng **Chờ nhận căn hộ**. Có thể tải PDF. Thông báo trong tài khoản được tạo ngay; email/hợp đồng đính kèm được xếp hàng gửi theo cấu hình SMTP.

Ngày nhận phòng, quản lý mở hợp đồng:

1. Kiểm tra khách và nội dung bản giấy.
2. Hai bên ký trực tiếp, kiểm kê bàn giao.
3. Ghi chỉ số điện/nước ban đầu.
4. Xác nhận đã ký và bàn giao.

Hợp đồng chuyển sang **Đang lưu trú**. Không được nhận sớm trước ngày thuê, nhận sau khi kỳ đã hết, hoặc nhận căn còn khách cũ chưa trả. Căn đang bảo trì cũng chưa được bàn giao.

Tên/liên hệ khách và thông tin căn được lưu vào hợp đồng khi tạo; cập nhật hồ sơ/tên căn sau đó không làm sửa nội dung nhận diện đã chốt trên hợp đồng PDF.

## 6. Sinh hoạt hàng tháng

Quản lý vào **Hóa đơn điện nước → Lập hóa đơn**:

- Chọn hợp đồng đang lưu trú.
- Chọn kỳ `YYYY-MM`, ngày chốt và hạn thanh toán.
- Chỉ số đầu lấy từ bàn giao/hóa đơn trước; nhập chỉ số cuối.
- Kiểm tra phí dịch vụ, gửi xe và khoản điều chỉnh có diễn giải.

Tiền điện/nước = lượng sử dụng × đơn giá chốt trong hợp đồng, làm tròn đến đồng. Chỉ số cuối không được giảm. Không lập trùng kỳ cho cùng hợp đồng và không thu phí dịch vụ/gửi xe hai lần trong cùng tháng.

Khách xem chỉ số, tổng cộng, trạng thái và tải PDF. Thanh toán hóa đơn cũng dùng yêu cầu thanh toán/phiếu thu thực nhận, không có nút tự đặt Paid.

Quản lý được sửa **hóa đơn mới nhất chưa có phiếu thu** để điều chỉnh lỗi chỉ số/chi phí, phải ghi lý do. Sửa đồng thời cập nhật số tiền cần thu, chỉ số căn hộ, nhật ký và thông báo cho khách. Hóa đơn đã có tiền thực nhận hoặc có hóa đơn sau không được sửa số liệu.

## 7. Yêu cầu hỗ trợ

Khách đang lưu trú chọn hợp đồng, nhóm sự cố, ưu tiên và mô tả. Hệ thống tạo mã hỗ trợ, lưu lịch sử trao đổi. Quản lý chọn người phụ trách, kỹ thuật viên/đơn vị xử lý và cập nhật tiến độ.

Khách thấy từng phản hồi, có thể bổ sung thông tin; yêu cầu đã giải quyết có thể được đề nghị mở lại nếu vấn đề còn tồn tại. Yêu cầu đã đóng cần tạo yêu cầu mới khi có vấn đề khác.

## 8. Gia hạn

Trong chi tiết hợp đồng chọn **Đăng ký gia hạn**. Ngày bắt đầu được lấy từ ngày kết thúc hiện tại, không nhập tùy ý. Giá tháng và đơn giá phí theo hợp đồng đang có; không thu cọc lần nữa.

Hệ thống kiểm tra lịch khách khác đã đặt kỳ kế tiếp. Chỉ sau khi nhận đủ tiền gia hạn mới cập nhật ngày kết thúc hợp đồng. Thao tác xác nhận lặp lại không cộng thêm tháng.

Hợp đồng đang có yêu cầu hủy/trả phòng cần được xử lý trước; một đơn gia hạn đã giữ chỗ từ trước cũng không được kích hoạt trong khi yêu cầu kết thúc đang mở.

Hệ thống nhắc gần hết hạn và khi đến hạn; không tự xác nhận trả phòng hoặc tự giải phóng căn hộ do hết ngày thuê.

## 9. Hủy trước nhận phòng

- Đơn chưa báo chuyển khoản và chưa có tiền: khách hủy trong **Đơn đặt thuê**, giải phóng giữ chỗ ngay.
- Đơn đã báo chuyển hoặc có tiền: khách gửi yêu cầu để quản lý kiểm tra.
- Hợp đồng chờ nhận căn hộ: khách gửi yêu cầu hủy tại chi tiết hợp đồng.

Khi quản lý chấp thuận hủy, lịch được giải phóng và các khoản tiền thực nhận còn lại được lập hoàn theo khoản mục. Không tự đánh dấu đã chuyển tiền hoàn.

Khách đã nhận phòng sử dụng quy trình trả phòng, không hủy như đơn chưa bàn giao.

## 10. Trả phòng và quyết toán

1. Khách gửi ngày dự kiến trả và lý do tại chi tiết hợp đồng.
2. Quản lý kiểm tra căn, công nợ và ghi chỉ số cuối thực tế.
3. Bấm **Lập hóa đơn chốt trả phòng**, dùng kỳ `YYYY-MM-FINAL` **đúng ngày bàn giao thực tế** và thanh toán đủ trước khi kết thúc. Nếu đã thu hóa đơn này mà lùi ngày bàn giao trong cùng tháng, lập kỳ bổ sung `YYYY-MM-FINAL-001`, `-002`… theo gợi ý trên form. Chỉ số đầu nối tiếp hóa đơn cũ; không sửa hóa đơn đã thu. Phí dịch vụ/gửi xe đã tính trong tháng được mặc định bằng 0 để tránh tính lại.
4. Xử lý yêu cầu trả: ghi ngày bàn giao, chỉ số cuối, khấu trừ cọc và tiền thuê thỏa thuận hoàn theo biên bản.
5. Chọn căn **Sẵn sàng** hoặc **Đang bảo trì**.

Nếu còn hóa đơn chưa đủ tiền hoặc chỉ số trả khác chỉ số đã lập hóa đơn, hệ thống yêu cầu hoàn tất phần còn thiếu. Hợp đồng không được kết thúc chỉ bằng cách đổi trạng thái căn hộ.

Cọc được lập hoàn sau khi trừ khoản khấu trừ theo biên bản. Tiền thuê trả trước không tự động hoàn theo ngày; khoản hoàn sớm là số tiền được hai bên thỏa thuận và quản lý nhập, có giới hạn theo số đã thu/chưa hoàn. Các đơn gia hạn chưa hoàn tất cũng được hủy và đối soát khi kết thúc.

## 11. Chuyển tiền hoàn

Vào **Hoàn tiền**, kiểm tra khoản mục/lý do/số tiền. Chuyển khoản thực tế cho khách rồi nhập mã giao dịch hoàn và xác nhận đã chuyển. Khoản này mới chuyển sang **Hoàn tất** và được trừ trong báo cáo dòng tiền.

Không thể hoàn vượt tổng thực nhận còn được hoàn; cọc đã khấu trừ không được hoàn tiếp; mã ngân hàng không được dùng lại ở phiếu thu hoặc khoản hoàn khác.

## 12. Báo cáo và theo dõi

Chọn khoảng ngày, tòa nhà, căn hộ và loại thanh toán trong **Doanh thu & báo cáo**, tải `.xlsx` hoặc PDF với cùng phạm vi đang lọc. Biểu đồ thể hiện xu hướng 12 tháng, so sánh dòng tiền giữa các tòa và cơ cấu tiền thuê/điện/nước/dịch vụ/gửi xe/khấu trừ cọc. Trang tổng quan có tổng thực nhận và doanh thu phân bổ từ đầu năm.

- **Thực nhận:** tổng phiếu thu trong khoảng ngày.
- **Đã chuyển hoàn:** chỉ khoản hoàn đã thực sự chuyển.
- **Dòng tiền ròng:** thực nhận trừ hoàn.
- **Doanh thu phân bổ:** tiền thuê theo các kỳ tháng và phần ngày tương ứng; cộng phí đã thanh toán theo ngày chốt và khấu trừ cọc theo ngày trả.
- **Cọc/chuyển thừa:** hiển thị riêng, không coi là doanh thu thuê nhà.

Thu trước nhiều tháng làm tăng tiền thực nhận ngay nhưng không làm cả số tiền thành doanh thu một tháng. Hóa đơn và phiếu thu không bị cộng hai lần. Mục **Nhật ký thao tác** giúp quản trị viên tra cứu người thực hiện và các thay đổi quan trọng.
