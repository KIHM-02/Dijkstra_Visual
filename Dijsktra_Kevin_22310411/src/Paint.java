import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

public class Paint extends JPanel {
	
    private static final Dimension SIZE = new Dimension(1000, 1000);
    private Image offScreenImageDrawed = null;
    private Graphics offScreenGraphicsDrawed = null;              
    private Timer timer = new Timer();
    
    private Nodo_conexion newLine = null;
    
    private boolean connected;
    private int indexNodeClicked, prevIndexNodeClicked, indexConnectorClicked, countNodes;
    private Vector<Integer> selection, path;
    private Nodo_pintar movingNode = null;
    private Vector<Nodo_pintar> nodeDrw;
    private Vector<Nodo_conexion> connDrw;
    private JPopupMenu jpmNodos, jpmConectors;
    private JTextArea textArea;
    
    public Paint(JTextArea textArea) {
        initialize();

        this.connected = false;
        this.countNodes = 0;
        this.indexNodeClicked = -1;
        this.textArea = textArea;
        this.path = new Vector<>();
        this.selection = new Vector<>();
        this.nodeDrw = new Vector<>();
        this.connDrw = new Vector<>();
    }

    public void initialize() {
        this.timer.schedule(new AutomataTask(), 0, 60);
        this.setPreferredSize(SIZE);
        this.addMouseListener(new ClickPanel());
        this.addMouseMotionListener(new ClickMotionPanel());
        this.setLayout(null);

        jpmNodos = new JPopupMenu();
        jpmNodos.add(new MuestraNodos("Asignar Peso"));
        jpmNodos.add(new MuestraNodos("Cambiar nombre"));
        jpmNodos.add(new MuestraNodos("Eliminar nodo"));

        jpmConectors = new JPopupMenu();
        jpmConectors.add(new ActionMenuConnectors("Cambiar peso"));
        jpmConectors.add(new ActionMenuConnectors("Eliminar conexión"));
    }

    public void paint(Graphics g) {
        final Dimension d = this.getSize();

        if(offScreenImageDrawed == null) {                 
            offScreenImageDrawed = createImage(d.width, d.height);   
        }

        offScreenGraphicsDrawed = offScreenImageDrawed.getGraphics();      
        offScreenGraphicsDrawed.setColor(new Color(120, 200, 120));
        offScreenGraphicsDrawed.fillRect(0, 0, d.width, d.height) ;                           

        renderOffScreen(offScreenImageDrawed.getGraphics());
        g.drawImage(offScreenImageDrawed, 0, 0, null);
    }

    @Override
    public void update(Graphics g) {                                
        paint(g);
    }

    private void renderOffScreen(final Graphics g) {
        if(newLine != null) {
            newLine.paint(g);
        }

        for(Nodo_conexion cd : connDrw) {
            cd.paint(g); 
        }

        for(Nodo_pintar nd : nodeDrw) {
            nd.paint(g);
        }

        if(indexNodeClicked != -1 && indexConnectorClicked < nodeDrw.size()) {
            nodeDrw.get(indexNodeClicked).paint(g);;
        }
    }

    private class AutomataTask extends java.util.TimerTask {
        public void run() {
            if(!EventQueue.isDispatchThread()) {
                EventQueue.invokeLater(this);
            } else {
                if(Paint.this != null) {
                    Paint.this.repaint();
                }
            }
        } 
    }

    private class ClickPanel implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            jpmNodos.setVisible(false);
            jpmConectors.setVisible(false);

            clickNodeActions(e, sobreNodo(e.getX(), e.getY()));

            if(!sobreNodo(e.getX(), e.getY()) && sobreConector(e.getX(), e.getY())) {
                clickConnectorActions(e);
            } else {}

            newLine = null;
            connected = false;
        }

        private void clickNodeActions(MouseEvent e, boolean isOverNode_) {
            if(e.getButton() == MouseEvent.BUTTON1) {
                if(isOverNode_ && connected) {				
                    addConnector(newLine.getX1(), newLine.getY1(), nodeDrw.get(indexNodeClicked).getX(), nodeDrw.get(indexNodeClicked).getY(), true);
                } else if(!isOverNode_ && !connected){ 
                    addNode(e.getX(), e.getY());
                }
            } else if(e.getButton() == MouseEvent.BUTTON3) {
                if(isOverNode_ && !connected) {
                    if(!selection.isEmpty() && selection.contains(indexNodeClicked)) {
                        jpmNodos.remove(1);
                        jpmNodos.insert(new MuestraNodos("Deseleccionar nodo") , 1);
                    } else if(selection.isEmpty() || !selection.contains(indexNodeClicked)) {
                        if(jpmNodos.getComponents().length == 4) {
                            jpmNodos.remove(1);
                        }

                        jpmNodos.insert(new MuestraNodos("Seleccionar nodo") , 1);
                    }

                    jpmNodos.setLocation(e.getLocationOnScreen());
                    jpmNodos.setVisible(true);					
                }
            }
        }

        private void clickConnectorActions(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON3) {
                jpmConectors.setLocation(e.getLocationOnScreen());
                jpmConectors.setVisible(true);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {} 

    }

    private class ClickMotionPanel implements MouseMotionListener {
            int prevX;
            int prevY;

            @Override
            public void mouseDragged(MouseEvent e) {
                if(movingNode == null) {
                    prevX = e.getX();
                    prevY = e.getY();

                    movingNode = getNode(e);
                } else {
                    if(!connected) {
                        Point P = e.getPoint();
                        P.y -= 20;
                        P.x -= 20;
                        
                        if(Paint.this.contains(P)) {
                            movingNode.setPosition(movingNode.getX() + (e.getX() - prevX), movingNode.getY() + (e.getY() - prevY));

                            prevX = e.getX();
                            prevY = e.getY();
                        }

                        for (Nodo_conexion cd : connDrw) {
                            if(cd.getSrc() == indexNodeClicked) {
                                cd.setX1(movingNode.getX() + (e.getX() - prevX));
                                cd.setY1(movingNode.getY() + (e.getY() - prevY));
                            } else if(cd.getDest() == indexNodeClicked){
                                cd.setX2(movingNode.getX() + (e.getX() - prevX));
                                cd.setY2(movingNode.getY() + (e.getY() - prevY));
                            }
                        }

                        Paint.this.repaint();
                    }

                    lineMove(e);	
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                movingNode = null;
                lineMove(e);
            }

            private void lineMove(MouseEvent e) {
                if(newLine != null) {
                    newLine.setX2(e.getX());
                    newLine.setY2(e.getY());

                    Paint.this.repaint();
                }	
            }

            private Nodo_pintar getNode(MouseEvent e) {
                if(sobreNodo(e.getX(), e.getY())) {
                    return nodeDrw.get(indexNodeClicked);
                } else {
                    return null;
                }
            }		
    }

    private Boolean sobreNodo(int x, int y) {
        indexNodeClicked = -1;
        for(Nodo_pintar nd : nodeDrw) {
            if(nd.isMouseOver(x, y)) {
                indexNodeClicked++;
                return true;
            }
            
            indexNodeClicked++;
        }
        return false;
    }

    private Boolean sobreConector(int x, int y) {
        indexConnectorClicked = -1;
        
        for(Nodo_conexion cd : connDrw) {
            if(cd.isMouseOver(x, y)) {
                indexConnectorClicked++;
                return true;
            }

            indexConnectorClicked++;
        }
        
        return false;
    }

    class MuestraNodos extends AbstractAction {

        public MuestraNodos(String textOption) {
            this.putValue(Action.NAME, textOption);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            jpmNodos.setVisible(false);

            if(e.getActionCommand() == "Asignar Peso") {
                connected = true;
                prevIndexNodeClicked = indexNodeClicked;
                newLine = new Nodo_conexion(nodeDrw.get(indexNodeClicked).getX(), nodeDrw.get(indexNodeClicked).getY(), nodeDrw.get(indexNodeClicked).getX(), nodeDrw.get(indexNodeClicked).getY(), 0, 0, -1);
            }

            if(e.getActionCommand() == "Eliminar nodo") {
                    Paint.this.removeNode(indexNodeClicked);
                    indexNodeClicked = -1;
            }

            if(e.getActionCommand() == "Seleccionar nodo") {
                if(selection.size() < 2) {
                    selection.addElement(indexNodeClicked);
                } else if(selection.size() >= 2) {
                    nodeDrw.get(selection.lastElement()).changeColor();;
                    selection.setElementAt(indexNodeClicked, 1);
                }

                nodeDrw.get(selection.lastElement()).changeColor();

                if(selection.size() == 2) {
                    drawShortestPath();
                }
            }

            if(e.getActionCommand() == "Deseleccionar nodo") {
                nodeDrw.get(indexNodeClicked).changeColor();
                selection.remove((Object)indexNodeClicked);
                changeColorPath(false);
            }

            if(e.getActionCommand() == "Cambiar nombre") {
                String input = getNodeName();

                if(input != null) {
                    if(!input.isEmpty() && !input.trim().isEmpty()) {
                            nodeDrw.get(indexNodeClicked).setText(input);

                            if(selection.size() == 2) {
                                drawShortestPath();  
                            }
                    }
                }
            }
        }
    }

    class ActionMenuConnectors extends AbstractAction {

        public ActionMenuConnectors(String textOption) {
            this.putValue(Action.NAME, textOption);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            jpmConectors.setVisible(false);

            if(e.getActionCommand() == "Cambiar peso") {
                double weigth = getEdgeWeight();

                if(weigth > -1) {
                    connDrw.get(indexConnectorClicked).setEdgeWeight(weigth);
                }

                if(selection.size() == 2) {
                    drawShortestPath();
                }
            }

            if(e.getActionCommand() == "Eliminar conexion") {
                    Paint.this.removeConnector(indexConnectorClicked);
            }
        }
    }

    public void drawShortestPath() {
        changeColorPath(false);

        path = Kevijkstra.caminoCorto(getAdjacencyList(), selection.get(0), selection.get(1));
        path.insertElementAt(selection.get(0), 0);
        changeColorPath(true);
        StringBuilder sPath = new StringBuilder();

        if(path.size() -1 > 0) {
            sPath.append("Distancia de la ruta más corta " + currentWeigth + ": ");
        } else {
            sPath.append("No existe la ruta, verificar la direccion del nodo ");
        }

        for (Integer i : path) {
            sPath.append(nodeDrw.get(i).getText());

            if(i != path.lastElement()) {
                    sPath.append(" --> ");
            }
        }
        
        textArea.setText(sPath.toString());
    }

    double currentWeigth = 0;

    public void changeColorPath(boolean op) {
        currentWeigth = 0;

        if(!op) {
            textArea.setText("");
        }

        for(int i = 0; i < path.size()-1; i++) {
            for(Nodo_conexion cd : connDrw) {
                if(cd.getSrc() == path.get(i) && cd.getDest() == path.get(i+1)){
                    cd.changeColor(op);
                    currentWeigth += cd.getEdgeWeight();
                }
            }
        }
    }

    private boolean isEdge(int source, int dest) {
        for(Nodo_conexion cd : connDrw) {
            if((cd.getSrc() == source && cd.getDest() == dest) || (cd.getSrc() == dest && cd.getDest() == source)){ 
                return true;
            }
        }
        
        return false;
    }

    private double getEdgeWeight() {
        double weight = -1;

        do {
            String input = JOptionPane.showInputDialog(new JFrame(), "Peso de la conexión");

            if(input == null) {				
                return -1;
            }

            try {
                weight = Double.parseDouble(input);

                if(weight < 0) {
                    weight = -1;
                    throw new Exception();
                }

                return weight;
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(new JFrame(), "¡Valor no valido!!!", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } while(weight == -1);

        return -1;
    }

    private String getNodeName() {
        String input = JOptionPane.showInputDialog(new JFrame(), "Nombre del nodo");
        return input;
    }

    private void addNode(int x, int y) {
        String input = getNodeName();

        if(input != null){ 
            if(input.isEmpty()) {
                input = "" + countNodes;
            }

            unselectNodes();
            nodeDrw.addElement(new Nodo_pintar(x, y, 40, input));
            countNodes++;
        }
    }

    private void addConnector(int x1, int y1, int x2, int y2, boolean directed) {
        if(prevIndexNodeClicked != indexNodeClicked) {
            if(!isEdge(prevIndexNodeClicked, indexNodeClicked)) {
                double weigth = getEdgeWeight();

                if(weigth > -1) {
                    unselectNodes();
                    connDrw.addElement(new Nodo_conexion(x1, y1, x2, y2, prevIndexNodeClicked, indexNodeClicked, weigth));
                }

                newLine = null;
            } else {				
                newLine = null;
                JOptionPane.showMessageDialog(new JFrame(), "Ya fue creado el nodo anteriormente", "Error", JOptionPane.WARNING_MESSAGE);
            }

            connected = false;
        }
    }

    private void removeNode(int index) {
        unselectNodes();
        nodeDrw.remove(index);
        connDrw.removeAll(searchListIndexConnector(index));

        for (Nodo_conexion cd : connDrw) {
            int s = cd.getSrc();
            int d = cd.getDest();

            if(s >= index && s > 0) {
                cd.setSrc(s-1);
            }

            if(d >= index && d > 0) {
                cd.setDest(d -1);
            }
        }
    }

    private void removeConnector(int index) {
        unselectNodes();
        connDrw.remove(index);
    }

    private void unselectNodes() {
        changeColorPath(false);
        
        for(Integer i : selection) {
            nodeDrw.elementAt(i).changeColor();
        }
        
        selection.clear();
    }


    private MatrizAdy getAdjacencyList() {
        MatrizAdy newAdLst = new MatrizAdy(nodeDrw.size());

        for(Nodo_conexion cd : connDrw) {
            newAdLst.addEdge(cd.getSrc(), cd.getDest(), cd.getEdgeWeight(), true);
        }

        return newAdLst;
    }

    private Vector<Nodo_conexion> searchListIndexConnector(int indexNode) {
        Vector<Nodo_conexion> connects = new Vector<>();

        for(Nodo_conexion nc : connDrw) {
            if(nc.getSrc() == indexNode || nc.getDest() == indexNode) {
                connects.addElement(nc);
            }
        }

        return connects;
    }

}