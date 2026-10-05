
-- procedimientos almacenados

--empleado

CREATE OR REPLACE PROCEDURE insertar_empleado(
    IN p_nombre VARCHAR,
    IN p_puesto VARCHAR,
    IN p_salario DOUBLE PRECISION,
    IN p_usuario VARCHAR,
    IN p_contraseña VARCHAR
    )
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO empleado (nombre, puesto,salario, usuario, contraseña)
    VALUES (p_nombre, p_puesto,p_salario::NUMERIC, p_usuario, p_contraseña);
END;
$$;



CREATE OR REPLACE PROCEDURE actualizar_empleado(
    IN p_id_empleado INT,
    IN p_nombre VARCHAR,
    IN p_puesto VARCHAR,
    IN p_salario DOUBLE PRECISION,
    IN p_usuario VARCHAR,
    IN p_contraseña VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE empleado
    SET
        nombre = p_nombre,
        puesto = p_puesto,
        salario = p_salario::NUMERIC,
        usuario = p_usuario,
        contraseña = p_contraseña
    WHERE
        id_empleado = p_id_empleado;
END;
$$;



CREATE OR REPLACE PROCEDURE eliminar_empleado(
    IN p_id_empleado INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM empleado
    WHERE id_empleado = p_id_empleado;
END;
$$;




CREATE OR REPLACE FUNCTION obtiene_empleados()
RETURNS TABLE(
    id_empleado INT,
    nombre VARCHAR,
    salario NUMERIC,
    puesto VARCHAR,
    usuario VARCHAR,
    contraseña VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT e.id_empleado, e.nombre,e.salario, e.puesto, e.usuario, e.contraseña
        FROM empleado e
        ORDER BY id_empleado ASC;
END;
$$;



-- proveedor

CREATE OR REPLACE FUNCTION obtener_proveedores()
RETURNS TABLE (
    id_proveedor INT,
    nombre VARCHAR,
    contacto VARCHAR,
    telefono VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT p.id_proveedor, p.nombre, p.contacto, p.telefono
    FROM Proveedor p
    ORDER BY p.id_proveedor;
END;
$$;



CREATE OR REPLACE PROCEDURE insertar_proveedor(
    IN p_nombre VARCHAR,
    IN p_contacto VARCHAR,
    IN p_telefono VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO Proveedor (nombre, contacto, telefono)
    VALUES (p_nombre, p_contacto, p_telefono);
END;
$$;



CREATE OR REPLACE PROCEDURE actualizar_proveedor(
    IN p_id_proveedor INT,
    IN p_nombre VARCHAR,
    IN p_contacto VARCHAR,
    IN p_telefono VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE Proveedor
    SET
        nombre = p_nombre,
        contacto = p_contacto,
        telefono = p_telefono
    WHERE
        id_proveedor = p_id_proveedor;
END;
$$;



CREATE OR REPLACE PROCEDURE eliminar_proveedor(
    IN p_id_proveedor INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM Proveedor
    WHERE id_proveedor = p_id_proveedor;
END;
$$;




-- producto

CREATE OR REPLACE FUNCTION obtener_productos()
RETURNS TABLE(
    id_producto INT,
    descripcion VARCHAR,
    categoria VARCHAR,
    stock INT,
    precio NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT p.id_producto, p.descripcion, p.categoria, p.stock, p.precio
    FROM Producto p
    ORDER BY p.id_producto ASC;
END;
$$;


CREATE OR REPLACE PROCEDURE insertar_producto(
    IN p_descripcion VARCHAR,
    IN p_categoria VARCHAR,
    IN p_stock INT,
    IN p_precio DOUBLE PRECISION
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO Producto (descripcion, categoria, stock, precio)
    VALUES (p_descripcion, p_categoria, p_stock, p_precio::NUMERIC);
END;
$$;


CREATE OR REPLACE PROCEDURE actualizar_producto(
    IN p_id INT,
    IN p_descripcion VARCHAR,
    IN p_categoria VARCHAR,
    IN p_stock INT,
    IN p_precio DOUBLE PRECISION
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE Producto
    SET descripcion = p_descripcion,
        categoria = p_categoria,
        stock = p_stock,
        precio = p_precio::NUMERIC
    WHERE id_producto = p_id;
END;
$$;


CREATE OR REPLACE PROCEDURE eliminar_producto(IN p_id INT)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM Producto WHERE id_producto = p_id;
END;
$$;



--cliente

CREATE OR REPLACE FUNCTION obtener_clientes()
RETURNS TABLE(
    id_cliente INT,
    nombre VARCHAR,
    telefono VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT c.id_cliente, c.nombre, c.telefono
    FROM Cliente c
    ORDER BY c.id_cliente ASC;
END;
$$;


CREATE OR REPLACE PROCEDURE insertar_cliente(
    IN c_nombre VARCHAR,
    IN c_telefono VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO Cliente (nombre, telefono)
    VALUES (c_nombre, c_telefono);
END;
$$;


CREATE OR REPLACE PROCEDURE actualizar_cliente(
    IN c_id_cliente INT,
    IN c_nombre VARCHAR,
    IN c_telefono VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE Cliente
    SET nombre = c_nombre,
        telefono = c_telefono

    WHERE id_cliente = c_id_cliente;
END;
$$;


CREATE OR REPLACE PROCEDURE eliminar_cliente(IN c_id_cliente INT)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM Cliente WHERE id_cliente = c_id_cliente;
END;
$$;






