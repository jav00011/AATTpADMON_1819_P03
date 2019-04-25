package com.practica3.myapp;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MapperUsuarios implements RowMapper<Usuario>{

	public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Usuario usu = new Usuario();
		usu.setNombre(rs.getString("nombre"));
		usu.setApellido1(rs.getString("apellido1"));
	    usu.setApellido2(rs.getString("apellido2"));
	    usu.setNif(rs.getString("dni"));
	    usu.setUser(rs.getString("user"));
	    
	
	    return usu;
	}

}
