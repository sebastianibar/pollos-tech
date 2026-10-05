package com.example.crudjavafx.model;

public class Session {

    private static Session instance;

    private final int staffId;
    private final String staffName;

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    private Session(int staffId, String staffName, String url, String user, String pass) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.dbUrl = url;
        this.dbUser = user;
        this.dbPassword = pass;
    }

    // Se llama solo en el Login
    public static void iniciarSesion(int staffId, String staffName, String url, String user, String pass) {
        if (instance == null) {
            instance = new Session(staffId, staffName, url, user, pass);
        }
    }

    public static Session getInstance() {
        if (instance == null) {
            throw new IllegalStateException("No hay sesión activa");
        }
        return instance;
    }

    public static void cerrarSesion() {
        instance = null;
    }

    public int getStaffId() {
        return staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }
}