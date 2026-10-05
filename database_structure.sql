-- Ejecutar este SQL antes de correr la aplicación

--Creación de tablas en base de datos

DROP TABLE IF EXISTS Detalle_Compra CASCADE;
DROP TABLE IF EXISTS Detalle_Pedido CASCADE;
DROP TABLE IF EXISTS Compra CASCADE;
DROP TABLE IF EXISTS Pedido CASCADE;
DROP TABLE IF EXISTS Producto CASCADE;
DROP TABLE IF EXISTS Proveedor CASCADE;
DROP TABLE IF EXISTS Cliente CASCADE;
DROP TABLE IF EXISTS Empleado CASCADE;


CREATE TABLE Empleado (
    id_empleado SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    puesto VARCHAR(50),
    salario NUMERIC(10, 2),
    usuario VARCHAR(50) UNIQUE,
    contraseña VARCHAR(50) NOT NULL

);


CREATE TABLE Cliente (
    id_cliente SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    telefono VARCHAR(50)
);


CREATE TABLE Proveedor (
    id_proveedor SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    contacto VARCHAR(50),
    telefono VARCHAR(50)
);


CREATE TABLE Producto (
    id_producto SERIAL PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL,
    categoria VARCHAR(50),
    stock INTEGER DEFAULT 0,
    precio NUMERIC(10, 2) NOT NULL
);



CREATE TABLE Compra (
    id_compra SERIAL PRIMARY KEY,
    fecha_compra DATE DEFAULT CURRENT_DATE,
    costo_total NUMERIC(10, 2) DEFAULT 0,
    id_proveedor INTEGER,
    CONSTRAINT fk_compra_proveedor
        FOREIGN KEY (id_proveedor)
        REFERENCES Proveedor(id_proveedor)
);


CREATE TABLE Pedido (
    id_pedido SERIAL PRIMARY KEY,
    fecha DATE DEFAULT CURRENT_DATE,
    total NUMERIC(10, 2) DEFAULT 0,
    estatus VARCHAR(50) DEFAULT 'Pendiente',
    metodo_pago VARCHAR(50),
    id_empleado INTEGER,
    id_cliente INTEGER,
    CONSTRAINT fk_pedido_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES Empleado(id_empleado),
    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES Cliente(id_cliente)
);



CREATE TABLE Detalle_Compra (
    id_detalle_compra SERIAL PRIMARY KEY,
    id_compra INTEGER NOT NULL,
    id_producto INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    costo_unitario NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_detcompra_compra
        FOREIGN KEY (id_compra)
        REFERENCES Compra(id_compra) ON DELETE CASCADE,
    CONSTRAINT fk_detcompra_producto
        FOREIGN KEY (id_producto)
        REFERENCES Producto(id_producto)
);


CREATE TABLE Detalle_Pedido (
    id_detalle_pedido SERIAL PRIMARY KEY,
    id_pedido INTEGER NOT NULL,
    id_producto INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_detpedido_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES Pedido(id_pedido) ON DELETE CASCADE,
    CONSTRAINT fk_detpedido_producto
        FOREIGN KEY (id_producto)
        REFERENCES Producto(id_producto)
);



--INSERTs iniciales

INSERT INTO Empleado (nombre, puesto, usuario, contraseña) VALUES
('David Gómez', 'Cajero', 'david', '1234'),
('Ana López', 'Gerente', 'ana', 'admin123'),
('Administrador', 'Administrador TI', 'admin', 'admin');



INSERT INTO Cliente (nombre, telefono) VALUES
('Juan Pérez', '6621234567'),
('Maria García', '6629876543');


INSERT INTO Proveedor (nombre, contacto, telefono) VALUES
('Avícola del Norte', 'ventas@avicola.com', '6621112222'),
('Bebidas Frescas S.A.', 'contacto@bebidas.com', '6623334444');


INSERT INTO Producto (descripcion, categoria, stock, precio) VALUES
('Pollo Entero', 'Comida', 50, 120.00),
('Medio Pollo', 'Comida', 30, 70.00),
('Orden de Papas', 'Complemento', 100, 45.00),
('Bebida Grande', 'Bebida', 200, 25.00),
('Ensalada Chica', 'Complemento', 40, 35.00);



INSERT INTO Compra (fecha_compra, costo_total, id_proveedor) VALUES
('2025-11-20', 4000.00, 1);


INSERT INTO Detalle_Compra (id_compra, id_producto, cantidad, costo_unitario) VALUES
(1, 1, 50, 80.00);



INSERT INTO Pedido (fecha, total, estatus, metodo_pago , id_empleado, id_cliente) VALUES
(CURRENT_DATE, 170.00, 'Pagado', 'Efectivo', 1, 1);



INSERT INTO Detalle_Pedido (id_pedido, id_producto, cantidad, subtotal) VALUES
(1, 1, 1, 120.00);


INSERT INTO Detalle_Pedido (id_pedido, id_producto, cantidad, subtotal) VALUES
(1, 4, 2, 50.00);

