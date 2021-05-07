package at.jit;

enum DmnDataTypes {
    STRING("string"),
    BOOLEAN("boolean"),
    INTEGER("integer"),
    LONG("long"),
    DOUBLE("double"),
    DATE("date");

    private final String value;

    DmnDataTypes(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static boolean dataTypeExists(String dataType) {
        for (DmnDataTypes dmnDataTypes : DmnDataTypes.values()) {
            if (dmnDataTypes.value().equals(dataType.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
