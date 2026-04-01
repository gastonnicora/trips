package com.gastonnicora.trips.helpers;


//Clase auxiliar de usuario para pruebas
public class AuxUser{
        private String email;
        private String pass;
        
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getPass() {
            return pass;
        }
        public void setPass(String pass) {
            this.pass = pass;
        }
        public AuxUser(String email, String pass) {
            this.email = email;
            this.pass = pass;
        }
        public AuxUser() {}
        
        @Override
        public String toString() {
            return """
                    {
                        "email": "%s",
                        "password": "%s"
                    }
                    """.formatted(email, pass);
        }
}  