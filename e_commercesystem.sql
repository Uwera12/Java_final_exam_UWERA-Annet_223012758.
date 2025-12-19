-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 19, 2025 at 08:26 PM
-- Server version: 8.3.0
-- PHP Version: 8.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `e_commercesystem`
--

-- --------------------------------------------------------

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
CREATE TABLE IF NOT EXISTS `cart_items` (
  `cart_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `added_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`cart_id`)
) ENGINE=MyISAM AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `cart_items`
--

INSERT INTO `cart_items` (`cart_id`, `user_id`, `product_id`, `quantity`, `added_at`) VALUES
(12, 0, 16, 1, '2025-12-19 12:53:41'),
(11, 0, 17, 1, '2025-11-23 16:24:10');

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
CREATE TABLE IF NOT EXISTS `categories` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`category_id`, `name`, `description`, `status`, `updated_at`, `created_at`) VALUES
(9, 'Clothing', NULL, NULL, '2025-11-07 11:49:57', '2025-11-07 11:49:57'),
(10, 'Home & Kitchen', NULL, NULL, '2025-11-07 11:49:57', '2025-11-07 11:49:57'),
(11, 'Sports & Outdoors', NULL, NULL, '2025-11-07 11:49:57', '2025-11-07 11:49:57'),
(13, 'SHOES', 'for girls and boys.', 'active', '2025-12-19 16:57:16', '2025-12-19 16:57:16');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
CREATE TABLE IF NOT EXISTS `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `order_number` varchar(50) DEFAULT NULL,
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('pending','paid','shipped','delivered','cancelled') DEFAULT 'pending',
  `total_amount` decimal(10,2) DEFAULT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `Notes` text,
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `order_number` (`order_number`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`order_id`, `user_id`, `order_number`, `date`, `status`, `total_amount`, `payment_method`, `Notes`) VALUES
(61, 24, NULL, '2025-11-22 17:10:41', 'shipped', 2699.99, 'Mobile Money', NULL),
(64, NULL, NULL, '2025-12-12 09:43:12', 'pending', NULL, NULL, NULL),
(65, 25, NULL, '2025-12-19 12:58:39', 'pending', 699.99, 'Mobile Money', NULL),
(66, 25, NULL, '2025-12-19 13:15:49', 'pending', 399.99, 'Cash on Delivery', NULL),
(69, 25, NULL, '2025-12-19 20:18:59', 'pending', 2300.00, 'Mobile Money', NULL),
(70, 25, NULL, '2025-12-19 20:19:00', 'pending', 24000.00, 'Mobile Money', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
CREATE TABLE IF NOT EXISTS `order_items` (
  `order_item_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `order_items`
--

INSERT INTO `order_items` (`order_item_id`, `order_id`, `product_id`, `quantity`, `price`, `user_id`) VALUES
(6, 61, 6, 1, 399.99, 0),
(7, 61, 16, 1, 2300.00, 0),
(8, 65, 10, 1, 699.99, 0),
(9, 66, 11, 1, 399.99, 0),
(10, 69, 20, 2, 12000.00, NULL),
(11, 69, 16, 1, 2300.00, 2),
(13, 70, 20, 2, 12000.00, NULL),
(14, 70, 16, 1, 2300.00, 2);

-- --------------------------------------------------------

--
-- Table structure for table `order_payments`
--

DROP TABLE IF EXISTS `order_payments`;
CREATE TABLE IF NOT EXISTS `order_payments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `payment_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `payment_id` (`payment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
CREATE TABLE IF NOT EXISTS `payments` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) DEFAULT NULL,
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `type` varchar(50) DEFAULT NULL,
  `reference` varchar(100) DEFAULT NULL,
  `status` enum('pending','completed','failed') DEFAULT 'pending',
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
CREATE TABLE IF NOT EXISTS `products` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  `description` text,
  `price` decimal(10,2) NOT NULL,
  `category_id` int DEFAULT NULL,
  `stock` int NOT NULL,
  `user_id` int DEFAULT NULL,
  `status` enum('active','inactive') DEFAULT 'active',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`),
  KEY `category_id` (`category_id`),
  KEY `seller_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`product_id`, `name`, `description`, `price`, `category_id`, `stock`, `user_id`, `status`, `created_at`) VALUES
(5, 'Smartphone X', 'Latest smartphone with 6GB RAM', 699.99, 1, 0, 2, 'active', '2025-11-07 11:44:24'),
(6, 'LED TV 42\"', '42 inch full HD LED TV', 399.99, 1, -1, 2, 'active', '2025-11-07 11:44:24'),
(7, 'Cooking Pan', 'Non-stick frying pan', 29.99, 4, 0, 2, 'active', '2025-11-07 11:44:24'),
(9, 'Java Programming Book', 'Learn Java step by step', 39.99, 2, 0, 2, 'active', '2025-11-07 11:44:24'),
(10, 'Smartphone X', 'Latest smartphone with 6GB RAM', 699.99, 1, -1, 2, 'active', '2025-11-07 11:50:44'),
(11, 'LED TV 42\"', '42 inch full HD LED TV', 399.99, 1, -1, 2, 'active', '2025-11-07 11:50:44'),
(12, 'Cooking Pan', 'Non-stick frying pan', 29.99, 4, 0, 2, 'active', '2025-11-07 11:50:44'),
(13, 'Running Shoes', 'Comfortable sports shoes', 59.99, 5, 0, 2, 'active', '2025-11-07 11:50:44'),
(14, 'Java Programming Book', 'Learn Java step by step', 39.99, 5, 3, 2, 'active', '2025-11-07 11:50:44'),
(16, 'Research book', 'for a beginer', 2300.00, 8, 9, 2, 'active', '2025-11-10 18:16:19'),
(18, 'data structure book', 'for beginner', 1000.00, 2, 10, 26, 'active', '2025-12-19 16:09:23'),
(20, 'converse', 'boys shoes', 12000.00, 13, 8, NULL, 'active', '2025-12-19 18:52:12');

-- --------------------------------------------------------

--
-- Table structure for table `shipments`
--

DROP TABLE IF EXISTS `shipments`;
CREATE TABLE IF NOT EXISTS `shipments` (
  `shipment_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `tracking_number` varchar(100) DEFAULT NULL,
  `status` enum('processing','shipped','delivered') DEFAULT 'processing',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`shipment_id`),
  KEY `order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `shipments`
--

INSERT INTO `shipments` (`shipment_id`, `order_id`, `tracking_number`, `status`, `created_at`) VALUES
(16, 61, '001', 'shipped', '2025-11-23 16:08:50'),
(17, 64, '1222', 'shipped', '2025-12-14 16:59:15');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `full_name` varchar(200) DEFAULT NULL,
  `role` varchar(50) NOT NULL DEFAULT 'customer',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password_hash`, `email`, `full_name`, `role`, `created_at`, `last_login`) VALUES
(3, 'Manzi@', '123', 'manzi12@gmail.com', 'annet', 'SELLER', '2025-11-04 10:42:45', '2025-11-04 10:42:45'),
(5, 'muhinda', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'muhii@gmail.com', 'MUHINDA WILSON', 'Customer', '2025-11-04 10:55:13', NULL),
(6, 'wwww', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'muhii12@gmail.com', 'muhinda wilson', 'seller', '2025-11-04 11:31:36', NULL),
(7, 'uwera', 'f6e0a1e2ac41945a9aa7ff8a8aaa0cebc12a3bcc981a929ad5cf810a090e11ae', 'uwera@gmail.com', 'uwera', 'Customer', '2025-11-04 14:23:59', NULL),
(8, 'annety', 'f6e0a1e2ac41945a9aa7ff8a8aaa0cebc12a3bcc981a929ad5cf810a090e11ae', 'uweraannety@gmail.com', 'uwera', 'Seller', '2025-11-04 14:24:47', NULL),
(9, 'kevine', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'keane@gmail.com', 'cyuzuzo kevine', 'Customer', '2025-11-06 15:12:16', NULL),
(10, 'KIM', '83ad92108b7c5a500908f390ba63ba1e54ad63132ecda463cd1e431881b890d7', 'KIMENYI@gmail.com', 'KIMENYI FRANK', 'Seller', '2025-11-07 08:39:56', '2025-11-07 09:05:29'),
(11, 'Annet@', 'd17f25ecfbcc7857f7bebea469308be0b2580943e96d13a3ad98a13675c4bfc2', 'annetuwera', 'UWERA ANNET', 'Admin', '2025-11-07 09:00:33', '2025-11-10 17:44:02'),
(12, 'alice123', 'hashed_pw_1', 'alice@example.com', 'Alice Johnson', 'customer', '2025-11-07 11:44:18', NULL),
(13, 'bob_seller', 'hashed_pw_2', 'bob@example.com', 'Bob Smith', 'seller', '2025-11-07 11:44:18', NULL),
(14, 'charlie456', 'hashed_pw_3', 'charlie@example.com', 'Charlie Brown', 'customer', '2025-11-07 11:44:18', NULL),
(15, 'diana789', 'hashed_pw_4', 'diana@example.com', 'Diana Prince', 'seller', '2025-11-07 11:44:18', NULL),
(22, 'Nziza', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'nziza@gmail.com', 'NZIZA FRED', 'Customer', '2025-11-10 17:04:40', '2025-11-11 13:34:07'),
(23, 'Bernard', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'bernard@gmail.com', 'Bernard G', 'Seller', '2025-11-10 17:45:58', '2025-11-10 17:52:49'),
(24, 'UMWALI', 'cc399d73903f06ee694032ab0538f05634ff7e1ce5e8e50ac330a871484f34cf', 'umwali@gmail.com', 'UMWALI ALLEN', 'Customer', '2025-11-22 16:20:59', '2025-11-22 17:10:08'),
(25, 'kimwhite', '26ae784d194a5760464348329af4eb9fca2b27bbf823742c968a61543e3a1153', 'white@gmail.com', 'Fredrick Muhire', 'Customer', '2025-12-19 12:56:56', '2025-12-19 20:18:24'),
(26, 'kizaaa', 'd17f25ecfbcc7857f7bebea469308be0b2580943e96d13a3ad98a13675c4bfc2', 'kiza@gmail.com', 'Kiza Fred', 'Seller', '2025-12-19 16:07:01', '2025-12-19 20:19:53');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  ADD CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `order_payments`
--
ALTER TABLE `order_payments`
  ADD CONSTRAINT `order_payments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  ADD CONSTRAINT `order_payments_ibfk_2` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`payment_id`);

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `shipments`
--
ALTER TABLE `shipments`
  ADD CONSTRAINT `shipments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
