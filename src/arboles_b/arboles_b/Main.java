package arboles_b;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int order = askOrder(sc);
        BTree tree = new BTree(order);

        int option;
        do {
            showMenu();
            option = readInt(sc, "Seleccione una opción: ");

            switch (option) {
                case 1:
                    System.out.println("\n=== VISUALIZACIÓN DEL ÁRBOL B ===");
                    if (tree.isEmpty()) {
                        System.out.println("El árbol está vacío.");
                    } else {
                        tree.printByLevels();
                    }
                    break;

                case 2: {
                    String name = readNonEmptyString(sc, "Ingrese el nombre a insertar: ");
                    System.out.println("\n=== INSERCIÓN DE \"" + name + "\" ===");
                    InsertResult result = tree.insert(name);

                    if (!result.inserted) {
                        System.out.println("El nombre \"" + name + "\" ya existe en el árbol. No se inserta (no se permiten duplicados).");
                    } else if (result.hadOverflow) {
                        System.out.println("Inserción CON SPLIT (hubo división de nodo).");
                    } else {
                        System.out.println("Inserción NORMAL (sin split).");
                    }
                    break;
                }

                case 3: {
                    String name = readNonEmptyString(sc, "Ingrese el nombre a buscar: ");
                    System.out.println("\n=== BÚSQUEDA DE \"" + name + "\" ===");
                    boolean exists = tree.search(name);
                    if (exists) {
                        System.out.println("El nombre \"" + name + "\" SÍ existe en el árbol.");
                    } else {
                        System.out.println("El nombre \"" + name + "\" NO existe en el árbol.");
                    }
                    break;
                }

                case 4: {
                    String name = readNonEmptyString(sc, "Ingrese el nombre a eliminar: ");
                    System.out.println("\n=== ELIMINACIÓN DE \"" + name + "\" ===");
                    boolean deleted = tree.delete(name);
                    if (deleted) {
                        System.out.println("El nombre \"" + name + "\" fue eliminado correctamente.");
                    } else {
                        System.out.println("El nombre \"" + name + "\" NO existe en el árbol. Nada que eliminar.");
                    }
                    break;
                }

                case 5:
                    System.out.println("Saliendo...");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }

            System.out.println();

        } while (option != 5);

        sc.close();
    }

    private static int askOrder(Scanner sc) {
        int order;
        do {
            order = readInt(sc, "Ingrese el orden del Árbol B (4 - 9): ");

            if (order < 4 || order > 9) {
                System.out.println("Error: el orden debe estar entre 4 y 9.");
            }

        } while (order < 4 || order > 9);

        return order;
    }

    private static void showMenu() {
        System.out.println("=================================");
        System.out.println("         MENÚ ÁRBOL B");
        System.out.println("=================================");
        System.out.println("1. Visualizar árbol");
        System.out.println("2. Insertar nombre");
        System.out.println("3. Buscar nombre");
        System.out.println("4. Eliminar nombre");
        System.out.println("5. Salir");
    }

    private static int readInt(Scanner sc, String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida.");
            }
        }
    }

    private static String readString(Scanner sc, String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    private static String readNonEmptyString(Scanner sc, String message) {
        while (true) {
            String value = readString(sc, message);
            if (!value.isEmpty()) return value;
            System.out.println("Error: el nombre no puede estar vacío.");
        }
    }
}
