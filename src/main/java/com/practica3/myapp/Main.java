package com.practica3.myapp;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;



public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        ByteArrayInputStream bais=null;
        
        //TAREA 2. Conseguir que el método LeerNIF de ObtenerDatos devuelva el 
        //         correctamente los datos de usuario 
        ObtenerDatos od = new ObtenerDatos();
        Usuario user = od.LeerNIF();
        
        if(user!=null)
            System.out.println("usuario: "+user.toString());
        
        //TAREA 3. AUTENTICAR EL CLIENTE CON EL SERVIDOR
        
    }
}
