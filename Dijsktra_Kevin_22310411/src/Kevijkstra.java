import java.util.PriorityQueue;
import java.util.Vector;

public class Kevijkstra
{
    public static Vector<Integer> caminoCorto(MatrizAdy matrizAdy, int inicio, int destino)
    {
        PriorityQueue<Filo_Figura> colaPrioridad = new PriorityQueue<>();

        Vector<Boolean> visitado = new Vector<>();
        Vector<Integer> previo = new Vector<>();
        Vector<Double> distancia = new Vector<>();
        Vector<Integer> camino = new Vector<>();

        int numVertices = matrizAdy.size(), actual, adyacente;
        double peso;

        for(int i = 0; i < numVertices ; i++)
        {
            distancia.add(Double.MAX_VALUE);
            visitado.addElement(false);
            previo.add(-1);
        }

        colaPrioridad.add(new Filo_Figura(inicio, 0));
        distancia.set(inicio, 0d);

        while(!colaPrioridad.isEmpty())
        {
            actual = colaPrioridad.element().dest;
            colaPrioridad.remove();

            if(visitado.get(actual))
            {
                continue;
            }

            visitado.set(actual, true);

            for(Filo_Figura n : matrizAdy.getEdgeList(actual))
            {
                adyacente = n.dest;
                peso = n.weight;

                if(!visitado.get(adyacente))
                {
                    if(distancia.get(actual) + peso < distancia.get(adyacente))
                    {
                        distancia.set(adyacente, distancia.get(actual) + peso);
                        previo.set(adyacente, actual);
                        colaPrioridad.add(new Filo_Figura(adyacente, distancia.get(adyacente)));
                    }
                }
            }
        }

        while(previo.get(destino) != -1)
        {
            camino.insertElementAt(destino, 0);
            destino = previo.get(destino);
        }

        return camino;
    }
}
