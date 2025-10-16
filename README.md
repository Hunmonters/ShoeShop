### # ShoeShop - Website Bán Giày Thể Thao

## Mô tả dự án
ShoeShop là ứng dụng web bán hàng được xây dựng bằng Spring Boot, Thymeleaf và SQL Server. Dự án bao gồm đầy đủ các chức năng:
- Trưng bày và tìm kiếm sản phẩm
- Quản lý giỏ hàng
- Đặt hàng và quản lý đơn hàng
- Quản lý tài khoản người dùng
- Bảo mật và phân quyền
- Công cụ quản trị CRUD
- Báo cáo thống kê

## Công nghệ sử dụng
- **Backend**: Spring Boot 3.2.0, Spring Data JPA, Spring Security
- **Frontend**: Thymeleaf, Bootstrap 5, Font Awesome
- **Database**: SQL Server 2017+
- **Build Tool**: Maven
- **Java Version**: JDK 17+

## Yêu cầu hệ thống
- JDK 17 trở lên
- SQL Server 2017 trở lên
- Maven 3.6+
- IDE: Spring Tool Suite 4 (STS4) hoặc IntelliJ IDEA
- Browser: Chrome, Firefox, Edge (phiên bản mới nhất)

## Hướng dẫn cài đặt

### 1. Tạo Database

```sql
-- Mở SQL Server Management Studio và chạy file database.sql
-- File này nằm trong thư mục Database/database.sql
-- Hoặc copy nội dung từ document đã cung cấp
```

Database sẽ tự động tạo:
- 11 bảng dữ liệu
- Views cho báo cáo
- Trigger tính tổng tiền đơn hàng
- Dữ liệu mẫu (10 sản phẩm, 2 tài khoản, 3 categories)

### 2. Cấu hình Database Connection

Mở file `src/main/resources/application.properties` và cập nhật:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=ShoeShop;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD_HERE
```

### 3. Cấu hình Email (Gmail)

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

**Lưu ý**: Để lấy App Password từ Gmail:
1. Vào Google Account → Security
2. Bật 2-Step Verification
3. Tạo App Password cho Mail

### 4. Build và Run

#### Sử dụng Maven Command Line:
```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

#### Sử dụng STS4/IntelliJ:
1. Import project as Maven Project
2. Right click on project → Run As → Spring Boot App
3. Hoặc chạy file `ShoeShopApplication.java`

### 5. Truy cập ứng dụng

- **URL**: http://localhost:8080
- **Tài khoản Admin**:
- Username: `admin`
- Password: (cần hash trong DB, hoặc tạo mới)
- **Tài khoản User**:
- Username: `user1`
- Password: (cần hash trong DB, hoặc tạo mới)

## Cấu trúc thư mục nộp bài

```
ShoeShop/
├── BaoCao/
│   └── BaoCao.doc          # File báo cáo
├── Database/
│   └── database.sql        # Script tạo CSDL
├── Source/
│   └── ShoeShop/           # Toàn bộ source code
│       ├── src/
│       ├── pom.xml
│       └── README.md
└── README.md               # File này
```

## Chức năng chính

### PHẦN 1: WEBSITE KHÁCH HÀNG

#### 1. Trang chủ (/)
- Hiển thị sản phẩm bán chạy
- Hiển thị sản phẩm mới
- Navigation menu với categories

#### 2. Sản phẩm
- **Danh sách sản phẩm**: `/product/list-by-category/{id}`
- **Chi tiết sản phẩm**: `/product/detail/{id}`
- **Tìm kiếm**: `/product/search?keyword=...`
- Phân trang và filter

#### 3. Giỏ hàng
- **Xem giỏ hàng**: `/cart`
- **Thêm vào giỏ**: `/cart/add/{id}`
- **Cập nhật số lượng**: `/cart/update`
- **Xóa sản phẩm**: `/cart/remove/{id}`
- **Xóa giỏ hàng**: `/cart/clear`

#### 4. Tài khoản
- **Đăng ký**: `/account/sign-up`
- **Đăng nhập**: `/auth/login`
- **Kích hoạt email**: `/account/activate?token=...`
- **Cập nhật profile**: `/account/edit-profile`
- **Đổi mật khẩu**: `/account/change-password`
- **Quên mật khẩu**: `/account/forgot-password`

#### 5. Đặt hàng
- **Thanh toán**: `/order/checkout`
- **Danh sách đơn hàng**: `/order/list`
- **Chi tiết đơn hàng**: `/order/detail/{id}`
- **Sản phẩm đã mua**: `/order/my-product-list`

### PHẦN 2: QUẢN TRỊ (Admin)

#### 1. CRUD Management
- **Loại hàng**: `/admin/category/*`
- **Sản phẩm**: `/admin/product/*`
- **Đơn hàng**: `/admin/order/*`
- **Tài khoản**: `/admin/account/*`

#### 2. Báo cáo
- **Doanh thu theo loại**: `/admin/report/revenue`
- **Top 10 VIP**: `/admin/report/vip`

## Bảo mật

### Phân quyền:
- **Public**: Home, Product List, Product Detail
- **User**: Cart, Order, Profile Management
- **Admin**: All Admin pages (/admin/**)

### Security Features:
- Password encryption (BCrypt)
- Session management
- CSRF protection
- Remember me cookie
- Email activation
- Password reset

## Kiểm tra chức năng

### Test Flow cho khách hàng:
1. Truy cập trang chủ
2. Xem danh sách sản phẩm theo category
3. Xem chi tiết sản phẩm
4. Thêm vào giỏ hàng
5. Đăng ký tài khoản mới
6. Đăng nhập
7. Checkout và tạo đơn hàng
8. Xem lịch sử đơn hàng

### Test Flow cho Admin:
1. Đăng nhập với tài khoản admin
2. Thêm/sửa/xóa categories
3. Thêm/sửa/xóa products (với upload ảnh)
4. Xem và cập nhật trạng thái đơn hàng
5. Xem báo cáo doanh thu
6. Xem danh sách VIP customers

## API Endpoints chính

### Customer APIs:
```
GET  /home                          - Trang chủ
GET  /product/list-by-category/{id} - Danh sách sản phẩm
GET  /product/detail/{id}           - Chi tiết sản phẩm
POST /cart/add/{id}                 - Thêm vào giỏ
POST /auth/login                    - Đăng nhập
POST /account/sign-up               - Đăng ký
POST /order/checkout                - Đặt hàng
```

### Admin APIs:
```
GET  /admin/product/index           - Quản lý sản phẩm
POST /admin/product/create          - Tạo sản phẩm
POST /admin/product/update          - Cập nhật sản phẩm
GET  /admin/report/revenue          - Báo cáo doanh thu
GET  /admin/report/vip              - Khách hàng VIP
```

## Troubleshooting

### Lỗi kết nối Database:
```
- Kiểm tra SQL Server đang chạy
- Kiểm tra username/password trong application.properties
- Kiểm tra firewall cho port 1433
- Enable TCP/IP trong SQL Server Configuration Manager
```

### Lỗi không gửi được email:
```
- Kiểm tra Gmail App Password
- Bật Less Secure App Access (nếu cần)
- Kiểm tra cấu hình SMTP trong application.properties
```

### Lỗi upload ảnh:
```
- Kiểm tra đường dẫn upload.path trong application.properties
- Tạo thư mục images nếu chưa có
- Kiểm tra quyền ghi file
```

## File quan trọng cần kiểm tra

1. **pom.xml** - Dependencies
2. **application.properties** - Configuration
3. **Entity classes** - Data models
4. **Repository interfaces** - Database access
5. **Service classes** - Business logic
6. **Controllers** - Request handlers
7. **Thymeleaf templates** - Views

## Tiêu chí chấm điểm

### Điểm A (70-100%):
- ✅ Hoàn thành 90% yêu cầu chức năng
- ✅ Sản phẩm chạy tốt, ít lỗi
- ✅ Code sạch, có comment
- ✅ UI đẹp, responsive

### Điểm B (60-69%):
- ✅ Hoàn thành 90% phần 1
- ✅ CRUD cơ bản phần 2
- ✅ Bảo mật hoạt động

### Điểm C (50-59%):
- ✅ Hoàn thành một trong: Trưng bày sản phẩm hoặc Quản lý tài khoản

## Liên hệ và hỗ trợ

- **Giảng viên**: Thầy Bình
- **Email**: support@shoeshop.com
- **Document**: Xem file Assignment_Lập trình Java 5.pdf

## License
© 2025 ShoeShop - FPT Polytechnic Assignment

---
**Chúc bạn hoàn thành tốt Assignment!** 🎉
