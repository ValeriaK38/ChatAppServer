package chatApp.Entities;

public class SystemMessage {

    private String name;

    public SystemMessage() {
    }

    public SystemMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "HelloMessage{" +
                "name='" + name + '\'' +
                '}';
    }
}