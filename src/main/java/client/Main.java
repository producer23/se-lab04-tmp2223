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
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT); DataInputStream input = new DataInputStream(socket.getInputStream()); DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
            Scanner scanner = new Scanner(System.in);

            // enter login
            System.out.println(input.readUTF());
            output.writeUTF(scanner.nextLine().trim());

            for (int i = 0; i < 15; i++) {

                // enter command
                System.out.println(input.readUTF());

                // push command
                int myCommand = -1;
                try {
                    myCommand = Integer.parseInt(scanner.nextLine().trim());
                } catch (Exception e) {
                    System.out.println("Wrong answer!");
                }
                output.writeInt(myCommand);

                switch (myCommand) {
                    case 1 -> {
                        // name or id?
                        System.out.println(input.readUTF());

                        int nameOrID = -1;
                        try {
                            nameOrID = Integer.parseInt(scanner.nextLine().trim());
                        } catch (Exception e) {
                            System.out.println("Wrong answer!");
                        }
                        output.writeInt(nameOrID);

                        switch (nameOrID) {
                            case 1 -> {
                                // enter file name
                                System.out.println(input.readUTF());
                                output.writeUTF(scanner.nextLine());

                                // req
                                System.out.println(input.readUTF());

                                // file
                                int length = input.readInt();
                                if (length == -1) {
                                    System.out.println("The response says that this file is not found!");
                                    break;
                                }
                                byte[] data = new byte[length];
                                input.readFully(data, 0, data.length);

                                // new name for file
                                System.out.println(input.readUTF());
                                String fileName = scanner.nextLine();
                                File file = new File("src/main/java/client/data/" + fileName);
                                Files.write(file.toPath(), data);

                                System.out.println(input.readUTF());
                            }
                            case 2 -> {
                                // enter file id
                                System.out.println(input.readUTF());
                                long id = -1;
                                try {
                                    id = Long.parseLong(scanner.nextLine().trim());
                                } catch (Exception ignored) {
                                }
                                output.writeLong(id);

                                // req
                                System.out.println(input.readUTF());

                                // file
                                int length = input.readInt();
                                if (length == -1) {
                                    System.out.println("404 file not found!");
                                    break;
                                }
                                byte[] data = new byte[length];
                                input.readFully(data, 0, data.length);

                                // new name for file
                                System.out.println(input.readUTF());
                                String fileName = scanner.nextLine();
                                File file = new File("src/main/java/client/data/" + fileName);
                                Files.write(file.toPath(), data);

                                System.out.println(input.readUTF());
                            }
                        }
                    }
                    case 2 -> {
                        // enter name
                        System.out.println(input.readUTF());

                        // push name
                        String fileName = scanner.nextLine();
                        byte[] file;
                        try {
                            file = Files.readAllBytes(new File("src/main/java/client/data/" + fileName).toPath());
                        } catch (Exception e) {
                            System.out.println("File upload error!");
                            output.writeUTF("upload error");
                            break;
                        }
                        output.writeUTF(fileName);
                        output.writeInt(file.length);
                        output.write(file);

                        // req and final
                        System.out.println(input.readUTF());
                        System.out.println(input.readUTF());
                    }
                    case 3 -> {
                        // name or id?
                        System.out.println(input.readUTF());

                        int nameOrID = -1;
                        try {
                            nameOrID = Integer.parseInt(scanner.nextLine().trim());
                        } catch (Exception e) {
                            System.out.println("Wrong answer!");
                        }
                        output.writeInt(nameOrID);

                        switch (nameOrID) {
                            case 1 -> {
                                // enter file name
                                System.out.println(input.readUTF());
                                output.writeUTF(scanner.nextLine());

                                // req
                                System.out.println(input.readUTF());

                                System.out.println(input.readUTF());
                            }
                            case 2 -> {
                                // enter file id
                                System.out.println(input.readUTF());
                                long id = -1;
                                try {
                                    id = Long.parseLong(scanner.nextLine().trim());
                                } catch (Exception ignored) {
                                }
                                output.writeLong(id);

                                // req
                                System.out.println(input.readUTF());

                                System.out.println(input.readUTF());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
