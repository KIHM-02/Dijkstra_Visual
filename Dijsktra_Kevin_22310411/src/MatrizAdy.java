import java.util.ArrayList;
import java.util.List;

public class MatrizAdy {
    private List<List<Filo_Figura>> data;
	
    public MatrizAdy(int size) {
        data = new ArrayList<>();

        for(int i = 0; i < size; i++) {
            data.add(new ArrayList<>());
        }
    }
	
    public void addEdge(int source, int dest, double weight, boolean directed) {
        data.get(source).add(new Filo_Figura(dest, weight));

        if(!directed) {
            data.get(dest).add(new Filo_Figura(source, weight));
        }
    }

    public boolean isEdge(int source, int dest) {
        if(data.get(source).contains(new Filo_Figura(dest, 0)) || data.get(dest).contains(new Filo_Figura(source, 0))) {
            return true;
        }

        return false;
    }

    public List<Filo_Figura> getEdgeList(int source) {
        return data.get(source);
    }
	
    public int size() { 
        return data.size(); 
    }
}
