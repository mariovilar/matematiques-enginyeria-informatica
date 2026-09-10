package ub.edu.model;

/*
 * Enum feta pel login i registre, per poder
 * identificar els errors llençats pel model
 * */
public enum StatusType {
    CONTRASENYA_NO_SEGURA("Contrasenya no prou segura"),
    CONTRASENYA_SEGURA("Contrasenya segura"),
    CORREU_INCORRECTE("Correu en format incorrecte"),
    CORREU_CORRECTE("Correu en format correcte"),
    CORREU_INEXISTENT("Correu inexistent"),
    CONTRASENYA_INCORRECTA("Contrasenya incorrecta"),
    LOGIN_CORRECTE("Login correcte"),
    PERSONA_DUPLICADA("Persona Duplicada"),
    REGISTRE_VALID("Registre valid"),
    FORMAT_INCORRECTE("Format incorrecte"),
    FORMAT_INCORRECTE_CORREU_PWD("Format incorrecte en l'email o contrasenya poc segura ha de contenir 8 caracters, amb: Mayus, Minus i Numeros");

    private final String text;

    StatusType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}