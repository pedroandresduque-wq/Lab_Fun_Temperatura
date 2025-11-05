package entidades;

import java.time.LocalDate;

public class CambioTemperatura {

    private String ciudad;
    private LocalDate fecha;
    private double cambio;
    
    public CambioTemperatura(String ciudad, LocalDate fecha, double cambio) {
        this.ciudad = ciudad;
        this.fecha = fecha;
        this.cambio = cambio;
    }

    public String getCiudad() {
        return ciudad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getCambio() {
        return cambio;
    }

}
