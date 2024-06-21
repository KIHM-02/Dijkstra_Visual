import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Nodos_Diks extends JFrame {

    public Nodos_Diks() {
	try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException 
        		| IllegalAccessException | UnsupportedLookAndFeelException ex) {}
		
            this.setTitle("Dijkstra VISHUAL");
            this.setBounds(500, 300, 800, 500);
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);

            add(new FrameNodos());

            this.setVisible(true);
    }
}

    
class FrameNodos extends JPanel {
    private Paint paint;
    private JScrollPane spPaintingPanel;
    private JTextArea textArea;

    public FrameNodos() {
        initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());

        textArea = new JTextArea("Da click al panel para crear un nodo, da un click derecho para mostra el minimenu de opciones. Para mostrar direccion asigna un peso", 1, 1);
        textArea.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 20));
        textArea.setEditable(false);
        textArea.setBackground(new Color(120, 200, 120));
        textArea.setLineWrap(true);


        paint = new Paint(textArea);
        spPaintingPanel = new JScrollPane(paint, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        this.add(textArea, BorderLayout.NORTH);
        this.add(spPaintingPanel, BorderLayout.CENTER);
    }
}
