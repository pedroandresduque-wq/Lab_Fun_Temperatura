import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

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
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.JTextArea;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import datechooser.beans.DateChooserCombo;
import entidades.CambioTemperatura;
import servicios.CalculoTemperaturaServicio;
import servicios.CambioTemperaturaServicio;

public class FrmCambiosTemperaturas extends JFrame {

    private JComboBox cmbCiudad;
    private DateChooserCombo dccDesde, dccHasta;
    private JTabbedPane tpCambiosTemperatura;
    private JPanel pnlGrafica;
    private JPanel pnlEstadisticas;

    private JPanel pnlTemperatura;
    private JTextArea txtTemperaturas;

    private List<String> ciudades;
    private List<CambioTemperatura> cambiosTemperaturas;

    public FrmCambiosTemperaturas() {

        setTitle("Cambios de Temperaturas");
        setSize(700, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JToolBar tb = new JToolBar();

        JButton btnGraficar = new JButton();
        btnGraficar.setIcon(new ImageIcon(getClass().getResource("/iconos/Grafica.png")));
        btnGraficar.setToolTipText("Grafica Cambios vs Fecha");
        btnGraficar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnGraficarClick();
            }
        });
        tb.add(btnGraficar);

        JButton btnCalcularEstadisticas = new JButton();
        btnCalcularEstadisticas.setIcon(new ImageIcon(getClass().getResource("/iconos/Datos.png")));
        btnCalcularEstadisticas.setToolTipText("Estadísticas de la ciudad seleccionada");
        btnCalcularEstadisticas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCalcularEstadisticasClick();
            }
        });
        tb.add(btnCalcularEstadisticas);

        JButton btnCalcularTempCiudad = new JButton();
        btnCalcularTempCiudad.setIcon(new ImageIcon(getClass().getResource("/iconos/mientrastanto.png")));
        btnCalcularTempCiudad.setToolTipText("Temperatura maxima y mínima de la ciudad seleccionada");
        btnCalcularTempCiudad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCalcularTempCiudadClick();
            }
        });
        tb.add(btnCalcularTempCiudad);

        JPanel pnlCambios = new JPanel();
        pnlCambios.setLayout(new BoxLayout(pnlCambios, BoxLayout.Y_AXIS));

        JPanel pnlDatosProceso = new JPanel();
        pnlDatosProceso.setPreferredSize(new Dimension(pnlDatosProceso.getWidth(), 50)); // Altura fija de 50px
        pnlDatosProceso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pnlDatosProceso.setLayout(null);

        JLabel lblCiudad = new JLabel("Ciudad");
        lblCiudad.setBounds(10, 10, 100, 25);
        pnlDatosProceso.add(lblCiudad);

        cmbCiudad = new JComboBox();
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

        tpCambiosTemperatura = new JTabbedPane();
        tpCambiosTemperatura.addTab("Gráfica", spGrafica);
        tpCambiosTemperatura.addTab("Estadísticas", pnlEstadisticas);

        pnlTemperatura = new JPanel(new BorderLayout());
        txtTemperaturas = new JTextArea();
        txtTemperaturas.setEditable(false);
        txtTemperaturas.setLineWrap(true);
        txtTemperaturas.setWrapStyleWord(true);
        JScrollPane spTemperatura = new JScrollPane(txtTemperaturas);
        pnlTemperatura.add(spTemperatura, BorderLayout.CENTER);

        tpCambiosTemperatura.addTab("Temperatura", pnlTemperatura);

        pnlCambios.add(pnlDatosProceso);
        pnlCambios.add(tpCambiosTemperatura);

        getContentPane().add(tb, BorderLayout.NORTH);
        getContentPane().add(pnlCambios, BorderLayout.CENTER);

        cargarDatos();
    }

    private void cargarDatos() {
        String nombreArchivo = System.getProperty("user.dir") + "/src/datos/CambiosTemperaturas.csv";
        cambiosTemperaturas = CambioTemperaturaServicio.getDatos(nombreArchivo);
        ciudades = CambioTemperaturaServicio.getCiudades(cambiosTemperaturas);

        DefaultComboBoxModel modelo = new DefaultComboBoxModel(ciudades.toArray());
        cmbCiudad.setModel(modelo);

    }

    private void btnGraficarClick() {
        if (cmbCiudad.getSelectedIndex() >= 0) {

            String ciudad = (String) cmbCiudad.getSelectedItem();
            LocalDate desde = dccDesde.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate hasta = dccHasta.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (ciudad.isEmpty() || hasta.isBefore(desde)) {
                JOptionPane.showMessageDialog(null, "Datos no válidos");
                return;
            }

            var diccionariosGrafica = CambioTemperaturaServicio
                    .extraer(CambioTemperaturaServicio.filtrar(ciudad, desde, hasta, cambiosTemperaturas));

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (var item : diccionariosGrafica.entrySet()) {
                var fecha = item.getKey();
                var valor = item.getValue();
                dataset.addValue(valor, "Temperatura", fecha.toString());
            }

            JFreeChart graficador = ChartFactory.createBarChart("Temperatura de " + ciudad + " vs Fecha", "Fecha",
                    "Temperatura (°C)", dataset, PlotOrientation.VERTICAL, false, true, false);

            ChartPanel pnlGraficador = new ChartPanel(graficador);
            pnlGraficador.setPreferredSize(new Dimension(600, 400));

            pnlGrafica.removeAll();
            pnlGrafica.setLayout(new BorderLayout());
            pnlGrafica.add(pnlGraficador, BorderLayout.CENTER);
            pnlGrafica.revalidate();
            tpCambiosTemperatura.setSelectedIndex(0);
        }
    }

    private void btnCalcularEstadisticasClick() {
        if (cmbCiudad.getSelectedIndex() >= 0) {

            String ciudad = (String) cmbCiudad.getSelectedItem();
            LocalDate desde = dccDesde.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate hasta = dccHasta.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            pnlEstadisticas.setLayout(new GridBagLayout());
            pnlEstadisticas.removeAll();

            var estadisticas = CambioTemperaturaServicio.getEstadisticas(ciudad, desde, hasta, cambiosTemperaturas);
            int fila = 0;
            for (var estadistica : estadisticas.entrySet()) {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = fila;
                pnlEstadisticas.add(new JLabel(estadistica.getKey()), gbc);
                gbc.gridx = 1;
                pnlEstadisticas.add(new JLabel(String.format("%.2f", estadistica.getValue())), gbc);
                fila++;
            }

            tpCambiosTemperatura.setSelectedIndex(1);

        }
    }

    private void btnCalcularTempCiudadClick() {
    if (cmbCiudad.getItemCount() == 0) {
        JOptionPane.showMessageDialog(this, "No hay ciudades en el combo.", "Atención",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    LocalDate desde = dccDesde.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate hasta = dccHasta.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    var resultados = CalculoTemperaturaServicio.calcularTemperaturas(
            cambiosTemperaturas, ciudades, desde, hasta);

    pnlTemperatura.removeAll();
    pnlTemperatura.setLayout(new GridBagLayout());

    if (resultados.isEmpty()) {
        pnlTemperatura.add(new JLabel("No hay datos en el rango seleccionado."));
    } else {
        int fila = 0;
        for (var entrada : resultados.entrySet()) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = fila;
            gbc.anchor = GridBagConstraints.WEST;
            pnlTemperatura.add(new JLabel(entrada.getKey()), gbc);

            gbc.gridx = 1;
            pnlTemperatura.add(new JLabel(String.format("%.2f", entrada.getValue())), gbc);
            fila++;
        }
    }

    pnlTemperatura.revalidate();
    pnlTemperatura.repaint();

    tpCambiosTemperatura.setSelectedIndex(2);
}
}