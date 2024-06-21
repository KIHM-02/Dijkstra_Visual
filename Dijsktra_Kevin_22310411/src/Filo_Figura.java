public class Filo_Figura implements Comparable<Filo_Figura> {
    public int dest;
    public double weight;

    Filo_Figura(int dest , double weight){
        this.dest = dest;
        this.weight = weight;
    }

    public boolean equals(Object other) {
        return dest == ((Filo_Figura)other).dest;
    }

    @Override
    public int compareTo(Filo_Figura other) {
        if(weight > other.weight) {
            return 1;
        }

        if(weight == other.weight) {
            return 0;
        }

        return -1;
    }
}
