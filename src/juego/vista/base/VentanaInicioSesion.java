package juego.vista.base;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class VentanaInicioSesion extends JFrame {
    private JPanel contentPane;
    private JTextField textUsuario;
    private JButton btnIniciar;

    public VentanaInicioSesion() {
        setTitle("Chinchón - Login");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 300, 150); // Un poco más grande para comodidad
        setLocationRelativeTo(null); // Centra la ventana en la pantalla

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        // Usamos GridLayout (2 filas, 2 columnas) para evitar dependencia de MigLayout
        contentPane.setLayout(new GridLayout(2, 2, 10, 10));

        JLabel lblUsuario = new JLabel("Usuario:");
        contentPane.add(lblUsuario);

        textUsuario = new JTextField();
        contentPane.add(textUsuario);
        textUsuario.setColumns(10);

        // Espacio vacío para la cuadrícula
        contentPane.add(new JLabel(""));

        btnIniciar = new JButton("Iniciar Sesión");
        contentPane.add(btnIniciar);

        // Permite que al apretar "Enter" se dispare el botón
        SwingUtilities.getRootPane(btnIniciar).setDefaultButton(btnIniciar);
    }

    // Este método lo usa VistaBase para conectar el botón con el Controlador
    public void onClickIniciar(ActionListener listener) {
        this.btnIniciar.addActionListener(listener);
    }

    // Este método lo usa VistaBase para obtener el String que escribió el usuario
    public String getGetNombreUsuario() {
        return this.textUsuario.getText().trim();
    }
}
