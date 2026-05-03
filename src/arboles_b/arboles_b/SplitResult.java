package arboles_b;

public class SplitResult {
    String promotedKey;
    Node left;
    Node right;

    public SplitResult(String promotedKey, Node left, Node right) {
        this.promotedKey = promotedKey;
        this.left = left;
        this.right = right;
    }
}
