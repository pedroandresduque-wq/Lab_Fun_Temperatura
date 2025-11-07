package presentacion;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import servicios.TemperaturaService;
import servicios.TemperaturaService.EstadisticasTemperatura;
import servicios.TemperaturaService.TemperaturasExtremas;
import entidades.CambioTemperatura;

public class TemperaturasPresenter {
    private final VistaTemperaturas vista;
    private List<CambioTemperatura> datos;
    private List<String> ciudades;

    public interface VistaTemperaturas {
        void mostrarDatosGrafica(Map<LocalDate, Double> datos, String titulo);
        void mostrarEstadisticas(EstadisticasTemperatura estadisticas);
        void mostrarError(String mensaje);
        void mostrarTemperaturasExtremas(TemperaturasExtremas extremas);
        void actualizarCiudades(List<String> ciudades);
    }

    public TemperaturasPresenter(VistaTemperaturas vista) {
        this.vista = vista;
    }

    public void cargarDatos(String rutaArchivo) {
        this.datos = TemperaturaService.cargarDatos(rutaArchivo);
        this.ciudades = TemperaturaService.obtenerCiudades(datos);
        vista.actualizarCiudades(ciudades);
    }

    public void generarGrafica(String ciudad, LocalDate desde, LocalDate hasta) {
        if (!validarParametros(ciudad, desde, hasta)) {
            return;
        }

        var datosGrafica = TemperaturaService.obtenerDatosGrafica(datos, desde, hasta, ciudad);
        vista.mostrarDatosGrafica(datosGrafica, "Temperatura de " + ciudad + " vs Fecha");
    }

    public void calcularEstadisticas(String ciudad, LocalDate desde, LocalDate hasta) {
        if (!validarParametros(ciudad, desde, hasta)) {
            return;
        }

        Optional<EstadisticasTemperatura> estadisticas = 
            TemperaturaService.calcularEstadisticas(datos, desde, hasta, ciudad);
        
        estadisticas.ifPresentOrElse(
            vista::mostrarEstadisticas,
            () -> vista.mostrarError("No hay datos para el período seleccionado")
        );
    }

    public void calcularTemperaturasExtremas(String ciudad, LocalDate desde, LocalDate hasta) {
        if (!validarParametros(ciudad, desde, hasta)) {
            return;
        }

        Optional<TemperaturasExtremas> extremas = 
            TemperaturaService.obtenerTemperaturasExtremas(datos, desde, hasta, ciudad);
        
        extremas.ifPresentOrElse(
            vista::mostrarTemperaturasExtremas,
            () -> vista.mostrarError("No hay datos para el período seleccionado")
        );
    }

    private boolean validarParametros(String ciudad, LocalDate desde, LocalDate hasta) {
        if (ciudad == null || ciudad.isEmpty()) {
            vista.mostrarError("Debe seleccionar una ciudad");
            return false;
        }

        if (desde == null || hasta == null) {
            vista.mostrarError("Debe seleccionar fechas válidas");
            return false;
        }

        if (hasta.isBefore(desde)) {
            vista.mostrarError("La fecha final debe ser posterior a la fecha inicial");
            return false;
        }

        return true;
    }
}