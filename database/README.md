# Cơ sở dữ liệu NNland

- `01_create_database.sql`: tạo database mới nếu tài khoản chạy ứng dụng không có quyền tạo.
- `02_nnland_schema.sql`: cấu trúc các bảng NNland; ứng dụng cũng tự tạo cấu trúc này khi khởi động.
- Dữ liệu mẫu được thêm bởi DataSeeder khi bật nnland.seed-data và chưa có tòa nhà.
- `03_nnland_demo_optional.sql`: dữ liệu mẫu tùy chọn gồm 3 tài khoản, 6 tòa, 30 căn và cấu hình trống ngân hàng. Chỉ import sau 01/02 khi database còn rỗng; không có giao dịch tài chính giả. Không cần tệp này nếu để ứng dụng tự tạo mẫu.
- `legacy/`: script nguyên bản bạn đã cung cấp, giữ để tham khảo; không dùng các script này để khởi tạo NNland.

Không cần import thủ công nếu tài khoản MySQL có quyền tạo database/bảng và bạn dùng cấu hình mặc định. Không import dữ liệu thử nghiệm lên database đang vận hành.
