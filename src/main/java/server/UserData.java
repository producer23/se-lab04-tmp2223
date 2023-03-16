package server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UserData {
    private String login;
    private List<FileData> files;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserData(@JsonProperty("login") String login) {
        this.login = login;
        this.files = new ArrayList<>();
    }

    public long addFile(String name, byte[] data) {
        long id = files.size();
        FileData file = new FileData(id, name, data);
        files.add(file);
        ServerData.getInstance().save();
        return id;
    }

    public boolean deleteFile(String name) {
        for (FileData fileData : files) {
            if (fileData.getName().equals(name)) {
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
                files.remove(fileData);
                reIndex();
                return true;
            }
        }
        return false;
    }

    public byte[] getFile(String name) {
        for (FileData fileData : files) {
            if (fileData.getName().equals(name)) {
                return fileData.getData();
            }
        }
        return null;
    }

    public byte[] getFile(long id) {
        for (FileData fileData : files) {
            if (fileData.getId() == id) {
                return fileData.getData();
            }
        }
        return null;
    }

    private void reIndex() {
        int counter = 0;
        for (FileData fileData : files) {
            fileData.setId(counter++);
        }
        ServerData.getInstance().save();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<FileData> getFiles() {
        return files;
    }

    public void setFiles(List<FileData> files) {
        this.files = files;
    }
}
