package com.practica3.myapp;

import java.util.List;

public interface DAOInterfazUsuarios {

	public Usuario ComprobarUsuario(String user, String nif);
	public void NuevoUsuario(Usuario user);
}
