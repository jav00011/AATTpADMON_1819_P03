<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Datos de Usuario</title>
</head>
<body>
<h2>Bienvenido</h2>

 
<p>Nombre de usuario:  ${nombreusuario.user}</p>
<p>DNI:  ${nombreusuario.nif}</p>
<p>${nombreusuario.apellido1}</p>
<p>${nombreusuario.apellido2}</p>
<p>${nombreusuario.nombre}</p>
<form action="/myapp/">
<p>Volver a autenticación:</p>
<button>Volver</button>
</form>
</body>
</html>