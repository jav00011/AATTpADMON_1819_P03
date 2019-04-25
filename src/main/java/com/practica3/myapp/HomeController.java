package com.practica3.myapp;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.practica3.myapp.DAOInterfazUsuarios;
import com.practica3.myapp.Usuario;
/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private DAOInterfazUsuarios dao;
	private ObtenerDatos datos = new ObtenerDatos();
	
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		
		return "index"; //Cuando se arranque nos manda en primer lugar a la autenticacion
	}
	//A continuacion pasamos al metodo para comprobar si un usuario existe o no
	
	@RequestMapping(value = "/ComprobarUsuario", method = RequestMethod.POST)
	public String servlet1(HttpServletRequest request, @RequestParam("user") String user,@RequestParam("nif") String nif,HttpServletRequest req,Locale locale, Model model) {
		
		HttpSession sesion = request.getSession();
		
		if(dao.ComprobarUsuario(user,nif)!=null){
			 
			
				Usuario u=dao.ComprobarUsuario(user,nif);
		        sesion.setAttribute("nombreusuario", u);
		        request.setAttribute("nombreusuario", u);
			
		        
		        return "MostrarDatosUsuario";
	}
		
		else return "NoExisteUsuario"; 
	}
	
	@RequestMapping(value = "/RegistrarNuevoUsuario", method = RequestMethod.POST)
	public String registro(HttpServletRequest request,Locale locale, Model model) {
		HttpSession sesion = request.getSession();
	
	    Usuario dat=datos.LeerNIF();
		//Se coge primera letra del nombre, primer apellido y segunda letra del apellido
	    String NombreUsuario = dat.getNombre().substring(0,1)+ dat.getApellido1()+dat.getApellido2().substring(0,1); //Utilizamos este comando para montar nombre de usuario tal como se indica en indice
		   NombreUsuario=NombreUsuario.toLowerCase();
	    
	   dat.setUser(NombreUsuario);
	    
	   if (dao.ComprobarUsuario(dat.getUser(),dat.getNif())==null) {  //Comprobamos que los nuevos datos a introducir no existen
	    	dao.NuevoUsuario(dat);
	    	
		    sesion.setAttribute("nombreusuario", dat);
		    request.setAttribute("nombreusuario",dat);	    
		    model.addAttribute("User:",NombreUsuario);

		    return "MostrarDatosUsuario";	
	    }
	   
	    else  return "index"; //Al estar ya registrado nos manda al index directamente
    
	
		
	}
	
}
