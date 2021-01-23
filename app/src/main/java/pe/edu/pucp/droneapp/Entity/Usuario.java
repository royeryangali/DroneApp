package pe.edu.pucp.droneapp.Entity;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String nombreUsuario;
    private String codigoUsuario;
    private String primaryKeyUsuario;
    private String rol;
    private int celularUsuario;

    public int getCelularUsuario() {
        return celularUsuario;
    }

    public void setCelularUsuario(int celularUsuario) {
        this.celularUsuario = celularUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(String codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public String getPrimaryKeyUsuario() {
        return primaryKeyUsuario;
    }

    public void setPrimaryKeyUsuario(String primaryKeyUsuario) {
        this.primaryKeyUsuario = primaryKeyUsuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
