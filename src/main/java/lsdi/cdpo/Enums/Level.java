package lsdi.cdpo.Enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Level {
    CLOUD("CLOUD"),
    FOG("FOG"),
    EDGE("EDGE");

    String value;

    Level(String value) {
        this.value = value;
    }


    @JsonValue
    public String getValue() {
        return value;
    }
}
