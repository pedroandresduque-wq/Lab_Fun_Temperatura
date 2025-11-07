package presentacion;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import datechooser.beans.DateChooserCombo;
import servicios.TemperaturaService.EstadisticasTemperatura;
import servicios.TemperaturaService.TemperaturasExtremas;

public class FrmCambiosTemperaturas extends JFrame implements TemperaturasPresenter.VistaTemperaturas {

    private JComboBox<String> cmbCiudad;
    private DateChooserCombo dccDesde, dccHasta;
    private JTabbedPane tpCambiosTemperatura;
    private JPanel pnlGrafica;
    private JPanel pnlEstadisticas;
    private JPanel pnlTemperatura;
    private JTextArea txtTemperaturas;
    
    private final TemperaturasPresenter presenter;

    public FrmCambiosTemperaturas() {
        presenter = new TemperaturasPresenter(this);

        setTitle("Cambios de Temperaturas");
        setSize(700, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        JToolBar tb = new JToolBar();

        JButton btnGraficar = new JButton();
        btnGraficar.setIcon(new ImageIcon(getClass().getResource("/iconos/Grafica.png")));
        btnGraficar.setToolTipText("Grafica Cambios vs Fecha");
        btnGraficar.addActionListener(e -> btnGraficarClick());
        tb.add(btnGraficar);

        JButton btnCalcularEstadisticas = new JButton();
        btnCalcularEstadisticas.setIcon(new ImageIcon(getClass().getResource("/iconos/Datos.png")));
        btnCalcularEstadisticas.setToolTipText("Estadísticas de la ciudad seleccionada");
        btnCalcularEstadisticas.addActionListener(e -> btnCalcularEstadisticasClick());
        tb.add(btnCalcularEstadisticas);

        JButton btnCalcularTempCiudad = new JButton();
        btnCalcularTempCiudad.setIcon(new ImageIcon(getClass().getResource("/iconos/mientrastanto.png")));
        btnCalcularTempCiudad.setToolTipText("Temperatura máxima y mínima de la ciudad seleccionada");
        btnCalcularTempCiudad.addActionListener(e -> btnCalcularTempCiudadClick());
        tb.add(btnCalcularTempCiudad);

        JPanel pnlCambios = new JPanel();
        pnlCambios.setLayout(new BoxLayout(pnlCambios, BoxLayout.Y_AXIS));

        JPanel pnlDatosProceso = new JPanel();
        pnlDatosProceso.setPreferredSize(new Dimension(pnlDatosProceso.getWidth(), 50));
        pnlDatosProceso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pnlDatosProceso.setLayout(null);

        JLabel lblCiudad = new JLabel("Ciudad");
        lblCiudad.setBounds(10, 10, 100, 25);
        pnlDatosProceso.add(lblCiudad);

        cmbCiudad = new JComboBox<>();
        cmbCiudad.setBounds(110, 10, 100, 25);
        pnlDatosProceso.add(cmbCiudad);

        dccDesde = new DateChooserCombo();
        dccDesde.setBounds(220, 10, 100, 25);
        pnlDatosProceso.add(dccDesde);

        dccHasta = new DateChooserCombo();
        dccHasta.setBounds(330, 10, 100, 25);
        pnlDatosProceso.add(dccHasta);

        pnlGrafica = new JPanel();
        JScrollPane spGrafica = new JScrollPane(pnlGrafica);

        pnlEstadisticas = new JPanel();

        pnlTemperatura = new JPanel(new BorderLayout());
        txtTemperaturas = new JTextArea();
        txtTemperaturas.setEditable(false);
        txtTemperaturas.setLineWrap(true);
        txtTemperaturas.setWrapStyleWord(true);
        JScrollPane spTemperatura = new JScrollPane(txtTemperaturas);
        pnlTemperatura.add(spTemperatura, BorderLayout.CENTER);

        tpCambiosTemperatura = new JTabbedPane();
        tpCambiosTemperatura.addTab("Gráfica", spGrafica);
        tpCambiosTemperatura.addTab("Estadísticas", pnlEstadisticas);
        tpCambiosTemperatura.addTab("Temperatura", pnlTemperatura);

        pnlCambios.add(pnlDatosProceso);
        pnlCambios.add(tpCambiosTemperatura);

        getContentPane().add(tb, BorderLayout.NORTH);
        getContentPane().add(pnlCambios, BorderLayout.CENTER);
    }

    private void cargarDatos() {
        String nombreArchivo = System.getProperty("user.dir") + "/src/datos/CambiosTemperaturas.csv";
        presenter.cargarDatos(nombreArchivo);
    }

    private void btnGraficarClick() {
        if (cmbCiudad.getSelectedIndex() >= 0) {
            String ciudad = (String) cmbCiudad.getSelectedItem();
            LocalDate desde = dccDesde.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate hasta = dccHasta.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            presenter.generarGrafica(ciudad, desde, hasta);
        }
    }

    private void btnCalcularEstadisticasClick() {
        if (cmbCiudad.getSelectedIndex() >= 0) {
            String ciudad = (String) cmbCiudad.getSelectedItem();
            LocalDate desde = dccDesde.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate hasta = dccHasta.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            presenter.calcularEstadisticas(ciudad, desde, hasta);
        }
    }

    private void btnCalcularTempCiudadClick() {
        if (cmbCiudad.getSelectedIndex() >= 0) {
            String ciudad = (String) cmbCiudad.getSelectedItem();
            LocalDate desde = dccDesde.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate hasta = dccHasta.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            presenter.calcularTemperaturasExtremas(ciudad, desde, hasta);
        }
    }

    @Override
    public void mostrarDatosGrafica(Map<LocalDate, Double> datos, String titulo) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        datos.forEach((fecha, valor) -> 
            dataset.addValue(valor, "Temperatura", fecha.toString())
        );

        JFreeChart graficador = ChartFactory.createBarChart(
            titulo, 
            "Fecha",
            "Temperatura (°C)", 
            dataset, 
            PlotOrientation.VERTICAL, 
            false, true, false
        );

        ChartPanel pnlGraficador = new ChartPanel(graficador);
        pnlGraficador.setPreferredSize(new Dimension(600, 400));

        pnlGrafica.removeAll();
        pnlGrafica.setLayout(new BorderLayout());
        pnlGrafica.add(pnlGraficador, BorderLayout.CENTER);
        pnlGrafica.revalidate();
        tpCambiosTemperatura.setSelectedIndex(0);
    }

    @Override
    public void mostrarEstadisticas(EstadisticasTemperatura estadisticas) {
        pnlEstadisticas.setLayout(new GridBagLayout());
        pnlEstadisticas.removeAll();

        String[][] datos = {
            {"Promedio", String.format("%.2f", estadisticas.promedio())},
            {"Desviación Estándar", String.format("%.2f", estadisticas.desviacionEstandar())},
            {"Máximo", String.format("%.2f", estadisticas.maximo())},
            {"Mínimo", String.format("%.2f", estadisticas.minimo())},
            {"Moda", String.format("%.2f", estadisticas.moda())},
            {"Mediana", String.format("%.2f", estadisticas.mediana())}
        };

        int fila = 0;
        for (String[] dato : datos) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = fila;
            pnlEstadisticas.add(new JLabel(dato[0]), gbc);
            gbc.gridx = 1;
            pnlEstadisticas.add(new JLabel(dato[1]), gbc);
            fila++;
        }

        pnlEstadisticas.revalidate();
        tpCambiosTemperatura.setSelectedIndex(1);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mostrarTemperaturasExtremas(TemperaturasExtremas extremas) {
        StringBuilder sb = new StringBuilder();
        sb.append("Temperatura Máxima:\n");
        sb.append(String.format("  Valor: %.2f°C\n", extremas.maxima().valor()));
        sb.append(String.format("  Fecha: %s\n", extremas.maxima().fecha()));
        sb.append("\nTemperatura Mínima:\n");
        sb.append(String.format("  Valor: %.2f°C\n", extremas.minima().valor()));
        sb.append(String.format("  Fecha: %s", extremas.minima().fecha()));

        txtTemperaturas.setText(sb.toString());
        tpCambiosTemperatura.setSelectedIndex(2);
    }

    @Override
    public void actualizarCiudades(List<String> ciudades) {
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>(ciudades.toArray(new String[0]));
        cmbCiudad.setModel(modelo);
    }
}