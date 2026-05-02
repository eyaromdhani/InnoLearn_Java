
import model.user;
import service.UserService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    static UserService userService = new UserService();
    static Scanner sc = new Scanner(System.in);

    static void afficher() {
        try {
            List<user> users = userService.read();
            System.out.println("Total: " + users.size() + " utilisateurs");
            for (user u : users) {
                System.out.println("id: " + u.getId()
                        + " | name: " + u.getName()
                        + " | username: " + u.getUsername()
                        + " | email: " + u.getEmail()
                        + " | active: " + u.getActive()
                        + " | banned: " + u.getBanned());
            }
        } catch (SQLException e) {
            System.out.println("Affichage failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        // test create
        try {
            user u = new user();
            u.setName("John Doe");
            u.setUsername("johndoe");
            u.setEmail("john@example.com");
            u.setPasswordHash("hashedpassword123");
            u.setCountryCode("TN");
            u.setPhoneNumber("12345678");
            u.setRoles("[\"ROLE_USER\"]");
            u.setActive(true);
            u.setAvatarUrl(null);
            u.setVerificationKey(null);
            u.setKeyExpiresAt(null);
            u.setPhoneVerified(false);
            u.setFailedLoginAttempts(0);
            u.setLastFailedLoginAttempt(null);
            u.setBanned(false);
            u.setAdminHardwareKeyHash(null);
            u.setAdminFaceSignatureHash(null);
            userService.create(u);
            System.out.println("Create: OK");
        } catch (SQLException e) {
            System.out.println("Create failed: " + e.getMessage());
        }

        System.out.println("\n--- Liste apres Create ---");
        afficher();



        System.out.println("\n--- Liste apres Update ---");
        afficher();

        // test delete
        System.out.println("\n--- Delete ---");
        System.out.print("Donner l'id de l'utilisateur a supprimer: ");
        int deleteId = sc.nextInt();
        sc.nextLine();
        System.out.print("Etes-vous sur de vouloir supprimer l'utilisateur " + deleteId + " ? (o/n): ");
        String confirm = sc.nextLine();
        if (confirm.equalsIgnoreCase("o")) {
            try {
                userService.delete(deleteId);
                System.out.println("Delete: OK");
            } catch (SQLException e) {
                System.out.println("Delete failed: " + e.getMessage());
            }
        } else {
            System.out.println("Suppression annulee.");
        }

        System.out.println("\n--- Liste apres Delete ---");
        afficher();
    }
}