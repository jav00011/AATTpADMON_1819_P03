package com.practica3.myapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.smartcardio.*;

/**
 * La clase ObtenerDatos implementa cuatro mÃ©todos pÃºblicos que permiten obtener
 * determinados datos de los certificados de tarjetas DNIe, Izenpe y Ona.
 *
 * @author Jose Ramón Rodriguez Rodriguez & Javier Almodovar Villacañas
 */
public class ObtenerDatos {

    private static final byte[] dnie_v_1_0_Atr = {
        (byte) 0x3B, (byte) 0x7F, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x6A, (byte) 0x44,
        (byte) 0x4E, (byte) 0x49, (byte) 0x65, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x90, (byte) 0x00};
    private static final byte[] dnie_v_1_0_Mask = {
        (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFF};

    public ObtenerDatos() {
    }

    public Usuario LeerNIF() {

        Usuario user = null;
        byte[] datos=null;
        try {
            Card c = ConexionTarjeta();
            if (c == null) {
                throw new Exception("ACCESO DNIe: No se ha encontrado ninguna tarjeta");
            }
            byte[] atr = c.getATR().getBytes();
            CardChannel ch = c.getBasicChannel();

            if (esDNIe(atr)) {
                datos = leerCertificado(ch);
                if(datos!=null)
                    user = leerDatosUsuario(datos);
            }
            c.disconnect(false);

        } catch (Exception ex) {
            Logger.getLogger(ObtenerDatos.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return user;
    }

    public byte[] leerCertificado(CardChannel ch) throws CardException, CertificateException {


        int offset = 0;
        String completName = null;

        //[1] PRÃ�CTICA 3. Punto 1.a
        //Octetos--> 0x00 "CLA", 0xA4 "INS", 0x00 "P1", 0x00 "P2", 0x02 "LC";
        byte[] command = new byte[]{(byte) 0x00, (byte) 0xa4, (byte) 0x04, (byte) 0x00, (byte) 0x0b, (byte) 0x4D, (byte) 0x61, (byte) 0x73, (byte) 0x74, (byte) 0x65, (byte) 0x72, (byte) 0x2E, (byte) 0x46, (byte) 0x69, (byte) 0x6C, (byte) 0x65};
        ResponseAPDU r = ch.transmit(new CommandAPDU(command));
        if ((byte) r.getSW() != (byte) 0x9000) {
            System.out.println("ACCESO DNIe: SW incorrecto");
            return null;
        }

        //[2] PRÃ�CTICA 3. Punto 1.a
        command = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x50, (byte) 0x15};
        r = ch.transmit(new CommandAPDU(command));

        if ((byte) r.getSW() != (byte) 0x9000) {
            System.out.println("ACCESO DNIe: SW incorrecto");
            return null;
        }

        //[3] PRÃ�CTICA 3. Punto 1.a
        command = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x60, (byte) 0x04};
        r = ch.transmit(new CommandAPDU(command));

        byte[] responseData = null;
        if ((byte) r.getSW() != (byte) 0x9000) {
            System.out.println("ACCESO DNIe: SW incorrecto");
            return null;
        } else {
            responseData = r.getData();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] r2 = null;
        int bloque = 0;

        do {
             //[4] PRÃ�CTICA 3. Punto 1.b OJO
            final byte CLA = (byte) 0x00;//(0xFF no es el correcto) Se escribe este valor ya que el nos permite recorren de 0 a F (0x0X)
            final byte INS = (byte) 0xB0;//(0xFF no es el correcto) Se escribe este valor para indicar que el comando es un Read Binary
            final byte LE = (byte) 0xFF;// Indica el numero de bites que va a leer
            

            //[4] PRÃ�CTICA 3. Punto 1.b
            command = new byte[]{CLA, INS, (byte) bloque/*P1*/, (byte) 0x00/*P2*/, LE};//Lo que ocurre es que se leen todos los bytes excepto aquellos comprendidos entre P1 y P2.
            r = ch.transmit(new CommandAPDU(command));

            //System.out.println("ACCESO DNIe: Response SW1=" + String.format("%X", r.getSW1()) + " SW2=" + String.format("%X", r.getSW2()));

            if ((byte) r.getSW() == (byte) 0x9000) {
                r2 = r.getData();

                baos.write(r2, 0, r2.length);

                for (int i = 0; i < r2.length; i++) {
                    byte[] t = new byte[1];
                    t[0] = r2[i];
                    System.out.println(i + (0xff * bloque) + String.format(" %2X", r2[i]) + " " + String.format(" %d", r2[i])+" "+new String(t));
                   // 
                }
                bloque++;
            } else {
                return null;
            }

        } while (r2.length >= 0xfe);


         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

      

        
        return baos.toByteArray();
    }

    
    
    
    /**
     * Este mÃ©todo establece la conexiÃ³n con la tarjeta. La funciÃ³n busca el
     * Terminal que contenga una tarjeta, independientemente del tipo de tarjeta
     * que sea.
     *
     * @return objeto Card con conexiÃ³n establecida
     * @throws Exception
     */
    private Card ConexionTarjeta() throws Exception {

        Card card = null;
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();
        //System.out.println("Terminals: " + terminals);

        for (int i = 0; i < terminals.size(); i++) {

            // get terminal
            CardTerminal terminal = terminals.get(i);

            try {
                if (terminal.isCardPresent()) {
                    card = terminal.connect("*"); //T=0, T=1 or T=CL(not needed)
                }
            } catch (Exception e) {

                System.out.println("Exception catched: " + e.getMessage());
                card = null;
            }
        }
        return card;
    }

    /**
     * Este mÃ©todo nos permite saber el tipo de tarjeta que estamos leyendo del
     * Terminal, a partir del ATR de Ã©sta.
     *
     * @param atrCard ATR de la tarjeta que estamos leyendo
     * @return tipo de la tarjeta. 1 si es DNIe, 2 si es Starcos y 0 para los
     * demÃ¡s tipos
     */
    private boolean esDNIe(byte[] atrCard) {
        int j = 0;
        boolean found = false;

        //Es una tarjeta DNIe?
        if (atrCard.length == dnie_v_1_0_Atr.length) {
            found = true;
            while (j < dnie_v_1_0_Atr.length && found) {
                if ((atrCard[j] & dnie_v_1_0_Mask[j]) != (dnie_v_1_0_Atr[j] & dnie_v_1_0_Mask[j])) {
                    found = false; //No es una tarjeta DNIe
                }
                j++;
            }
        }

        if (found == true) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Analizar los datos leÃ­dos del DNIe para obtener
     *   - nombre
     *   - apellidos
     *   - NIF
     * @param datos
     * @return 
     */
    private Usuario leerDatosUsuario(byte[] datos) {
       byte[] dni= new byte[9];
        int aux=0;
        int TamañoNombre=0;
        byte[] nombre= null;
        int i=0,j=0;
        int aux2=0;
        String DNI="",Nombre="",n="",a1="",a2="",us="";
        
        
        //En primer lugar, buscamos patrones en los datos para localizar el DNI y obtenerlo...
        
        for(i=0;i<datos.length;i++){//Recorremos todos los datos almacenados
        
            
            if(datos[i]==4 && datos[i+1]==5 && datos[i+2]==19){
            i=i+3;//Saltamos el tamaÃ±o del DNI y nos colocamos en el primer digito
            aux2=1; //Sirve para indicar que nos hemos situado en la posicion buscada dentro de los datos  
            }else if(aux<9 && aux2==1){
            dni[aux]=datos[i];
            aux++;
            }else if (aux==9){
               aux=0;
                
                i=datos.length;//Forzamos que termine el 
        DNI= new String(dni);//Convertimos los bytes del array de la variable DNI
            }
        
        }
                
        //Este for para recoger el nombre y apellidos    
         for(j=0;j<datos.length;j++){
         
            if(datos[j]==6 && datos[j+2]==85 && datos[j+3]==4 && datos[j+4]==3){ //Se comprueban estos datos porque es una secuencia univoca para coger el nombre
            j=j+5;//Indicamos que la siguiente secuencia es UTF8Stringtype si el dato es igual a 12
            
           if(datos[j]==12){
            
            TamañoNombre= (int)datos[j+1];//El siguiente byte indica el tamaÃ±o del nombre, lo guardamos.
            nombre= new byte[TamañoNombre];//Inicializamos con ese tamaÃ±o.
            j=j+2;//Nos colocamos en la primera letra del nombre.
           
           }
           }
            if(aux<TamañoNombre){//Realizamos esto hasta que hayamos recogido todos los valores del nombre gracias a la variable TamaÃ±oNombre
                nombre[aux]=datos[j];
                aux++;
            }else if(aux==TamañoNombre && TamañoNombre!=0){//Una vez que cogemos todos los valores, el programa entra aqui para terminar el bucle y guardar varible como un string
            
            j=datos.length;
            Nombre=new String(nombre);
            }
        }
        
        String[] nombr,apellidos;
        //Para obtener apellidos separados
        apellidos=Nombre.split(",");
        apellidos=apellidos[0].split(" ");
        a1=apellidos[0];
        a2=apellidos[1];
        //Para obtener el nombre
        nombr=Nombre.split(",");
        nombr=nombr[1].split(Pattern.quote("(AUTENTICACIÃ“N)"));
        n=nombr[0].trim();//Quitar espacios nombre
        
        
        Usuario user= new Usuario(n, a1, a2, DNI, us );
        
       return user;
    }
    
}
