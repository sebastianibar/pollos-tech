module com.example.crudjavafx {
    // Requerimientos básicos de JavaFX
    requires javafx.fxml;

    // Requerimiento para la base de datos
    requires java.sql;
    requires org.mybatis;
    requires javafx.controls;


    //estilos
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;

    // Exporta tu paquete principal para que se pueda lanzar
    exports com.example.crudjavafx;
    opens com.example.crudjavafx;



    opens com.example.crudjavafx.controller to javafx.fxml;
    opens com.example.crudjavafx.model to javafx.fxml, javafx.base;
}