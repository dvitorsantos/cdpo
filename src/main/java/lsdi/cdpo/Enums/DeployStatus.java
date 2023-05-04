package lsdi.cdpo.Enums;

public enum DeployStatus {
    DEPLOYED("DEPLOYED"),
    UNDEPLOYED("UNDEPLOYED"),
    PENDING("PENDING"),

    ERROR("ERROR");

    String value;

    DeployStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
