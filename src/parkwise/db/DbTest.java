package parkwise.db;

import parkwise.controller.ClientController;
import parkwise.controller.ParkingSessionController;
import parkwise.controller.VehicleController;

import java.sql.Connection;

public class DbTest {

    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection()) {

            System.out.println("Connected to Access successfully!");

            ClientController clientController = new ClientController();

            Object[] client = clientController.getClientByPhone("0501234567");

            if (client == null) {
                boolean added = clientController.addClient("Aya", "Arabas", "0501234567");
                System.out.println("Client added: " + added);
                client = clientController.getClientByPhone("0501234567");
            } else {
                System.out.println("Client already exists.");
            }

            if (client == null) {
                System.out.println("Client not found.");
                return;
            }

            System.out.println("Client found:");
            System.out.println("ID: " + client[0]);
            System.out.println("First Name: " + client[1]);
            System.out.println("Last Name: " + client[2]);
            System.out.println("Phone: " + client[3]);

            VehicleController vehicleController = new VehicleController();

            Object[] vehicle = vehicleController.getVehicleByNumber("12345678");

            if (vehicle == null) {
                boolean addedVehicle = vehicleController.addVehicle(
                        "12345678",
                        "Toyota Corolla",
                        "WHITE",
                        "MEDIUM",
                        1300,
                        (Integer) client[0]
                );
                System.out.println("Vehicle added: " + addedVehicle);
                vehicle = vehicleController.getVehicleByNumber("12345678");
            } else {
                System.out.println("Vehicle already exists.");
            }

            if (vehicle != null) {
                System.out.println("Vehicle found:");
                System.out.println("Number: " + vehicle[0]);
                System.out.println("Type: " + vehicle[1]);
                System.out.println("Color: " + vehicle[2]);
                System.out.println("Size: " + vehicle[3]);
                System.out.println("Weight: " + vehicle[4]);
                System.out.println("Client ID: " + vehicle[5]);
            } else {
                System.out.println("Vehicle not found.");
                return;
            }

            ParkingSessionController c = new ParkingSessionController();
            int clientId = (Integer) client[0];

            boolean firstTry = c.startSession("12345678", 1, clientId, "0501234567");
            System.out.println("First session attempt: " + firstTry);

            boolean secondTry = c.startSession("12345678", 1, clientId, "0501234567");
            System.out.println("Second session attempt: " + secondTry);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}