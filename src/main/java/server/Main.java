package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int PORT = 34522;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            while (true) {
                Session session = new Session(server.accept());
                session.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Session extends Thread {
    private final Socket socket;

    public Session(Socket socketForClient) {
        this.socket = socketForClient;
    }

    public void run() {
        try (DataInputStream input = new DataInputStream(socket.getInputStream());DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
            output.writeUTF("Enter your login:");
            String login = input.readUTF();

            UserData user = ServerData.getInstance().addUser(login);

            for (int i = 0; i < 15; i++) {
                output.writeUTF("Enter action (1 - get a file, 2 - save a file, 3 - delete a file):");
                int commandFromClient = input.readInt();

                switch (commandFromClient) {
                    case 1 -> {
                        output.writeUTF("Do you want to get the file by name or by id (1 - name, 2 - id):");

                        int nameOrID = input.readInt();

                        switch (nameOrID) {
                            case 1 -> {
                                output.writeUTF("Enter name:");
                                String fileName = input.readUTF();

                                output.writeUTF("The request was sent.");

                                byte[] data = ServerData.getInstance().getUser(login).getFile(fileName);
                                if (data != null) {
                                    output.writeInt(data.length);
                                } else {
                                    output.writeInt(-1);
                                    break;
                                }
                                output.write(data);

                                output.writeUTF("The file was downloaded! Specify a name for it:");

                                output.writeUTF("File saved on the hard drive!");
                            }
                            case 2 -> {
                                output.writeUTF("Enter id:");
                                long id = input.readLong();

                                output.writeUTF("The request was sent.");

                                byte[] data = ServerData.getInstance().getUser(login).getFile(id);
                                if (data != null) {
                                    output.writeInt(data.length);
                                } else {
                                    output.writeInt(-1);
                                    break;
                                }
                                output.write(data);

                                output.writeUTF("The file was downloaded! Specify a name for it:");

                                output.writeUTF("File saved on the hard drive!");
                            }
                        }
                    }
                    case 2 -> {
                        output.writeUTF("Enter name of the file:");
                        String fileName = input.readUTF();

                        if (fileName.equals("upload error")) {
                            break;
                        }

                        int length = input.readInt();
                        byte[] file = new byte[length];
                        input.readFully(file, 0, file.length);

                        output.writeUTF("The request was sent.");
                        long id = user.addFile(fileName, file);
                        output.writeUTF("Response says that file is saved! ID = " + id);
                    }
                    case 3 -> {
                        output.writeUTF("Do you want to get the file by name or by id (1 - name, 2 - id):");

                        int nameOrID = input.readInt();

                        switch (nameOrID) {
                            case 1 -> {
                                output.writeUTF("Enter name:");
                                String fileName = input.readUTF();

                                output.writeUTF("The request was sent.");

                                boolean deleted = ServerData.getInstance().getUser(login).deleteFile(fileName);

                                if (deleted) {
                                    output.writeUTF("The response says that this file was deleted successfully!");
                                } else {
                                    output.writeUTF("The response says that this file is not found!");
                                }
                            }
                            case 2 -> {
                                output.writeUTF("Enter id:");
                                long id = input.readLong();

                                output.writeUTF("The request was sent.");

                                boolean deleted = ServerData.getInstance().getUser(login).deleteFile(id);

                                if (deleted) {
                                    output.writeUTF("The response says that this file was deleted successfully!");
                                } else {
                                    output.writeUTF("The response says that this file is not found!");
                                }
                            }
                        }
                    }
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
