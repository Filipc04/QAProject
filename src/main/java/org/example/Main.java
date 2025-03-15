package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ILibraryStore store = new DbLibraryStore();
        LibraryService svc = new LibraryService(store);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Välkommen till bibliotekssystemet!");
        System.out.println("Ange ditt användar-ID (fyra siffror):");
        String userId = scanner.nextLine();

        boolean done = false;
        while (!done) {
            System.out.println("\nMeny:");
            System.out.println("1. Låna bok.");
            System.out.println("2. Returnera bok.");
            System.out.println("3. Registrera ny medlem.");
            System.out.println("4. Ta bort medlem.");
            System.out.println("5. Suspendera medlem.");
            System.out.println("9. Avsluta.");
            System.out.print("Välj (1-9): ");

            int selection;
            try {
                selection = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Felaktigt val, försök igen.");
                continue;
            }

            switch (selection) {
                case 1 -> {
                    System.out.println("Ange bokens ISBN:");
                    String bookId = scanner.nextLine();
                    if (svc.borrow(bookId, userId)) {
                        System.out.println("Boken lånad framgångsrikt!");
                    }
                }
                case 2 -> {
                    System.out.println("Ange bokens ISBN:");
                    String bookId = scanner.nextLine();
                    if (svc.returnBook(bookId, userId)) {
                        System.out.println("Boken returnerad framgångsrikt!");
                    }
                }
                case 3 -> {
                    System.out.println("Ange förnamn:");
                    String firstName = scanner.nextLine();
                    System.out.println("Ange ID (4 siffror):");
                    String id = scanner.nextLine();
                    System.out.println("Ange medlemsnivå (1=Undergraduate, 2=Postgraduate, 3=PhD, 4=Teacher):");
                    int level = Integer.parseInt(scanner.nextLine());

                    Member newMember = new Member(firstName, id, level) {
                        @Override
                        public Class<?> getDeclaringClass() {
                            return null;
                        }

                        @Override
                        public String getName() {
                            return null;
                        }

                        @Override
                        public int getModifiers() {
                            return 0;
                        }

                        @Override
                        public boolean isSynthetic() {
                            return false;
                        }
                    };
                    store.addMember(newMember);
                    System.out.println("Medlem registrerad!");
                }
                case 4 -> {
                    System.out.println("Ange ID för medlem som ska tas bort:");
                    String id = scanner.nextLine();
                    store.removeMember(id);
                    System.out.println("Medlem borttagen!");
                }
                case 5 -> {
                    System.out.println("Ange ID för medlem som ska suspenderas:");
                    String id = scanner.nextLine();
                    store.suspendMember(id);
                    System.out.println("Medlem suspenderad!");
                }
                case 9 -> done = true;
                default -> System.out.println(selection + " är inte ett giltigt val.");
            }
        }

        System.out.println("Hejdå!");
        scanner.close();
    }
}
