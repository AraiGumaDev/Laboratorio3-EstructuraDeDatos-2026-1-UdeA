package arboles_b;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BTree {

    private final int order;
    private Node root;

    public BTree(int order) {
        this.order = order;
        this.root = null;
    }

    // ✅ Verificar si el árbol está vacío
    public boolean isEmpty() {
        return root == null;
    }

    // ✅ Insertar nombre (String)
    public InsertResult insert(String key) {

        if (root == null) {
            root = new Node(true);
            root.keys.add(key);
            return new InsertResult(true, false);
        }

        InsertState state = new InsertState();
        SplitResult split = insertRecursive(root, key, state);

        if (!state.inserted) {
            return new InsertResult(false, false);
        }

        if (split != null) {
            Node newRoot = new Node(false);
            newRoot.keys.add(split.promotedKey);
            newRoot.children.add(split.left);
            newRoot.children.add(split.right);
            root = newRoot;
        }

        return new InsertResult(true, state.hadOverflow);
    }

    // 🔁 Inserción recursiva
    private SplitResult insertRecursive(Node node, String key, InsertState state) {

        int pos = findPosition(node, key);

        // ❌ evitar duplicados
        if (pos < node.keys.size() && node.keys.get(pos).equals(key)) {
            state.inserted = false;
            return null;
        }

        // 🌿 Si es hoja
        if (node.isLeaf) {
            insertSorted(node.keys, key);
            state.inserted = true;

            if (node.keys.size() >= order) {
                state.hadOverflow = true;
                return splitNode(node);
            }

            return null;
        }

        // 🌳 Nodo interno
        int childIndex = findPosition(node, key);
        SplitResult childSplit = insertRecursive(node.children.get(childIndex), key, state);

        if (!state.inserted)
            return null;

        if (childSplit != null) {
            node.keys.add(childIndex, childSplit.promotedKey);
            node.children.set(childIndex, childSplit.left);
            node.children.add(childIndex + 1, childSplit.right);

            if (node.keys.size() >= order) {
                state.hadOverflow = true;
                return splitNode(node);
            }
        }

        return null;
    }

    // 🔎 Encontrar posición correcta
    private int findPosition(Node node, String key) {
        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) {
            i++;
        }
        return i;
    }

    // 📌 Insertar ordenado
    private void insertSorted(List<String> keys, String key) {
        int i = 0;
        while (i < keys.size() && key.compareTo(keys.get(i)) > 0) {
            i++;
        }
        keys.add(i, key);
    }

    // ✂️ Dividir nodo (split)
    private SplitResult splitNode(Node node) {

        int totalKeys = node.keys.size();
        int mid = (totalKeys - 1) / 2;

        String promotedKey = node.keys.get(mid);

        Node left = new Node(node.isLeaf);
        Node right = new Node(node.isLeaf);

        // izquierda
        for (int i = 0; i < mid; i++) {
            left.keys.add(node.keys.get(i));
        }

        // derecha
        for (int i = mid + 1; i < totalKeys; i++) {
            right.keys.add(node.keys.get(i));
        }

        // hijos
        if (!node.isLeaf) {
            for (int i = 0; i <= mid; i++) {
                left.children.add(node.children.get(i));
            }

            for (int i = mid + 1; i < node.children.size(); i++) {
                right.children.add(node.children.get(i));
            }
        }

        return new SplitResult(promotedKey, left, right);
    }

    // 🌳 Imprimir árbol por niveles
    public void printByLevels() {

        if (root == null) {
            System.out.println("Árbol vacío.");
            return;
        }

        Queue<NodeLevel> queue = new LinkedList<>();
        queue.add(new NodeLevel(root, 0));

        int currentLevel = -1;

        while (!queue.isEmpty()) {
            NodeLevel current = queue.poll();

            if (current.level != currentLevel) {
                currentLevel = current.level;
                System.out.print("Nivel " + currentLevel + ": ");
            }

            System.out.print(current.node + "  ");

            if (!current.node.isLeaf) {
                for (Node child : current.node.children) {
                    queue.add(new NodeLevel(child, current.level + 1));
                }
            }

            if (queue.isEmpty() || queue.peek().level != currentLevel) {
                System.out.println();
            }
        }
    }
}
