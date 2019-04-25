package com.practica3.myapp;


import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DAOUsuarios implements DAOInterfazUsuarios{

private JdbcTemplate jdbcTemplate;
	
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
	    this.jdbcTemplate = jdbcTemplate;
	}
	@Autowired
	public void setDataSource(DataSource dataSource) {
	this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//Primer metodo, para encontrar un usuario en una BBDD
	
	public Usuario ComprobarUsuario(String user, String nif) {
		String sql = "select * from users where user = ? and dni = ?";
		Object[] parametros = {user,nif};
		MapperUsuarios mapper = new MapperUsuarios();
		List<Usuario> usuarios = this.jdbcTemplate.query(sql, parametros, mapper);
		if (usuarios.isEmpty()) return null;
		else return usuarios.get(0);
	}	
	@Override
	public void NuevoUsuario(Usuario user) {
		String sql = "insert into users values(?,?,?,?,?)";
		Object[ ] parametros = {user.getUser(),user.getNif(),user.getNombre(),user.getApellido1(),user.getApellido2()};
		this.jdbcTemplate.update(sql,parametros);
	}

}
