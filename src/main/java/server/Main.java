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
        try (
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            // start
            for (int i = 0; i < 15; i++) {

                String action = input.readUTF();

                switch (action) {
                    case "1" -> {
                        int nameOrID = input.readInt();

                        switch (nameOrID) {
                            case 1 -> {
                                String fileName = input.readUTF();

                                byte[] data = ServerData.getInstance().getFile(fileName);
                                if (data != null) {
                                    output.writeInt(data.length);
                                } else {
                                    output.writeInt(-1);
                                    break;
                                }
                                output.write(data);
                            }
                            case 2 -> {
                                long id = input.readLong();
                                if (id == -1) {
                                    break;
                                }

                                byte[] data = ServerData.getInstance().getFile(id);
                                if (data != null) {
                                    output.writeInt(data.length);
                                } else {
                                    output.writeInt(-1);
                                    break;
                                }
                                output.write(data);
                                String fileName = ServerData.getInstance().getFileName(id);
                                output.writeUTF(fileName.substring(fileName.indexOf(".")));
                            }
                        }
                    }
                    case "2" -> {
                        // 1
                        String fileName = input.readUTF();

                        if (fileName.equals("upload error")) {
                            break;
                        }

                        // 2
                        int length = input.readInt();
                        byte[] data = new byte[length];
                        // 3
                        input.readFully(data, 0, data.length);

                        // 4
                        String serverFileName = input.readUTF() + fileName.substring(fileName.indexOf("."));

                        if (ServerData.getInstance().existsFileName(serverFileName)) {
                            // 5
                            output.writeBoolean(true);
                            break;
                        } else {
                            // 5
                            output.writeBoolean(false);
                        }

                        long id = ServerData.getInstance().addFile(serverFileName, data);
                        output.writeUTF("Response says that file is saved! ID = " + id);
                    }
                    case "3" -> {
                        int nameOrID = input.readInt();

                        switch (nameOrID) {
                            case 1 -> {
                                String fileName = input.readUTF();

                                boolean deleted = ServerData.getInstance().deleteFile(fileName);

                                if (deleted) {
                                    output.writeUTF("The response says that this file was deleted successfully!");
                                } else {
                                    output.writeUTF("The response says that this file is not found!");
                                }
                            }
                            case 2 -> {
                                long id = input.readLong();

                                boolean deleted = ServerData.getInstance().deleteFile(id);

                                if (deleted) {
                                    output.writeUTF("The response says that this file was deleted successfully!");
                                } else {
                                    output.writeUTF("The response says that this file is not found!");
                                }
                            }
                        }
                    }
                    default -> {
                        String command = input.readUTF();
                        switch (command) {
                            case "save" -> {
                                ServerData.getInstance().save();
                            }
                            case "exit" -> {
                                ServerData.getInstance().save();
                                System.exit(0);
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
