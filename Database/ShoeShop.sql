/* =========================================================
   ShoeShop Database - 50 Sản Phẩm
   SQL Server 2017+
   ========================================================= */

IF DB_ID('ShoeShop') IS NOT NULL
BEGIN
    ALTER DATABASE ShoeShop SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE ShoeShop;
END
GO

CREATE DATABASE ShoeShop;
GO
USE ShoeShop;
GO

-- ======================== BẢNG CƠ SỞ DỮ LIỆU ========================

CREATE TABLE Roles (
    id   NVARCHAR(20)  PRIMARY KEY,
    name NVARCHAR(100) NOT NULL
);

CREATE TABLE Accounts (
    username      NVARCHAR(50)  PRIMARY KEY,
    fullname      NVARCHAR(100) NOT NULL,
    password_hash NVARCHAR(200) NOT NULL,
    email         NVARCHAR(120) UNIQUE NOT NULL,
    photo         NVARCHAR(255),
    activated     BIT           DEFAULT 1,
    is_admin      BIT           DEFAULT 0,
    address       NVARCHAR(200),
    phone         NVARCHAR(20),
    createDate    DATETIME2     DEFAULT SYSDATETIME()
);

CREATE TABLE Account_Roles (
    username NVARCHAR(50),
    roleId   NVARCHAR(20),
    PRIMARY KEY (username, roleId),
    FOREIGN KEY (username) REFERENCES Accounts(username) ON DELETE CASCADE,
    FOREIGN KEY (roleId) REFERENCES Roles(id) ON DELETE CASCADE
);

CREATE TABLE Categories (
    id   NVARCHAR(20)  PRIMARY KEY,
    name NVARCHAR(100) UNIQUE NOT NULL,
    note NVARCHAR(255)
);

CREATE TABLE Products (
    id         INT IDENTITY(1,1) PRIMARY KEY,
    name       NVARCHAR(150) NOT NULL,
    image      NVARCHAR(255),
    price      DECIMAL(12,2) CHECK (price >= 0),
    quantity   INT DEFAULT 0 CHECK (quantity >= 0),
    createDate DATETIME2 DEFAULT SYSDATETIME(),
    available  BIT DEFAULT 1,
    categoryId NVARCHAR(20),
    FOREIGN KEY (categoryId) REFERENCES Categories(id)
);

CREATE TABLE Orders (
    id            BIGINT IDENTITY(1,1) PRIMARY KEY,
    username      NVARCHAR(50),
    createDate    DATETIME2 DEFAULT SYSDATETIME(),
    status        TINYINT DEFAULT 0,
    totalAmount   DECIMAL(14,2) DEFAULT 0,
    shipName      NVARCHAR(120),
    shipPhone     NVARCHAR(20),
    address       NVARCHAR(200),
    paymentMethod NVARCHAR(30),
    paymentRef    NVARCHAR(120),
    note          NVARCHAR(255),
    FOREIGN KEY (username) REFERENCES Accounts(username)
);

CREATE TABLE OrderDetails (
    id        BIGINT IDENTITY(1,1) PRIMARY KEY,
    orderId   BIGINT,
    productId INT,
    price     DECIMAL(12,2) CHECK (price >= 0),
    quantity  INT CHECK (quantity > 0),
    discount  DECIMAL(5,2) DEFAULT 0 CHECK (discount BETWEEN 0 AND 100),
    FOREIGN KEY (orderId) REFERENCES Orders(id) ON DELETE CASCADE,
    FOREIGN KEY (productId) REFERENCES Products(id)
);

CREATE TABLE Carts (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    username   NVARCHAR(50),
    createDate DATETIME2 DEFAULT SYSDATETIME(),
    FOREIGN KEY (username) REFERENCES Accounts(username) ON DELETE CASCADE
);

CREATE TABLE CartItems (
    id        BIGINT IDENTITY(1,1) PRIMARY KEY,
    cartId    BIGINT,
    productId INT,
    quantity  INT DEFAULT 1 CHECK (quantity > 0),
    FOREIGN KEY (cartId) REFERENCES Carts(id) ON DELETE CASCADE,
    FOREIGN KEY (productId) REFERENCES Products(id)
);

-- ======================== CHỈ MỤC ========================

CREATE INDEX IX_Products_Category ON Products(categoryId);
CREATE INDEX IX_Products_Name ON Products(name);
CREATE INDEX IX_Orders_Username ON Orders(username);
CREATE INDEX IX_Orders_Status ON Orders(status);
CREATE UNIQUE INDEX UX_CartItems ON CartItems(cartId, productId);

-- ======================== TRIGGER ========================

CREATE TRIGGER trg_OrderDetails_UpdateTotal
ON OrderDetails AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    UPDATE o SET o.totalAmount = ISNULL(
        (SELECT SUM((od.price * od.quantity) * (1 - od.discount/100.0))
         FROM OrderDetails od WHERE od.orderId = o.id), 0)
    FROM Orders o
    WHERE o.id IN (SELECT orderId FROM inserted UNION SELECT orderId FROM deleted);
END;
GO

-- ======================== VIEW BÁO CÁO ========================

CREATE VIEW vw_RevenueByCategory AS
SELECT c.name AS categoryName,
       ISNULL(SUM((od.price * od.quantity) * (1 - od.discount/100.0)), 0) AS totalRevenue,
       ISNULL(SUM(od.quantity), 0) AS totalQuantity,
       ISNULL(MAX(od.price), 0) AS maxPrice,
       ISNULL(MIN(od.price), 0) AS minPrice,
       ISNULL(AVG(od.price), 0) AS avgPrice
FROM Categories c
LEFT JOIN Products p ON p.categoryId = c.id
LEFT JOIN OrderDetails od ON od.productId = p.id
LEFT JOIN Orders o ON o.id = od.orderId AND o.status IN (1,2,3)
GROUP BY c.name;
GO

CREATE VIEW vw_TopVIPCustomers AS
SELECT TOP 10 a.fullname AS customerName,
       SUM((od.price * od.quantity) * (1 - od.discount/100.0)) AS totalSpent,
       MIN(o.createDate) AS firstOrderDate,
       MAX(o.createDate) AS lastOrderDate
FROM Accounts a
JOIN Orders o ON o.username = a.username
JOIN OrderDetails od ON od.orderId = o.id
WHERE o.status IN (1,2,3)
GROUP BY a.fullname
ORDER BY totalSpent DESC;
GO

-- ======================== DỮ LIỆU MẪU ========================

-- Vai trò
INSERT INTO Roles VALUES 
(N'ADMIN', N'Quản trị viên'), 
(N'STAFF', N'Nhân viên'), 
(N'USER', N'Khách hàng');

-- Danh mục sản phẩm
INSERT INTO Categories VALUES
(N'MEN', N'Giày Nam', N'Giày thể thao và giày công sở nam'),
(N'WOMEN', N'Giày Nữ', N'Giày thời trang nữ'),
(N'KIDS', N'Giày Trẻ Em', N'Giày cho bé trai và bé gái'),
(N'SPORT', N'Giày Thể Thao', N'Giày chơi thể thao chuyên nghiệp'),
(N'RUNNING', N'Giày Chạy Bộ', N'Giày chạy bộ marathon'),
(N'CASUAL', N'Giày Thường Ngày', N'Sneaker đi chơi hàng ngày'),
(N'BASKET', N'Giày Bóng Rổ', N'Giày bóng rổ chuyên dụng'),
(N'FOOTBALL', N'Giày Bóng Đá', N'Giày đá bóng sân cỏ'),
(N'SANDAL', N'Dép & Sandal', N'Dép đi biển và dép thời trang');

-- Tài khoản (Mật khẩu: 123456)
INSERT INTO Accounts VALUES
(N'admin', N'Quản Trị Viên', N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
 N'admin@shoeshop.com', NULL, 1, 1, N'123 Lê Lợi, Q1, TP.HCM', N'0909111111', GETDATE()),

(N'staff01', N'Nguyễn Thị Lan', N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
 N'staff01@shoeshop.com', NULL, 1, 0, N'456 Nguyễn Huệ, Q1, TP.HCM', N'0909222222', GETDATE()),

(N'staff02', N'Trần Văn Bình', N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
 N'staff02@shoeshop.com', NULL, 1, 0, N'789 Pasteur, Q3, TP.HCM', N'0909223333', GETDATE()),

(N'user001', N'Nguyễn Văn An', N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
 N'nguyenvanan@gmail.com', NULL, 1, 0, N'12 Lý Thường Kiệt, Q10, TP.HCM', N'0909333333', GETDATE()),

(N'user002', N'Lê Thị Bích', N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
 N'lethibich@gmail.com', NULL, 1, 0, N'321 Võ Văn Tần, Q3, TP.HCM', N'0909444444', GETDATE()),

(N'user003', N'Phạm Văn Cường', N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
 N'phamcuong@gmail.com', NULL, 1, 0, N'654 Hai Bà Trưng, Q1, TP.HCM', N'0909555555', GETDATE()),

(N'user004', N'Hoàng Thị Dung', N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
 N'hoangdung@gmail.com', NULL, 1, 0, N'88 Điện Biên Phủ, Q.Bình Thạnh, TP.HCM', N'0909666666', GETDATE());

-- Phân quyền
INSERT INTO Account_Roles VALUES 
(N'admin', N'ADMIN'),
(N'staff01', N'STAFF'),
(N'staff02', N'STAFF'),
(N'user001', N'USER'),
(N'user002', N'USER'),
(N'user003', N'USER'),
(N'user004', N'USER');

-- 50 SẢN PHẨM GIÀY
SET IDENTITY_INSERT Products ON;

INSERT INTO Products (id, name, image, price, quantity, categoryId) VALUES
-- GIÀY NAM (10 sản phẩm)
(1, N'Nike Air Max 90 Essential', N'/images/nike-am90-essential.jpg', 3500000, 25, N'MEN'),
(2, N'Nike Air Force 1 Low White', N'/images/nike-af1-white.jpg', 2800000, 30, N'MEN'),
(3, N'Adidas Ultraboost 22', N'/images/adidas-ultraboost22.jpg', 4200000, 20, N'MEN'),
(4, N'Adidas Stan Smith Classic', N'/images/adidas-stansmith.jpg', 2600000, 35, N'MEN'),
(5, N'Puma RS-X Efekt', N'/images/puma-rsx-efekt.jpg', 2900000, 28, N'MEN'),
(6, N'New Balance 574 Core', N'/images/nb574-core.jpg', 2700000, 22, N'MEN'),
(7, N'Converse Chuck 70 High', N'/images/converse-chuck70.jpg', 1900000, 40, N'MEN'),
(8, N'Vans Old Skool Black', N'/images/vans-oldskool-black.jpg', 1600000, 45, N'MEN'),
(9, N'Reebok Club C 85 Vintage', N'/images/reebok-clubc85.jpg', 2200000, 30, N'MEN'),
(10, N'Asics Gel-Lyte III OG', N'/images/asics-gellyte3.jpg', 3200000, 18, N'MEN'),

-- GIÀY NỮ (10 sản phẩm)
(11, N'Nike Air Max 270 React Women', N'/images/nike-am270-women.jpg', 3200000, 28, N'WOMEN'),
(12, N'Adidas NMD R1 V2 Women', N'/images/adidas-nmdr1-women.jpg', 3600000, 22, N'WOMEN'),
(13, N'Puma Cali Sport Women', N'/images/puma-cali-sport.jpg', 2500000, 30, N'WOMEN'),
(14, N'New Balance 327 Women', N'/images/nb327-women.jpg', 2800000, 25, N'WOMEN'),
(15, N'Converse Run Star Hike', N'/images/converse-runstar.jpg', 2900000, 20, N'WOMEN'),
(16, N'Nike React Infinity Run', N'/images/nike-react-infinity.jpg', 3800000, 18, N'WOMEN'),
(17, N'Adidas Solarboost 4 Women', N'/images/adidas-solarboost4.jpg', 3500000, 23, N'WOMEN'),
(18, N'Vans Authentic Platform 2.0', N'/images/vans-platform2.jpg', 1700000, 32, N'WOMEN'),
(19, N'Reebok Classic Leather Women', N'/images/reebok-classic-women.jpg', 2100000, 27, N'WOMEN'),
(20, N'Asics Gel-Kayano 29 Women', N'/images/asics-kayano29.jpg', 4200000, 15, N'WOMEN'),

-- GIÀY TRẺ EM (8 sản phẩm)
(21, N'Nike Air Max 90 Kids', N'/images/nike-am90-kids.jpg', 2100000, 30, N'KIDS'),
(22, N'Adidas Superstar 360 Kids', N'/images/adidas-ss360-kids.jpg', 1800000, 35, N'KIDS'),
(23, N'Puma R78 Runner Kids', N'/images/puma-r78-kids.jpg', 1600000, 32, N'KIDS'),
(24, N'New Balance 574 Kids', N'/images/nb574-kids.jpg', 1900000, 28, N'KIDS'),
(25, N'Converse Chuck Taylor Kids', N'/images/converse-ct-kids.jpg', 1200000, 40, N'KIDS'),
(26, N'Nike Revolution 6 Kids', N'/images/nike-revolution6-kids.jpg', 1700000, 33, N'KIDS'),
(27, N'Adidas Tensaur Run Kids', N'/images/adidas-tensaur-kids.jpg', 1500000, 36, N'KIDS'),
(28, N'Vans Authentic Kids', N'/images/vans-authentic-kids.jpg', 1400000, 30, N'KIDS'),

-- GIÀY CHẠY BỘ (6 sản phẩm)
(29, N'Nike Vaporfly NEXT% 2', N'/images/nike-vaporfly-next2.jpg', 6500000, 10, N'RUNNING'),
(30, N'Adidas Adizero Adios Pro 3', N'/images/adidas-adios-pro3.jpg', 5800000, 12, N'RUNNING'),
(31, N'Asics Metaspeed Sky+', N'/images/asics-metaspeed-sky.jpg', 6200000, 9, N'RUNNING'),
(32, N'New Balance FuelCell RC Elite', N'/images/nb-fuelcell-elite.jpg', 5500000, 11, N'RUNNING'),
(33, N'Hoka One One Carbon X 3', N'/images/hoka-carbonx3.jpg', 5900000, 10, N'RUNNING'),
(34, N'Nike Pegasus 39', N'/images/nike-pegasus39.jpg', 3500000, 20, N'RUNNING'),

-- GIÀY CASUAL (6 sản phẩm)
(35, N'Nike Dunk Low Retro', N'/images/nike-dunk-low.jpg', 3300000, 18, N'CASUAL'),
(36, N'Adidas Samba OG Black', N'/images/adidas-samba-og.jpg', 2600000, 25, N'CASUAL'),
(37, N'New Balance 2002R', N'/images/nb2002r.jpg', 3600000, 16, N'CASUAL'),
(38, N'Puma Future Rider Play On', N'/images/puma-future-rider.jpg', 2800000, 22, N'CASUAL'),
(39, N'Converse Pro Leather Mid', N'/images/converse-pro-leather.jpg', 2200000, 24, N'CASUAL'),
(40, N'Vans Era Stacked', N'/images/vans-era-stacked.jpg', 1900000, 28, N'CASUAL'),

-- GIÀY BÓNG RỔ (4 sản phẩm)
(41, N'Nike LeBron 20', N'/images/nike-lebron20.jpg', 5200000, 10, N'BASKET'),
(42, N'Adidas Harden Vol. 7', N'/images/adidas-harden7.jpg', 4800000, 12, N'BASKET'),
(43, N'Air Jordan 37', N'/images/jordan37.jpg', 5600000, 9, N'BASKET'),
(44, N'Under Armour Curry Flow 10', N'/images/ua-curry-flow10.jpg', 4900000, 11, N'BASKET'),

-- GIÀY BÓNG ĐÁ (3 sản phẩm)
(45, N'Nike Mercurial Superfly 9', N'/images/nike-mercurial-sf9.jpg', 6800000, 8, N'FOOTBALL'),
(46, N'Adidas Predator Edge+', N'/images/adidas-predator-edge.jpg', 6200000, 9, N'FOOTBALL'),
(47, N'Puma Future Z 1.4', N'/images/puma-future-z14.jpg', 5500000, 10, N'FOOTBALL'),

-- DÉP & SANDAL (3 sản phẩm)
(48, N'Adidas Adilette Comfort', N'/images/adidas-adilette.jpg', 800000, 50, N'SANDAL'),
(49, N'Nike Benassi JDI', N'/images/nike-benassi-jdi.jpg', 750000, 55, N'SANDAL'),
(50, N'Birkenstock Arizona EVA', N'/images/birkenstock-arizona.jpg', 1200000, 40, N'SANDAL');

SET IDENTITY_INSERT Products OFF;

-- ======================== GIỎ HÀNG MẪU ========================

INSERT INTO Carts (username) VALUES 
(N'user001'),
(N'user002'),
(N'user003');

INSERT INTO CartItems (cartId, productId, quantity) VALUES 
(1, 1, 2),   -- user001: Nike Air Max 90 x2
(1, 11, 1),  -- user001: Nike Air Max 270 Women x1
(2, 5, 1),   -- user002: Puma RS-X x1
(2, 22, 3),  -- user002: Adidas Kids x3
(3, 35, 1),  -- user003: Nike Dunk Low x1
(3, 29, 1);  -- user003: Nike Vaporfly x1

-- ======================== ĐỠN HÀNG MẪU ========================

INSERT INTO Orders (username, status, shipName, shipPhone, address, paymentMethod, note) VALUES
(N'user001', 3, N'Nguyễn Văn An', N'0909333333', N'12 Lý Thường Kiệt, Q10, TP.HCM', N'COD', N'Giao giờ hành chính'),
(N'user001', 2, N'Nguyễn Văn An', N'0909333333', N'12 Lý Thường Kiệt, Q10, TP.HCM', N'BANK', N'Đã chuyển khoản'),
(N'user002', 3, N'Lê Thị Bích', N'0909444444', N'321 Võ Văn Tần, Q3, TP.HCM', N'MOMO', N'Giao ngoài giờ'),
(N'user002', 1, N'Lê Thị Bích', N'0909444444', N'321 Võ Văn Tần, Q3, TP.HCM', N'COD', N'Gọi trước 15 phút'),
(N'user003', 3, N'Phạm Văn Cường', N'0909555555', N'654 Hai Bà Trưng, Q1, TP.HCM', N'BANK', N''),
(N'user004', 2, N'Hoàng Thị Dung', N'0909666666', N'88 Điện Biên Phủ, Q.Bình Thạnh, TP.HCM', N'COD', N'Giao cuối tuần');

-- Chi tiết đơn hàng
INSERT INTO OrderDetails (orderId, productId, price, quantity, discount) VALUES
-- Đơn hàng 1
(1, 1, 3500000, 1, 0),      -- Nike Air Max 90
(1, 7, 1900000, 2, 10),     -- Converse Chuck 70 x2 (giảm 10%)
(1, 11, 3200000, 1, 5),     -- Nike Air Max 270 Women (giảm 5%)

-- Đơn hàng 2
(2, 35, 3300000, 1, 0),     -- Nike Dunk Low
(2, 29, 6500000, 1, 15),    -- Nike Vaporfly (giảm 15%)

-- Đơn hàng 3
(3, 5, 2900000, 2, 0),      -- Puma RS-X x2
(3, 22, 1800000, 3, 0),     -- Adidas Kids x3

-- Đơn hàng 4
(4, 11, 3200000, 1, 10),    -- Nike Air Max 270 Women (giảm 10%)
(4, 25, 1200000, 2, 0),     -- Converse Kids x2

-- Đơn hàng 5
(5, 41, 5200000, 1, 0),     -- Nike LeBron 20
(5, 48, 800000, 2, 0),      -- Adidas Adilette x2

-- Đơn hàng 6
(6, 29, 6500000, 1, 20),    -- Nike Vaporfly (giảm 20%)
(6, 3, 4200000, 1, 0);      -- Adidas Ultraboost

-- ======================== THỐNG KÊ ========================

PRINT '========== SHOESHOP DATABASE - TẠO THÀNH CÔNG ==========';
PRINT '';
PRINT 'Thống kê dữ liệu:';
SELECT 'Vai trò' AS [Bảng], COUNT(*) AS [Số lượng] FROM Roles 
UNION ALL SELECT 'Tài khoản', COUNT(*) FROM Accounts 
UNION ALL SELECT 'Danh mục', COUNT(*) FROM Categories 
UNION ALL SELECT 'Sản phẩm', COUNT(*) FROM Products 
UNION ALL SELECT 'Đơn hàng', COUNT(*) FROM Orders 
UNION ALL SELECT 'Chi tiết ĐH', COUNT(*) FROM OrderDetails
UNION ALL SELECT 'Giỏ hàng', COUNT(*) FROM Carts
UNION ALL SELECT 'Items trong giỏ', COUNT(*) FROM CartItems;

PRINT '';
PRINT 'Thông tin đăng nhập (Mật khẩu: 123456):';
PRINT '----------------------------------------';
PRINT 'Admin:  admin / 123456';
PRINT 'Staff:  staff01 / 123456';
PRINT '        staff02 / 123456';
PRINT 'Users:  user001, user002, user003, user004 / 123456';
PRINT '';
PRINT 'Tổng giá trị kho hàng:';
SELECT FORMAT(SUM(price * quantity), 'N0') + ' VND' AS [Tổng giá trị] FROM Products;
PRINT '';
PRINT '========================================================';
GO