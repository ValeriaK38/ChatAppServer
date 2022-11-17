package chatApp.Entities.Enums;

public enum Prefix {
    GUEST, ADMIN;

    public String getPrefix() {

        switch (this) {
            case GUEST:
                return "Guest - ";

            case ADMIN:
                return "Admin - ";

            default:
                return null;
        }
    }
}

