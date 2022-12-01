package chatApp.Entities;

import java.util.Objects;

public class RequestActivation {
    private String id;
    private String activationCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestActivation that = (RequestActivation) o;
        return Objects.equals(id, that.id) && Objects.equals(activationCode, that.activationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, activationCode);
    }

    @Override
    public String toString() {
        return "RequestActivation{" +
                "id='" + id + '\'' +
                ", activationCode='" + activationCode + '\'' +
                '}';
    }
}
