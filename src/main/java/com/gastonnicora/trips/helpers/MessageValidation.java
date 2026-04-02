package com.gastonnicora.trips.helpers;

public class MessageValidation {

    private static final String NOT_BLANK = " no puede quedar en blanco";
    private static final String SIZE_MAX = " no puede tener mas de %d caracteres";
    private static final String EMAIL_VALID = "El email no es valido";
    private static final String PASS_MATCH = "Las contraseñas deben coincidir";
    private static final String SIZE_MIN_MAX = " debe contener al menos %d y máximo %d caracteres";
    private static final String ROLE_NOT_EMPTY = "Debe seleccionar al menos un rol";
    private static final String ROLE_NOT_NULL = "Debe seleccionar al menos un rol";
    private static final String SIZE_MIN= " debe contener al menos %d caracteres";
    

    public static String getNotBlack(String filed){
        return filed + NOT_BLANK;
    }
    public static String getSizeMax(String filed, int size){
        return filed + String.format(SIZE_MAX, size);
    }
    public static String getEmailValid(){
        return EMAIL_VALID;
    }
    public static String getPassMatch(){
        return PASS_MATCH;
    }
    public static String getSizeMin(String filed, int min){
        return filed + String.format(SIZE_MIN, min);
    }
    public static String getSizeMinMax(String filed, int min, int max){
        return filed + String.format(SIZE_MIN_MAX, min, max);
    }
    public static String getRoleNotEmpty(){
        return ROLE_NOT_EMPTY;
    }
    public static String getRoleNotNull(){
        return ROLE_NOT_NULL;
    }


}
