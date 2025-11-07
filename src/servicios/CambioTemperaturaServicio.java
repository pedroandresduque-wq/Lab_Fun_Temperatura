package servicios;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import entidades.CambioTemperatura;

public class CambioTemperaturaServicio {

    public static List<CambioTemperatura> getDatos(String nombreArchivo) {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("d/M/yyyy");
        try (Stream<String> lineas = Files.lines(Paths.get(nombreArchivo))) {

            return lineas.skip(1)
                    .map(linea -> linea.split(","))
                    .map(textos -> new CambioTemperatura(
                            textos[0].trim(),
                            LocalDate.parse(textos[1].trim(), formatoFecha),
                            Double.parseDouble(textos[2].trim().replace(",", "."))))
                    .collect(Collectors.toUnmodifiableList());

        } catch (Exception ex) {
            return List.of();
        }
    }

    public static List<String> getCiudades(List<CambioTemperatura> datos) {
        return datos.stream()
                .map(CambioTemperatura::getCiudad)
                .distinct()
                .sorted()
                .collect(Collectors.toUnmodifiableList());
    }

    public static List<CambioTemperatura> filtrar(String ciudad, LocalDate desde, LocalDate hasta,
            List<CambioTemperatura> datos) {
        return datos.stream()
                .filter(item -> item.getCiudad().equals(ciudad)
                        && !item.getFecha().isBefore(desde)
                        && !item.getFecha().isAfter(hasta))
                .collect(Collectors.toUnmodifiableList());
    }

    public static Map<LocalDate, Double> extraer(List<CambioTemperatura> datos) {
        return datos.stream()
                .collect(Collectors.toMap(
                        CambioTemperatura::getFecha,
                        CambioTemperatura::getCambio,
                        (oldV, newV) -> newV,
                        LinkedHashMap::new));
    }

    public static double getPromedio(List<Double> valores) {
        return valores.isEmpty() ? 0 : valores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    public static double getDesviacionEstandar(List<Double> valores) {
        var promedio = getPromedio(valores);
        return valores.isEmpty() ? 0
                : Math.sqrt(valores.stream()
                        .mapToDouble(item -> Math.pow(item - promedio, 2))
                        .average()
                        .orElse(0));
    }

    public static double getMinimo(List<Double> valores) {
        return valores.isEmpty() ? 0 : valores.stream().mapToDouble(Double::doubleValue).min().orElse(0);
    }

    public static double getMaximo(List<Double> valores) {
        return valores.isEmpty() ? 0 : valores.stream().mapToDouble(Double::doubleValue).max().orElse(0);
    }

    public static double getMediana(List<Double> valores) {
        if (valores.isEmpty()) return 0;
        var sorted = valores.stream().sorted().collect(Collectors.toList());
        var n = sorted.size();
        return n % 2 == 0 ? (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2 : sorted.get(n / 2);
    }

    public static double getModa(List<Double> valores) {
        return valores.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0.0);
    }

    public static Map<String, Double> getEstadisticas(String ciudad, LocalDate desde, LocalDate hasta,
            List<CambioTemperatura> datos) {

        var datosFiltrados = filtrar(ciudad, desde, hasta, datos);
        var cambios = datosFiltrados.stream()
                .map(CambioTemperatura::getCambio)
                .collect(Collectors.toUnmodifiableList());

        return Map.ofEntries(
            Map.entry("Promedio", getPromedio(cambios)),
            Map.entry("Desviación Estándar", getDesviacionEstandar(cambios)),
            Map.entry("Máximo", getMaximo(cambios)),
            Map.entry("Mínimo", getMinimo(cambios)),
            Map.entry("Moda", getModa(cambios)),
            Map.entry("Mediana", getMediana(cambios))
        );
    }

}
