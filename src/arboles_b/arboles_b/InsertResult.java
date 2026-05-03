package arboles_b;

public class InsertResult {
    public boolean inserted;
    public boolean hadOverflow;

    public InsertResult(boolean inserted, boolean hadOverflow) {
        this.inserted = inserted;
        this.hadOverflow = hadOverflow;
    }
}