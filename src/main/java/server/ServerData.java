package server;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServerData {
    private List<UserData> users;
    private final String path = "src/main/java/server/data/serverdata.json";

    private ServerData() {
        users = new ArrayList<>();
        try {
            users.addAll(List.of(new ObjectMapper().readValue(new File(path), UserData[].class)));
        } catch (Exception ignored) {
        }
    }

    private static class SingletonHolder {
        public static final ServerData HOLDER_INSTANCE = new ServerData();
    }

    public static ServerData getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    public UserData addUser(String login) {
        if (!containsUser(login)) {
            UserData newUser = new UserData(login);
            users.add(newUser);
            save();
            return newUser;
        }
        return getUser(login);
    }

    public UserData getUser(String login) {
        for (UserData userData : users) {
            if (userData.getLogin().equals(login)) {
                return userData;
            }
        }
        return null;
    }

    private boolean containsUser(String login) {
        for (UserData userData : users) {
            if (userData.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    public void save() {
        try {
            new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValue(Paths.get(path).toFile(), users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
