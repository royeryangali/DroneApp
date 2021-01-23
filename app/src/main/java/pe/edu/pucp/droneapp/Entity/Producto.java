package pe.edu.pucp.droneapp.Entity;

import java.io.Serializable;

public class Producto implements Serializable {
    private String primaryKeyProducto;

    private String nombreProducto;

    private String tipoProducto;

    private String nombreFotoProducto;

    private String estadoProducto;

    private int stockProducto;

    private String marcaProducto;

    private String caracteristicaProducto;

    private String incluyeProducto;
    private String ubicacionProducto;

    public String getUbicacionProducto() {
        return ubicacionProducto;
    }

    public void setUbicacionProducto(String ubicacionProducto) {
        this.ubicacionProducto = ubicacionProducto;
    }

    public String getIncluyeProducto() {
        return incluyeProducto;
    }

    public void setIncluyeProducto(String incluyeProducto) {
        this.incluyeProducto = incluyeProducto;
    }

    public String getCaracteristicaProducto() {
        return caracteristicaProducto;
    }

    public void setCaracteristicaProducto(String caracteristicaProducto) {
        this.caracteristicaProducto = caracteristicaProducto;
    }

    public String getMarcaProducto() {
        return marcaProducto;
    }

    public void setMarcaProducto(String marcaProducto) {
        this.marcaProducto = marcaProducto;
    }

    public String getPrimaryKeyProducto() {
        return primaryKeyProducto;
    }

    public void setPrimaryKeyProducto(String primaryKeyProducto) {
        this.primaryKeyProducto = primaryKeyProducto;
    }

    public int getStockProducto() {
        return stockProducto;
    }

    public void setStockProducto(int stockProducto) {
        this.stockProducto = stockProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getNombreFotoProducto() {
        return nombreFotoProducto;
    }

    public void setNombreFotoProducto(String nombreFotoProducto) {
        this.nombreFotoProducto = nombreFotoProducto;
    }

    public String getEstadoProducto() {
        return estadoProducto;
    }

    public void setEstadoProducto(String estadoProducto) {
        this.estadoProducto = estadoProducto;
    }
}
