-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ceramic_store_db
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id_category` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `event_status` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  PRIMARY KEY (`id_category`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Animales',_binary '','Animales','/categorias/1781171793995.webp','Piezas únicas de animales hechas a mano, con acabados artesanales y presencia cálida.'),(2,'Frutas',_binary '','Frutas','/categorias/1781171766820.webp','Miniaturas de frutas esmaltadas con acabado brillante para decorar cocinas y bandejas 11.'),(3,'Extras',_binary '','Piezas','/categorias/1781171817955.webp','Objetos con carácter, ideales para regalos, vitrinas pequeñas y colecciones con personalidad.'),(5,'Ramdon',_binary '','Randon','/categorias/1781172753435.webp','Son solo cosas random');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorites`
--

DROP TABLE IF EXISTS `favorites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorites` (
  `id_favorite` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `id_product` bigint NOT NULL,
  `id_user` bigint NOT NULL,
  PRIMARY KEY (`id_favorite`),
  UNIQUE KEY `UK2p6ivii13s2y4bf6obc8bxs6m` (`id_user`,`id_product`),
  KEY `FKknsl2w33734uj26wn0fiu7xcx` (`id_product`),
  CONSTRAINT `FKknsl2w33734uj26wn0fiu7xcx` FOREIGN KEY (`id_product`) REFERENCES `products` (`id_product`),
  CONSTRAINT `FKksy4x2jag0w6ta90lvxrv6vym` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorites`
--

LOCK TABLES `favorites` WRITE;
/*!40000 ALTER TABLE `favorites` DISABLE KEYS */;
INSERT INTO `favorites` VALUES (1,'2026-06-22 13:53:30.808842',20,2);
/*!40000 ALTER TABLE `favorites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id_order_item` bigint NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `unit_price` double NOT NULL,
  `id_order` bigint DEFAULT NULL,
  `id_product` bigint DEFAULT NULL,
  `id_size` bigint DEFAULT NULL,
  `size_dimension` varchar(255) DEFAULT NULL,
  `size_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_order_item`),
  KEY `FKj18ef1agdhkb3f8rmgrrgdbvu` (`id_order`),
  KEY `FK2eoo5mp3khsag7l93o41u1w7g` (`id_product`),
  KEY `FKctbset83ryts7lmbkory5dfnc` (`id_size`),
  CONSTRAINT `FK2eoo5mp3khsag7l93o41u1w7g` FOREIGN KEY (`id_product`) REFERENCES `products` (`id_product`),
  CONSTRAINT `FKctbset83ryts7lmbkory5dfnc` FOREIGN KEY (`id_size`) REFERENCES `size` (`id_size`),
  CONSTRAINT `FKj18ef1agdhkb3f8rmgrrgdbvu` FOREIGN KEY (`id_order`) REFERENCES `orders` (`id_order`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,10,9,1,20,NULL,'2x2 cm','Pulga'),(2,100,6,1,22,NULL,'2x2 cm','Pulga'),(3,50,6,1,22,NULL,'3x3 cm','Small'),(4,5,1.5,2,19,NULL,'2x2 cm','Pulga'),(5,5,8,2,13,NULL,'3x3 cm','Small'),(6,5,1.5,3,19,NULL,'2x2 cm','Pulga'),(7,5,8,3,13,NULL,'3x3 cm','Small'),(8,1,8,4,13,NULL,'3x3 cm','Small'),(9,1,8,5,13,NULL,'3x3 cm','Small'),(10,2,8,6,13,NULL,'3x3 cm','Small'),(11,3,8,7,13,NULL,'3x3 cm','Small'),(12,3,8,8,13,NULL,'3x3 cm','Small'),(13,3,8,9,13,NULL,'3x3 cm','Small'),(14,39,9,10,20,NULL,'2x2 cm','Pulga');
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id_order` bigint NOT NULL AUTO_INCREMENT,
  `register_date` datetime(6) NOT NULL,
  `status` varchar(255) NOT NULL,
  `total` double NOT NULL,
  `id_client` bigint DEFAULT NULL,
  `stripe_session_id` varchar(255) DEFAULT NULL,
  `customer_email` varchar(255) DEFAULT NULL,
  `customer_name` varchar(255) DEFAULT NULL,
  `fulfillment_status` varchar(255) DEFAULT NULL,
  `shipping_address` varchar(255) DEFAULT NULL,
  `shipping_reference` varchar(255) DEFAULT NULL,
  `customer_phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_order`),
  UNIQUE KEY `UKgkeltls6kwwpk41f577et02dj` (`stripe_session_id`),
  KEY `FKij55jgmxvqv003tvf8yyj8k00` (`id_client`),
  CONSTRAINT `FKij55jgmxvqv003tvf8yyj8k00` FOREIGN KEY (`id_client`) REFERENCES `personas` (`id_persona`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'2026-06-17 18:01:42.109615','PAID',990,2,'cs_test_b1Qr75E7XTEHKp1EZp4o9PnA42VBmDu0RwKMnIq4VmDGntyMwkstG1Mgqa',NULL,NULL,'PENDING_SHIPMENT',NULL,NULL,NULL),(2,'2026-06-17 19:29:10.089611','PENDING',47.5,2,'cs_test_b1aSZJBhvqdItUhzL3OUN0of5RjYWP1b3ChzoAeJ0HxIL2jUP7eWaxujDp',NULL,NULL,NULL,NULL,NULL,NULL),(3,'2026-06-18 08:00:51.108658','PAID',47.5,2,'cs_test_b1dmj745aT3eJ3FtGCcgp70paeWmIZ219gaC3Ee1ZHCGyZPXnpOF2KvXx3','efrainqn16@gmail.com','HAROLD EFRAIN QUISPE NAPA','PENDING_SHIPMENT','awa','cerca a la utp',NULL),(4,'2026-06-22 13:06:43.446122','PENDING',8,2,'cs_test_a12xADT4j2B4zHeFOQFcG6JrOP7dq0QiOpXfacZ5yngnEfjqLp0juuhWJ8','efrainqn16@gmail.com','HAROLD EFRAIN QUISPE NAPA','PENDING_SHIPMENT','Los olivos','en la utp',NULL),(5,'2026-06-22 13:07:48.915786','PENDING',8,2,'cs_test_a1q0DLM450oLbn9HmO1qw7LdAUSonTNi2cjD34FAlybqMr2zL8tX1cmJyE','efrainqn16@gmail.com','HAROLD EFRAIN QUISPE NAPA','PENDING_SHIPMENT','Upis el Trebol MzC LT08','cerca a la utp',NULL),(6,'2026-06-22 13:11:59.042398','PENDING',16,2,'cs_test_a1DdYwI1XyyMSnrUZOfrR1VpwyvkZrfbW6qNcFmOhQYWFqT1SLZGfBhMPT','efrainqn16@gmail.com','HAROLD EFRAIN QUISPE NAPA','PENDING_SHIPMENT','Los olivos','utp',NULL),(7,'2026-06-22 13:13:59.057693','PENDING',24,2,'cs_test_a1Egi8k3cDVDlbZ7u8UB37AJpR4PR3lb4qovIJPqdwmNfTuJYGC1g9xv1E','efrainqn16@gmail.com','HAROLD EFRAIN QUISPE NAPA','PENDING_SHIPMENT','Los olivos','cerca a la utp',NULL),(8,'2026-06-22 13:15:43.672996','PENDING',24,2,'cs_test_a1K1JwFJFKMSz4yYrLkU0Yw7KbDziZmmbj0WHrvx9ERJBMDdvOXmnZBCXT','efrainqn16@gmail.com','HAROLD EFRAIN QUISPE NAPA','SHIPPED','as','s',NULL),(9,'2026-06-22 13:52:38.386574','PAID',24,2,'cs_test_a1xdDhFyFUbUOHhsYwspQcIkZg2kdwGulpKJc1kb7H3Z2NYVqrGJKJWjcv','efrainqn16@gmail.com','HAROLD EFRAIN QUISPE NAPA','PENDING_SHIPMENT','los olivos','cerca a la utp','904207665'),(10,'2026-06-22 13:58:42.905430','PAID',351,2,'cs_test_a19j0gdVwLLggfpLzIZwE9ledd0XYSyZ6uBZPlMhl6GzrJKNXbzmRazqyO','efrainqn16@gmail.com','HAROLD EFRAIN QUISPE NAPA','PENDING_SHIPMENT','Los olivos','a','904207665');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_codes`
--

DROP TABLE IF EXISTS `password_reset_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_codes` (
  `id_password_reset_code` bigint NOT NULL AUTO_INCREMENT,
  `attempts` int NOT NULL,
  `code_hash` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `used` bit(1) NOT NULL,
  PRIMARY KEY (`id_password_reset_code`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_codes`
--

LOCK TABLES `password_reset_codes` WRITE;
/*!40000 ALTER TABLE `password_reset_codes` DISABLE KEYS */;
INSERT INTO `password_reset_codes` VALUES (1,0,'$2a$10$UQ0fsRZ67FJrB2aPPOGRS.zmuNvw4srq.Csg1GDsPa1148YQWXAOe','2026-06-11 04:27:27.359461','efrainqn16@gmail.com','2026-06-11 04:37:27.359461',_binary '\0'),(2,0,'$2a$10$Yn4QPJe61UCi67qcUHm14eIlQjE1DHFuoJo9PcrgVfuW4hFU2Kq9S','2026-06-11 04:29:47.305595','efrainqn16@gmail.com','2026-06-11 04:39:47.305595',_binary '\0'),(3,0,'$2a$10$Dyu3vCBarLwfi0dEDwbmF.ceBL37aI4PULbNt2kERqbCgrHBp6cIS','2026-06-11 04:34:08.776097','ellanoteama160104@gmail.com','2026-06-11 04:44:08.776097',_binary '\0'),(4,0,'$2a$10$bbZQKHKxjbGW8lnyGreGxuOpfr.5h/EykDLvJ5sGySOVer3wV0I56','2026-06-11 04:40:45.130019','ellanoteama160104@gmail.com','2026-06-11 04:50:45.130019',_binary '\0'),(5,0,'$2a$10$y3Y2VzRXZbNd6T6iXuRUVeNuhzSdr0S92w/o5PyHYOe.Y/812jg76','2026-06-17 17:25:22.539742','efrainqn1601@gmail.com','2026-06-17 17:35:22.539742',_binary '\0'),(6,5,'$2a$10$jjdSuVGCBheF3DrTv4EOXuUhkdYbnQSZaIe5q7zvL31v/zS8m06Z.','2026-06-17 17:27:00.905712','ellanoteama160104@gmail.com','2026-06-17 17:37:00.905712',_binary ''),(7,1,'$2a$10$vN3962F8aUWm8eVq0f6gw.e5K/nwXVGGw1jcv1a4uF8xiR/QRwM9m','2026-06-17 17:28:30.837317','ellanoteama160104@gmail.com','2026-06-17 17:38:30.837317',_binary '');
/*!40000 ALTER TABLE `password_reset_codes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id_payment` bigint NOT NULL AUTO_INCREMENT,
  `method` varchar(255) NOT NULL,
  `payment_date` datetime(6) NOT NULL,
  `id_order` bigint DEFAULT NULL,
  `payment_status` varchar(255) DEFAULT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `amount` double NOT NULL,
  `status` varchar(255) NOT NULL,
  `stripe_session_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_payment`),
  UNIQUE KEY `UKk0ew22hb3du79tp0hdgu7qybp` (`stripe_session_id`),
  KEY `FKalvupkbf37ax1kxiayasm8k1x` (`id_order`),
  CONSTRAINT `FKalvupkbf37ax1kxiayasm8k1x` FOREIGN KEY (`id_order`) REFERENCES `orders` (`id_order`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,'STRIPE','2026-06-17 18:03:23.642135',1,NULL,NULL,990,'PAID','cs_test_b1Qr75E7XTEHKp1EZp4o9PnA42VBmDu0RwKMnIq4VmDGntyMwkstG1Mgqa'),(2,'STRIPE','2026-06-18 08:01:27.730573',3,NULL,NULL,47.5,'PAID','cs_test_b1dmj745aT3eJ3FtGCcgp70paeWmIZ219gaC3Ee1ZHCGyZPXnpOF2KvXx3'),(6,'STRIPE','2026-06-22 13:53:05.227200',9,NULL,NULL,24,'PAID','cs_test_a1xdDhFyFUbUOHhsYwspQcIkZg2kdwGulpKJc1kb7H3Z2NYVqrGJKJWjcv'),(7,'STRIPE','2026-06-22 13:59:01.164742',10,NULL,NULL,351,'PAID','cs_test_a19j0gdVwLLggfpLzIZwE9ledd0XYSyZ6uBZPlMhl6GzrJKNXbzmRazqyO');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personas`
--

DROP TABLE IF EXISTS `personas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `personas` (
  `id_persona` bigint NOT NULL AUTO_INCREMENT,
  `birth_date` varchar(255) DEFAULT NULL,
  `dni` varchar(8) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `mother_last_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `shipping_address` varchar(255) DEFAULT NULL,
  `shipping_city` varchar(255) DEFAULT NULL,
  `shipping_number` varchar(255) DEFAULT NULL,
  `shipping_province` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_persona`),
  UNIQUE KEY `UKnnr7w7h44crpy3wdqmk1724hp` (`dni`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `personas`
--

LOCK TABLES `personas` WRITE;
/*!40000 ALTER TABLE `personas` DISABLE KEYS */;
INSERT INTO `personas` VALUES (1,'25/11/2003','73332055','JOSE ANTHONY','ADANAQUE','ZUÑIGA','JOSE ANTHONY ADANAQUE ZUÑIGA',NULL,NULL,NULL,NULL,NULL,NULL),(2,'16/01/2004','72472737','HAROLD EFRAIN','QUISPE','NAPA','HAROLD EFRAIN QUISPE NAPA',NULL,NULL,NULL,NULL,NULL,NULL),(3,'22/08/2002','71123134','MICAELA DEL ROSARIO','GARCIA','WONG','MICAELA DEL ROSARIO GARCIA WONG',NULL,NULL,NULL,NULL,NULL,NULL),(4,'15/11/1994','72313222','KELY CAROLINA','GONZALES','SALES','KELY CAROLINA GONZALES SALES',NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `personas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_sizes`
--

DROP TABLE IF EXISTS `product_sizes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_sizes` (
  `id_product` bigint NOT NULL,
  `id_size` bigint NOT NULL,
  KEY `FKmy9ovi72qttbx2a18j8aix75j` (`id_size`),
  KEY `FKlkkrna8rmondt3jxb7u6csxfx` (`id_product`),
  CONSTRAINT `FKlkkrna8rmondt3jxb7u6csxfx` FOREIGN KEY (`id_product`) REFERENCES `products` (`id_product`),
  CONSTRAINT `FKmy9ovi72qttbx2a18j8aix75j` FOREIGN KEY (`id_size`) REFERENCES `size` (`id_size`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_sizes`
--

LOCK TABLES `product_sizes` WRITE;
/*!40000 ALTER TABLE `product_sizes` DISABLE KEYS */;
INSERT INTO `product_sizes` VALUES (1,1),(1,2),(2,1),(2,2),(3,1),(3,2),(4,2),(12,1),(13,1),(14,1),(14,2),(15,2),(16,2),(16,1),(17,1),(18,1),(18,2),(18,1),(18,2),(19,2),(20,2),(21,2),(22,1),(22,2),(23,2);
/*!40000 ALTER TABLE `product_sizes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id_product` bigint NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `status` bit(1) NOT NULL,
  `stock` int NOT NULL,
  `id_category` bigint DEFAULT NULL,
  PRIMARY KEY (`id_product`),
  KEY `FKip7b0y8ja7fsm5wl7mhmseh5n` (`id_category`),
  CONSTRAINT `FKip7b0y8ja7fsm5wl7mhmseh5n` FOREIGN KEY (`id_category`) REFERENCES `categories` (`id_category`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'/categorias/loro.png','Loro',85.50,_binary '\0',12,1),(2,'/categorias/delfin.png','Delfin Rosado',25.00,_binary '\0',12,1),(3,'/categorias/brujas.png','Brujas',150.00,_binary '\0',15,1),(4,'/categorias/buho.png','Buho',80.50,_binary '\0',20,1),(5,'/categorias/perro.png','Perro',150.00,_binary '\0',30,1),(6,'/categorias/jarrones.png','Jarrones 3',75.00,_binary '\0',27,3),(7,'/categorias/frutas.png','Frutas canasta sobremesa',100.00,_binary '\0',15,2),(8,'/categorias/brujas.png','Brujas',270.00,_binary '\0',50,3),(9,'/categorias/set_frutal.png','Set Frutal',150.00,_binary '\0',30,2),(10,'/categorias/1781169740416.jpg','ROJITO',100.00,_binary '\0',5000,3),(11,'/categorias/1781172804398.jpg','yatusabes',110.00,_binary '\0',2220,5),(12,'https://portafolio.s3.us-east-2.amazonaws.com/productos/1782544327146-451ede7a-7fc9-4555-8e58-359c48ae16cb.png','Buho Junior',9.50,_binary '',1000,5),(13,'https://portafolio.s3.us-east-2.amazonaws.com/productos/1782544159721-e7347ed8-ca14-48b7-9167-bcf12572e624.png','Alan Garcia',8.00,_binary '',142,3),(14,'https://portafolio.s3.us-east-2.amazonaws.com/productos/1782544177566-235690d0-585f-4f5f-80f4-3cee333f17c1.jpg','Celebridad Navidad',25.00,_binary '',50,3),(15,'https://portafolio.s3.us-east-2.amazonaws.com/productos/1782544186400-06b04ac9-3c2b-4fbe-8db9-ee58b84969c2.jpg','Casa Belen',7.00,_binary '',200,3),(16,'https://portafolio.s3.us-east-2.amazonaws.com/productos/1782544199968-d0065f77-3327-44a2-a9e6-e2b2511e2ff3.jpg','Caballito',11.00,_binary '',1000,3),(17,'https://portafolio.s3.us-east-2.amazonaws.com/productos/1782544212979-1a995e53-fbe4-4777-8a59-be9ba298cc89.jpg','Uvita',4.00,_binary '',2000,2),(18,'https://portafolio.s3.us-east-2.amazonaws.com/productos/1782544221219-30be8cc8-de4b-4f83-8aa5-90ad7df3d282.png','Pera',3.00,_binary '',2000,2),(19,'https://portafolio.s3.us-east-2.amazonaws.com/productos/1782544230298-0fce4302-c1e3-4ff2-a7f2-52eb56eaaaeb.png','Fresa',1.50,_binary '',95,2),(20,'https://portafolio.s3.us-east-2.amazonaws.com/productos/1782544240022-19e55e96-b233-4dc1-9bec-56c2fc1de469.png','Aguila',9.00,_binary '',1,1),(21,'https://portafolio.s3.us-east-2.amazonaws.com/productos/1782544251130-ee7e3f49-264f-46ec-970a-ef587f64d344.png','Foca',3.00,_binary '',400,1),(22,'https://portafolio.s3.us-east-2.amazonaws.com/productos/1782544268942-fd2e332c-5a3e-4970-b291-58394dda69fb.png','Tigre',6.00,_binary '',70,1),(23,'https://portafolio.s3.us-east-2.amazonaws.com/productos/1782544291227-d944b40c-d4fe-4ac3-b186-d7ed02920498.png','Buho Coral',9.00,_binary '',600,1);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_intentions`
--

DROP TABLE IF EXISTS `purchase_intentions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_intentions` (
  `id_purchase_intention` bigint NOT NULL AUTO_INCREMENT,
  `viewed_at` datetime(6) NOT NULL,
  `id_product` bigint NOT NULL,
  `id_user` bigint NOT NULL,
  `interaction_type` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_purchase_intention`),
  KEY `FKf6e6wmvwf5ao42595w3fgdlcc` (`id_product`),
  KEY `FKe6kockqier6ph15megoik2dh3` (`id_user`),
  CONSTRAINT `FKe6kockqier6ph15megoik2dh3` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`),
  CONSTRAINT `FKf6e6wmvwf5ao42595w3fgdlcc` FOREIGN KEY (`id_product`) REFERENCES `products` (`id_product`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_intentions`
--

LOCK TABLES `purchase_intentions` WRITE;
/*!40000 ALTER TABLE `purchase_intentions` DISABLE KEYS */;
INSERT INTO `purchase_intentions` VALUES (1,'2026-06-17 18:11:25.105878',12,2,NULL),(2,'2026-06-17 18:11:25.105878',12,2,NULL),(3,'2026-06-17 18:11:43.679948',13,2,NULL),(4,'2026-06-17 18:11:43.749334',13,2,NULL),(5,'2026-06-17 18:12:46.591440',17,2,NULL),(6,'2026-06-17 18:12:46.664552',17,2,NULL),(7,'2026-06-17 18:16:35.657635',20,2,NULL),(8,'2026-06-17 18:18:48.343923',12,2,NULL),(9,'2026-06-17 18:18:56.324654',12,2,NULL),(10,'2026-06-17 18:22:45.381248',12,2,NULL),(11,'2026-06-17 18:28:36.851691',12,2,NULL),(12,'2026-06-17 18:29:26.195029',12,2,NULL),(13,'2026-06-17 18:29:44.503910',17,2,NULL),(14,'2026-06-17 18:29:44.534798',17,2,NULL),(15,'2026-06-17 18:30:59.844688',19,2,NULL),(16,'2026-06-17 18:31:06.258267',19,2,NULL),(17,'2026-06-17 18:45:16.520994',19,2,NULL),(18,'2026-06-17 19:28:16.792743',19,2,NULL),(19,'2026-06-17 19:28:16.792743',19,2,NULL),(20,'2026-06-17 19:28:18.462808',19,2,NULL),(21,'2026-06-17 19:28:18.500845',19,2,NULL),(22,'2026-06-17 19:28:35.137291',13,2,NULL),(23,'2026-06-17 19:28:35.175671',13,2,NULL),(24,'2026-06-18 08:04:52.175307',17,2,NULL),(25,'2026-06-18 08:04:56.526323',18,2,NULL),(26,'2026-06-18 08:04:58.644006',19,2,NULL),(27,'2026-06-18 08:10:47.987229',19,2,NULL),(28,'2026-06-18 08:16:19.465952',19,2,NULL),(29,'2026-06-22 13:06:27.058782',13,2,NULL),(30,'2026-06-22 13:53:23.671788',20,2,'VIEW'),(31,'2026-06-22 13:53:30.818883',20,2,'FAVORITE'),(32,'2026-06-22 13:53:32.791059',22,2,'VIEW'),(33,'2026-06-22 13:58:31.173914',20,2,'VIEW'),(34,'2026-06-22 13:58:37.645918',20,2,'CART'),(35,'2026-06-27 03:46:36.786133',13,2,'CART');
/*!40000 ALTER TABLE `purchase_intentions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id_role` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id_role`),
  UNIQUE KEY `UKofx66keruapi6vyqpv6f2or37` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (2,'ADMIN'),(1,'CLIENTE');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `size`
--

DROP TABLE IF EXISTS `size`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `size` (
  `id_size` bigint NOT NULL AUTO_INCREMENT,
  `dimension` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id_size`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `size`
--

LOCK TABLES `size` WRITE;
/*!40000 ALTER TABLE `size` DISABLE KEYS */;
INSERT INTO `size` VALUES (1,'3x3 cm','Small'),(2,'2x2 cm','Pulga');
/*!40000 ALTER TABLE `size` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id_user` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `id_persona` bigint DEFAULT NULL,
  `id_role` bigint DEFAULT NULL,
  `auth_provider` varchar(255) DEFAULT NULL,
  `google_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK58t4q8mdd9y6kwcbosxbs1ycd` (`id_persona`),
  UNIQUE KEY `UKovh8xmu9ac27t18m56gri58i1` (`google_id`),
  KEY `FKt92dgi4412ywy3u8tm9jwdya5` (`id_role`),
  CONSTRAINT `FKl6yj3pslomlljh0ob6hfqqsvd` FOREIGN KEY (`id_persona`) REFERENCES `personas` (`id_persona`),
  CONSTRAINT `FKt92dgi4412ywy3u8tm9jwdya5` FOREIGN KEY (`id_role`) REFERENCES `roles` (`id_role`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'AireSeco@hotmail.com','$2a$10$CsDnr36SMX.LzN2ZR832g.WHqxyUjFjG6icCB0MMNSn7FSv98LG.a',1,2,'LOCAL',NULL),(2,'efrainqn16@gmail.com','$2a$10$CsDnr36SMX.LzN2ZR832g.WHqxyUjFjG6icCB0MMNSn7FSv98LG.a',2,1,'GOOGLE','107353456932557428951'),(3,'efrainqn1601@gmail.com','$2a$10$QoV3ZTYIkmnR8mFhAxhF4OqRabaJylX1n0TI.A5noRRfd6/.SYEtK',3,1,'LOCAL',NULL),(4,'ellanoteama160104@gmail.com','$2a$10$Dqghk8xthg7Sr2Pt19PFZecz1hFkv2Z1JVPrMf.aNlwtLTcty8m1S',4,1,'LOCAL',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-30 13:19:27
