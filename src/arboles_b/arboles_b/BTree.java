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

    public boolean isEmpty() {
        return root == null;
    }

    // Mínimo de claves por nodo no-raíz: ceil(m/2) - 1
    private int minKeys() {
        return (order + 1) / 2 - 1;
    }

    // Insertar nombre (String)
    public InsertResult insert(String key) {

        if (root == null) {
            root = new Node(true);
            root.keys.add(key);
            System.out.println("    -> Árbol vacío. Se crea la raíz con '" + key + "': " + root);
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
            System.out.println("    -> Se crea nueva RAÍZ: " + root);
        }

        return new InsertResult(true, state.hadOverflow);
    }

    // Inserción recursiva
    private SplitResult insertRecursive(Node node, String key, InsertState state) {

        int pos = findPosition(node, key);

        // Evitar duplicados
        if (pos < node.keys.size() && node.keys.get(pos).equals(key)) {
            state.inserted = false;
            return null;
        }

        // Si es hoja
        if (node.isLeaf) {
            System.out.println("    -> Insertando '" + key + "' en hoja " + node + ".");
            insertSorted(node.keys, key);
            state.inserted = true;
            System.out.println("       Hoja tras inserción: " + node);

            if (node.keys.size() >= order) {
                state.hadOverflow = true;
                return splitNode(node);
            }

            return null;
        }

        // Nodo interno
        int childIndex = findPosition(node, key);
        SplitResult childSplit = insertRecursive(node.children.get(childIndex), key, state);

        if (!state.inserted)
            return null;

        if (childSplit != null) {
            System.out.println("    -> Subiendo clave promovida '" + childSplit.promotedKey + "' al nodo interno " + node + ".");
            node.keys.add(childIndex, childSplit.promotedKey);
            node.children.set(childIndex, childSplit.left);
            node.children.add(childIndex + 1, childSplit.right);
            System.out.println("       Nodo interno tras absorción: " + node);

            if (node.keys.size() >= order) {
                state.hadOverflow = true;
                return splitNode(node);
            }
        }

        return null;
    }

    // Encontrar posición correcta
    private int findPosition(Node node, String key) {
        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) {
            i++;
        }
        return i;
    }

    // Insertar ordenado
    private void insertSorted(List<String> keys, String key) {
        int i = 0;
        while (i < keys.size() && key.compareTo(keys.get(i)) > 0) {
            i++;
        }
        keys.add(i, key);
    }

    // Dividir nodo (split)
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

        System.out.println("    -> SPLIT: nodo " + node + " tiene " + totalKeys + " claves (orden " + order + "), desbordamiento.");
        System.out.println("       Clave promovida: '" + promotedKey + "'");
        System.out.println("       Nodo izquierdo: " + left);
        System.out.println("       Nodo derecho:   " + right);

        return new SplitResult(promotedKey, left, right);
    }

    // Imprimir árbol por niveles
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

    // ============================================================
    // BÚSQUEDA
    // ============================================================
    public boolean search(String key) {
        if (root == null) {
            System.out.println("    -> Árbol vacío.");
            return false;
        }
        int[] visited = {0};
        boolean found = searchRecursive(root, key, 0, visited);
        System.out.println("    -> Nodos visitados en total: " + visited[0]);
        return found;
    }

    private boolean searchRecursive(Node node, String key, int level, int[] visited) {
        visited[0]++;
        System.out.println("    -> Nivel " + level + ": revisando " + node);

        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) {
            i++;
        }

        if (i < node.keys.size() && node.keys.get(i).equals(key)) {
            System.out.println("    -> Encontrado en " + node + " (nivel " + level + ", posición " + i + ").");
            return true;
        }

        if (node.isLeaf) {
            System.out.println("    -> No encontrado: se llegó a una hoja sin coincidencia.");
            return false;
        }

        System.out.println("    -> Bajando al hijo " + i + ".");
        return searchRecursive(node.children.get(i), key, level + 1, visited);
    }

    // ============================================================
    // ELIMINACIÓN
    // ============================================================
    public boolean delete(String key) {
        if (root == null) {
            System.out.println("    -> El árbol está vacío. No hay nada que eliminar.");
            return false;
        }

        if (!containsKey(root, key)) {
            return false;
        }

        deleteRecursive(root, key);

        if (root.keys.isEmpty()) {
            if (root.isLeaf) {
                System.out.println("    -> La raíz quedó vacía. El árbol ahora está vacío.");
                root = null;
            } else {
                System.out.println("    -> La raíz quedó vacía tras una fusión. Su único hijo se promueve como nueva raíz.");
                root = root.children.get(0);
            }
        }

        return true;
    }

    // Verificar si una clave existe (solo para validar antes de borrar)
    private boolean containsKey(Node node, String key) {
        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) i++;
        if (i < node.keys.size() && node.keys.get(i).equals(key)) return true;
        if (node.isLeaf) return false;
        return containsKey(node.children.get(i), key);
    }

    // Eliminación recursiva
    private void deleteRecursive(Node node, String key) {
        int pos = findPosition(node, key);
        boolean foundHere = pos < node.keys.size() && node.keys.get(pos).equals(key);

        if (foundHere) {
            if (node.isLeaf) {
                // CASO 1: clave en una hoja
                System.out.println("    -> CASO 1: '" + key + "' está en la hoja " + node + ". Se elimina directamente.");
                node.keys.remove(pos);
                System.out.println("       Hoja resultante: " + node);
            } else {
                // CASO 2: clave en un nodo interno
                deleteFromInternal(node, pos, key);
            }
        } else {
            if (node.isLeaf) {
                // No debería ocurrir porque ya validamos existencia, pero por seguridad:
                return;
            }

            // La clave no está aquí: hay que descender al hijo correspondiente.
            // Si ese hijo está en el mínimo, primero se ajusta (Caso 2a o 2b).
            int childIdx = pos;
            if (node.children.get(childIdx).keys.size() == minKeys()) {
                System.out.println("    -> Aviso: el hijo " + childIdx + " " + node.children.get(childIdx)
                        + " está en el mínimo (" + minKeys() + " claves). Se ajusta antes de descender.");
                childIdx = ensureChildHasEnoughKeys(node, childIdx);
            }

            deleteRecursive(node.children.get(childIdx), key);
        }
    }

    // Eliminar clave que está en un nodo interno
    private void deleteFromInternal(Node node, int pos, String key) {
        Node leftChild = node.children.get(pos);
        Node rightChild = node.children.get(pos + 1);

        if (leftChild.keys.size() > minKeys()) {
            // CASO 3 (eliminación en nodo interno): reemplazo por PREDECESOR
            String pred = getPredecessor(leftChild);
            System.out.println("    -> CASO 3 (eliminación en nodo interno): '" + key + "' está en " + node
                    + ". El subárbol izquierdo " + leftChild + " tiene claves de sobra.");
            System.out.println("       Se reemplaza '" + key + "' con su predecesor '" + pred + "' y se elimina '" + pred + "' de la hoja original.");
            node.keys.set(pos, pred);
            deleteRecursive(leftChild, pred);

        } else if (rightChild.keys.size() > minKeys()) {
            // CASO 3 (eliminación en nodo interno): reemplazo por SUCESOR
            String succ = getSuccessor(rightChild);
            System.out.println("    -> CASO 3 (eliminación en nodo interno): '" + key + "' está en " + node
                    + ". El subárbol derecho " + rightChild + " tiene claves de sobra.");
            System.out.println("       Se reemplaza '" + key + "' con su sucesor '" + succ + "' y se elimina '" + succ + "' de la hoja original.");
            node.keys.set(pos, succ);
            deleteRecursive(rightChild, succ);

        } else {
            // CASO 3 + CASO 2b: clave en interno, ambos hijos al mínimo, se fusionan
            System.out.println("    -> CASO 3 (eliminación en nodo interno) + CASO 2b (fusión): '" + key + "' está en " + node
                    + " y ambos subárboles vecinos están en el mínimo. Se fusionan junto con '" + key + "'.");
            mergeChildren(node, pos);
            System.out.println("       Nodo fusionado: " + leftChild);
            deleteRecursive(leftChild, key);
        }
    }

    // Predecesor: clave más a la derecha del subárbol izquierdo
    private String getPredecessor(Node node) {
        while (!node.isLeaf) {
            node = node.children.get(node.children.size() - 1);
        }
        return node.keys.get(node.keys.size() - 1);
    }

    // Sucesor: clave más a la izquierda del subárbol derecho
    private String getSuccessor(Node node) {
        while (!node.isLeaf) {
            node = node.children.get(0);
        }
        return node.keys.get(0);
    }

    // Asegurar que el hijo en idx tenga al menos minKeys+1 claves
    // (redistribución desde un hermano o fusión). Devuelve el índice
    // efectivo del hijo después del ajuste (puede cambiar si se fusionó
    // con el hermano izquierdo).
    private int ensureChildHasEnoughKeys(Node parent, int idx) {

        Node leftSibling = (idx > 0) ? parent.children.get(idx - 1) : null;
        Node rightSibling = (idx < parent.children.size() - 1) ? parent.children.get(idx + 1) : null;

        if (leftSibling != null && leftSibling.keys.size() > minKeys()) {
            System.out.println("       CASO 2a (préstamo desde el hermano izquierdo " + leftSibling + ").");
            borrowFromLeft(parent, idx);
            return idx;
        }

        if (rightSibling != null && rightSibling.keys.size() > minKeys()) {
            System.out.println("       CASO 2a (préstamo desde el hermano derecho " + rightSibling + ").");
            borrowFromRight(parent, idx);
            return idx;
        }

        // Ningún hermano tiene de sobra -> fusión (Caso 2b)
        if (rightSibling != null) {
            System.out.println("       CASO 2b (fusión con el hermano derecho " + rightSibling + ").");
            mergeChildren(parent, idx);
            return idx;
        } else {
            System.out.println("       CASO 2b (fusión con el hermano izquierdo " + leftSibling + ").");
            mergeChildren(parent, idx - 1);
            return idx - 1;
        }
    }

    // Préstamo desde el hermano izquierdo
    private void borrowFromLeft(Node parent, int idx) {
        Node child = parent.children.get(idx);
        Node leftSibling = parent.children.get(idx - 1);

        // Bajar la clave separadora del padre al inicio del hijo
        child.keys.add(0, parent.keys.get(idx - 1));
        // Subir la última clave del hermano izquierdo al padre
        parent.keys.set(idx - 1, leftSibling.keys.remove(leftSibling.keys.size() - 1));

        // Si no es hoja, mover también el último hijo del hermano
        if (!child.isLeaf) {
            child.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
        }
    }

    // Préstamo desde el hermano derecho
    private void borrowFromRight(Node parent, int idx) {
        Node child = parent.children.get(idx);
        Node rightSibling = parent.children.get(idx + 1);

        // Bajar la clave separadora del padre al final del hijo
        child.keys.add(parent.keys.get(idx));
        // Subir la primera clave del hermano derecho al padre
        parent.keys.set(idx, rightSibling.keys.remove(0));

        // Si no es hoja, mover también el primer hijo del hermano
        if (!child.isLeaf) {
            child.children.add(rightSibling.children.remove(0));
        }
    }

    // Fusionar children[idx] + parent.keys[idx] + children[idx+1]
    private void mergeChildren(Node parent, int idx) {
        Node left = parent.children.get(idx);
        Node right = parent.children.get(idx + 1);

        // Bajar la clave separadora al nodo izquierdo
        left.keys.add(parent.keys.remove(idx));
        // Mover todas las claves del derecho al izquierdo
        left.keys.addAll(right.keys);

        // Si tienen hijos, también moverlos
        if (!left.isLeaf) {
            left.children.addAll(right.children);
        }

        // Quitar el hijo derecho del padre
        parent.children.remove(idx + 1);
    }
}
