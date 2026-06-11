-- Script para actualizar tamaños, dimensiones, precios y disponibilidad de productos
-- Orden oficial de tamaños: 1=Pulga, 2=Dije, 3=Mini, 4=Small, 5=Super Small

-- Primero, eliminamos todos los registros existentes en las tablas relacionadas
DELETE FROM product_size_pricing;
DELETE FROM product_sizes;

-- Actualizamos la tabla size con el nuevo orden, nombres, dimensiones y sortOrder
-- Orden: Pulga (1) -> Dije (2) -> Mini (3) -> Small (4) -> Super Small (5)
UPDATE size SET name = 'Pulga', dimension = '0.8 cm x 0.8 cm', sort_order = 1 WHERE id_size = 1;
UPDATE size SET name = 'Dije', dimension = '1.5 cm x 1.5 cm', sort_order = 2 WHERE id_size = 2;
UPDATE size SET name = 'Mini', dimension = '2.5 cm x 2.5 cm', sort_order = 3 WHERE id_size = 3;
UPDATE size SET name = 'Small', dimension = '5 cm x 5 cm', sort_order = 4 WHERE id_size = 4;
UPDATE size SET name = 'Super Small', dimension = '8 cm x 8 cm', sort_order = 5 WHERE id_size = 5;

-- Precios por tamaño:
-- Pulga: S/ 0.65 | Dije: S/ 0.70 | Mini: S/ 1.00 | Small: S/ 2.50 | Super Small: S/ 8.00

-- Insertar tamaños disponibles para cada producto (product_sizes)
-- Productos 1-18: Dije (2), Mini (3), Pulga (1)
INSERT INTO product_sizes (id_product, id_size) VALUES
-- Productos 1-6 (Dijes)
(1, 2), (1, 3), (1, 1),
(2, 2), (2, 3), (2, 1),
(3, 2), (3, 3), (3, 1),
(4, 2), (4, 3), (4, 1),
(5, 2), (5, 3), (5, 1),
(6, 2), (6, 3), (6, 1),
-- Productos 7-12 (Minis)
(7, 2), (7, 3), (7, 1),
(8, 2), (8, 3), (8, 1),
(9, 2), (9, 3), (9, 1),
(10, 2), (10, 3), (10, 1),
(11, 2), (11, 3), (11, 1),
(12, 2), (12, 3), (12, 1),
-- Productos 13-18 (Pulgas)
(13, 2), (13, 3), (13, 1),
(14, 2), (14, 3), (14, 1),
(15, 2), (15, 3), (15, 1),
(16, 2), (16, 3), (16, 1),
(17, 2), (17, 3), (17, 1),
(18, 2), (18, 3), (18, 1);

-- Producto 19 (Delfin Zafiro): Pulga (1), Small (4)
INSERT INTO product_sizes (id_product, id_size) VALUES (19, 1), (19, 4);

-- Producto 20 (Flamengo Rosa del Tropico): Small (4), Super Small (5)
INSERT INTO product_sizes (id_product, id_size) VALUES (20, 4), (20, 5);

-- Producto 21 (Llama Tradicional): Pulga (1), Small (4)
INSERT INTO product_sizes (id_product, id_size) VALUES (21, 1), (21, 4);

-- Producto 22 (Lobo Marino Ambar): Small (4), Super Small (5)
INSERT INTO product_sizes (id_product, id_size) VALUES (22, 4), (22, 5);

-- Producto 23 (Pez Palometa Dorado): Small (4)
INSERT INTO product_sizes (id_product, id_size) VALUES (23, 4);

-- Producto 24 (Vaca Lola): Small (4)
INSERT INTO product_sizes (id_product, id_size) VALUES (24, 4);

-- Producto 25 (Caballo Pucara Rojo): Small (4), Super Small (5)
INSERT INTO product_sizes (id_product, id_size) VALUES (25, 4), (25, 5);

-- Producto 26 (Casa de Belen): Small (4), Super Small (5)
INSERT INTO product_sizes (id_product, id_size) VALUES (26, 4), (26, 5);

-- Producto 27 (Celebracion Celestial): Small (4), Super Small (5)
INSERT INTO product_sizes (id_product, id_size) VALUES (27, 4), (27, 5);

-- Producto 28 (Ekeko de la Abundancia): Super Small (5)
INSERT INTO product_sizes (id_product, id_size) VALUES (28, 5);

-- Producto 29 (Ekeko de la Prosperidad): Super Small (5)
INSERT INTO product_sizes (id_product, id_size) VALUES (29, 5);

-- Producto 30 (Llama Acuatica): Super Small (5)
INSERT INTO product_sizes (id_product, id_size) VALUES (30, 5);

-- Insertar precios por tamaño para cada producto (product_size_pricing)
-- Productos 1-18: Dije (0.70), Mini (1.00), Pulga (0.65)
INSERT INTO product_size_pricing (id_product, id_size, price) VALUES
-- Productos 1-6 (Dijes)
(1, 2, 0.70), (1, 3, 1.00), (1, 1, 0.65),
(2, 2, 0.70), (2, 3, 1.00), (2, 1, 0.65),
(3, 2, 0.70), (3, 3, 1.00), (3, 1, 0.65),
(4, 2, 0.70), (4, 3, 1.00), (4, 1, 0.65),
(5, 2, 0.70), (5, 3, 1.00), (5, 1, 0.65),
(6, 2, 0.70), (6, 3, 1.00), (6, 1, 0.65),
-- Productos 7-12 (Minis)
(7, 2, 0.70), (7, 3, 1.00), (7, 1, 0.65),
(8, 2, 0.70), (8, 3, 1.00), (8, 1, 0.65),
(9, 2, 0.70), (9, 3, 1.00), (9, 1, 0.65),
(10, 2, 0.70), (10, 3, 1.00), (10, 1, 0.65),
(11, 2, 0.70), (11, 3, 1.00), (11, 1, 0.65),
(12, 2, 0.70), (12, 3, 1.00), (12, 1, 0.65),
-- Productos 13-18 (Pulgas)
(13, 2, 0.70), (13, 3, 1.00), (13, 1, 0.65),
(14, 2, 0.70), (14, 3, 1.00), (14, 1, 0.65),
(15, 2, 0.70), (15, 3, 1.00), (15, 1, 0.65),
(16, 2, 0.70), (16, 3, 1.00), (16, 1, 0.65),
(17, 2, 0.70), (17, 3, 1.00), (17, 1, 0.65),
(18, 2, 0.70), (18, 3, 1.00), (18, 1, 0.65);

-- Producto 19 (Delfin Zafiro): Pulga (0.65), Small (2.50)
INSERT INTO product_size_pricing (id_product, id_size, price) VALUES (19, 1, 0.65), (19, 4, 2.50);

-- Producto 20 (Flamengo Rosa del Tropico): Small (2.50), Super Small (8.00)
INSERT INTO product_size_pricing (id_product, id_size, price) VALUES (20, 4, 2.50), (20, 5, 8.00);

-- Producto 21 (Llama Tradicional): Pulga (0.65), Small (2.50)
INSERT INTO product_size_pricing (id_product, id_size, price) VALUES (21, 1, 0.65), (21, 4, 2.50);

-- Producto 22 (Lobo Marino Ambar): Small (2.50), Super Small (8.00)
INSERT INTO product_size_pricing (id_product, id_size, price) VALUES (22, 4, 2.50), (22, 5, 8.00);

-- Producto 23 (Pez Palometa Dorado): Small (2.50)
INSERT INTO product_size_pricing (id_product, id_size, price) VALUES (23, 4, 2.50);

-- Producto 24 (Vaca Lola): Small (2.50)
INSERT INTO product_size_pricing (id_product, id_size, price) VALUES (24, 4, 2.50);

-- Producto 25 (Caballo Pucara Rojo): Small (2.50), Super Small (8.00)
INSERT INTO product_size_pricing (id_product, id_size, price) VALUES (25, 4, 2.50), (25, 5, 8.00);

-- Producto 26 (Casa de Belen): Small (2.50), Super Small (8.00)
INSERT INTO product_size_pricing (id_product, id_size, price) VALUES (26, 4, 2.50), (26, 5, 8.00);

-- Producto 27 (Celebracion Celestial): Small (2.50), Super Small (8.00)
INSERT INTO product_size_pricing (id_product, id_size, price) VALUES (27, 4, 2.50), (27, 5, 8.00);

-- Producto 28 (Ekeko de la Abundancia): Super Small (8.00)
INSERT INTO product_size_pricing (id_product, id_size, price) VALUES (28, 5, 8.00);

-- Producto 29 (Ekeko de la Prosperidad): Super Small (8.00)
INSERT INTO product_size_pricing (id_product, id_size, price) VALUES (29, 5, 8.00);

-- Producto 30 (Llama Acuatica): Super Small (8.00)
INSERT INTO product_size_pricing (id_product, id_size, price) VALUES (30, 5, 8.00);

-- Verificación de datos
SELECT 'Tabla size actualizada:' as verificacion;
SELECT id_size, name, dimension, sort_order FROM size ORDER BY sort_order;

SELECT 'Total de productos por tamaño:' as verificacion;
SELECT s.name as tamaño, COUNT(ps.id_product) as cantidad_productos
FROM size s
LEFT JOIN product_sizes ps ON s.id_size = ps.id_size
GROUP BY s.id_size, s.name, s.sort_order
ORDER BY s.sort_order;

SELECT 'Total de relaciones producto-tamaño:' as verificacion;
SELECT COUNT(*) as total_registros FROM product_sizes;

SELECT 'Total de registros de precios:' as verificacion;
SELECT COUNT(*) as total_registros FROM product_size_pricing;