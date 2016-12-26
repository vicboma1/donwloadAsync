/**
 * Created by vicboma on 26/12/16.
 */
public class Pair<X,Y> {

    private X key;
    private Y value;

    public Pair(X key, Y value){
        this.key = key;
        this.value = value;
    }

    public static <X,Y>  Pair create(X key, Y value){
        return new Pair(key,value);
    }

    public X key() { return this.key;}
    public Y value() { return this.value; }
}