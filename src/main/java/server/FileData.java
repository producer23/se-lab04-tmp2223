package server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FileData {
    private long id;
    private String name;
    private byte[] data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public FileData(@JsonProperty("id") long id, @JsonProperty("name") String name, @JsonProperty("data") byte[] data) {
        this.id = id;
        this.name = name;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
