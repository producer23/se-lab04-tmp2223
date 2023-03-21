package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34522;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            Scanner scanner = new Scanner(System.in);

            // start
            for (int i = 0; i < 15; i++) {

                System.out.println("Enter action (1 - get a file, 2 - save a file, 3 - delete a file):");

                // enter action
                String action = scanner.nextLine();

                // push action
                output.writeUTF(action);

                switch (action) {
                    case "1" -> {
                        System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id):");

                        int nameOrID = -1;
                        try {
                            nameOrID = Integer.parseInt(scanner.nextLine().trim());
                        } catch (Exception e) {
                            System.out.println("Wrong answer!");
                        }
                        output.writeInt(nameOrID);

                        switch (nameOrID) {
                            case 1 -> {
                                System.out.println("Enter name:");
                                String serverFileName = scanner.nextLine();
                                output.writeUTF(serverFileName);

                                System.out.println("The request was sent.");

                                // get file from server
                                int length = input.readInt();
                                if (length == -1) {
                                    System.out.println("The response says that this file is not found!");
                                    break;
                                }
                                byte[] data = new byte[length];
                                input.readFully(data, 0, data.length);

                                System.out.println("The file was downloaded! Specify a name for it:");
                                String fileName = scanner.nextLine() + serverFileName.substring(serverFileName.indexOf("."));

                                File file = new File("src/main/java/client/data/" + fileName);
                                Files.write(file.toPath(), data);

                                System.out.println("File saved on the hard drive!");
                            }
                            case 2 -> {
                                System.out.println("Enter id:");
                                long id = -1;
                                try {
                                    id = Long.parseLong(scanner.nextLine());
                                } catch (Exception e) {
                                    System.out.println("Wrong answer!");
                                    output.writeLong(id);
                                    break;
                                }
                                output.writeLong(id);

                                System.out.println("The request was sent.");

                                // get file from server
                                int length = input.readInt();
                                if (length == -1) {
                                    System.out.println("The response says that this file is not found!");
                                    break;
                                }
                                byte[] data = new byte[length];
                                input.readFully(data, 0, data.length);

                                System.out.println("The file was downloaded! Specify a name for it:");
                                String fileName = scanner.nextLine() + input.readUTF();

                                File file = new File("src/main/java/client/data/" + fileName);
                                Files.write(file.toPath(), data);

                                System.out.println("File saved on the hard drive!");
                            }
                        }
                    }
                    case "2" -> {
                        System.out.println("Enter name of the file:");
                        String fileName = scanner.nextLine();

                        byte[] file;
                        try {
                            file = Files.readAllBytes(new File("src/main/java/client/data/" + fileName).toPath());
                        } catch (Exception e) {
                            System.out.println("File upload error!");
                            output.writeUTF("upload error");
                            break;
                        }

                        // 1
                        output.writeUTF(fileName);
                        // 2
                        output.writeInt(file.length);
                        // 3
                        output.write(file);

                        System.out.println("Enter name of the file to be saved on server:");
                        String serverFileName = scanner.nextLine();

                        // 4
                        output.writeUTF(serverFileName);
                        System.out.println("The request was sent.");

                        // 5
                        boolean status = input.readBoolean();
                        if (status) {
                            System.out.println("This file name is already used on the server!");
                            break;
                        } else {
                            System.out.println(input.readUTF());
                        }
                    }
                    case "3" -> {
                        System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id):");

                        int nameOrID = -1;
                        try {
                            nameOrID = Integer.parseInt(scanner.nextLine().trim());
                        } catch (Exception e) {
                            System.out.println("Wrong answer!");
                        }
                        output.writeInt(nameOrID);

                        switch (nameOrID) {
                            case 1 -> {
                                System.out.println("Enter name:");
                                output.writeUTF(scanner.nextLine());

                                System.out.println("The request was sent.");

                                System.out.println(input.readUTF());
                            }
                            case 2 -> {
                                System.out.println("Enter id:");
                                long id = -1;
                                try {
                                    id = Long.parseLong(scanner.nextLine());
                                } catch (Exception ignored) {
                                    System.out.println("Wrong answer!");
                                }
                                output.writeLong(id);

                                System.out.println("The request was sent.");

                                System.out.println(input.readUTF());
                            }
                        }
                    }
                    default -> {
                        output.writeUTF(action);
                        if (action.equals("exit")) {
                            System.exit(0);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
