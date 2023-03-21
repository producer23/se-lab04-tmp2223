package server;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerData {
    private List<FileData> files;
    private final String path = "src/main/java/server/data/";

    private ServerData() {
        files = new ArrayList<>();
        try {
            files.addAll(List.of(new ObjectMapper().readValue(new File(path + "serverdata.json"), FileData[].class)));
        } catch (Exception ignored) {
        }
    }

    private static class SingletonHolder {
        public static final ServerData HOLDER_INSTANCE = new ServerData();
    }

    public static ServerData getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    public long addFile(String name, byte[] data) {
        long id = files.size();
        FileData file = new FileData(id, name);
        files.add(file);
        uploadFileOnServer(name, data);
        return id;
    }

    private void uploadFileOnServer(String name, byte[] data) {
        File file = new File(path + name);
        try {
            Files.write(file.toPath(), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getFile(String name) {
        for (FileData fileData : files) {
            if (fileData.getName().equals(name)) {
                try {
                    return Files.readAllBytes(new File("src/main/java/server/data/" + name).toPath());
                } catch (IOException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public byte[] getFile(long id) {
        for (FileData fileData : files) {
            if (fileData.getId() == id) {
                try {
                    return Files.readAllBytes(new File("src/main/java/server/data/" + fileData.getName()).toPath());
                } catch (IOException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public String getFileName(long id) {
        for (FileData fileData : files) {
            if (fileData.getId() == id) {
                return fileData.getName();
            }
        }
        return null;
    }

    public boolean deleteFile(String name) {
        for (FileData fileData : files) {
            if (fileData.getName().equals(name)) {
                for (File file : Objects.requireNonNull(new File(path).listFiles())) {
                    if (file.getName().equals(name)) {
                        file.delete();
                    }
                }
                files.remove(fileData);
                reIndex();
                return true;
            }
        }
        return false;
    }

    public boolean deleteFile(long id) {
        for (FileData fileData : files) {
            if (fileData.getId() == id) {
                for (File file : Objects.requireNonNull(new File(path).listFiles())) {
                    if (file.getName().equals(fileData.getName())) {
                        file.delete();
                    }
                }
                files.remove(fileData);
                reIndex();
                return true;
            }
        }
        return false;
    }

    public boolean existsFileName(String name) {
        for (FileData fileData : files) {
            if (fileData.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void reIndex() {
        int counter = 0;
        for (FileData fileData : files) {
            fileData.setId(counter++);
        }
    }

    public void save() {
        try {
            new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValue(Paths.get(path + "serverdata.json").toFile(), files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
