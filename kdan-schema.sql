-- 建立 store 表
CREATE TABLE store (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  cash_balance DECIMAL(10, 2) NOT NULL,
  opening_hours varchar(100) NOT NULL,
  PRIMARY KEY (id)
);

-- 建立 mask_type 表
CREATE TABLE mask_type (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  color VARCHAR(50) NOT NULL,
  quantity_per_pack INT NOT NULL,
  PRIMARY KEY (id)
);

-- 建立 store_hours 表
CREATE TABLE store_hours (
  id INT NOT NULL AUTO_INCREMENT,
  store_id INT,
  weekday VARCHAR(10),  -- Monday, Tuesday, Wednesday, etc.
  open_time TIME,
  close_time TIME,
  PRIMARY KEY (id),
  FOREIGN KEY (store_id) REFERENCES store(id)
);

-- 建立 mask 表
CREATE TABLE mask (
  id INT NOT NULL AUTO_INCREMENT,
  store_id INT,
  mask_type_id INT,
  price DECIMAL(10, 2) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (store_id) REFERENCES store(id),
  FOREIGN KEY (mask_type_id) REFERENCES mask_type(id)
);

-- 建立 user 表
CREATE TABLE user (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  cash_balance DECIMAL(10, 2) NOT NULL,
  PRIMARY KEY (id)
);

-- 建立 transaction_history 表
CREATE TABLE transaction_history (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT,  -- 用戶 ID
  store_id INT,  -- 藥局 ID（關聯 store 表）
  mask_type_id INT,  -- 口罩類型 ID（關聯 mask_type 表）
  transaction_amount DECIMAL(10, 2) NOT NULL,  -- 交易金額
  transaction_date DATETIME NOT NULL,  -- 交易日期
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES user(id),  -- 關聯到 user 表
  FOREIGN KEY (store_id) REFERENCES store(id),  -- 關聯到 store 表
  FOREIGN KEY (mask_type_id) REFERENCES mask_type(id)  -- 關聯到 mask_type 表
);
