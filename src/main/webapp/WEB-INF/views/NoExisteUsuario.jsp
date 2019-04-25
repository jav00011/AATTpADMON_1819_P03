<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Usuario No Encontrado</title>
</head>
<body>
<br>
<br>
<h1>Se ha introducido un usuario de manera incorrecta o no existe en la BBDD</h1>
<form action="http://localhost:8080/myapp/RegistrarNuevoUsuario" method=post>
<br>
<p>Leer tarjeta y registrar nuevo usuario:</p>
    <button>Leer</button>
    </form>
<form action="/myapp/">
<p>Volver a intentar autenticación:</p>
<button>Volver</button>
</form>
</body>
</html>