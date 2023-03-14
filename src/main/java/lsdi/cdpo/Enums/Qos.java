package lsdi.cdpo.Enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Qos {
    AT_MOST_ONCE("AT_MOST_ONCE"),
    AT_LEAST_ONCE("AT_LEAST_ONCE"),
    EXACTLY_ONCE("EXACTLY_ONCE");

    String value;
    Qos(String value) {
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }
}
