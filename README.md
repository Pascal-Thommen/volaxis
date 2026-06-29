# Volaxis

Plataforma de compra y venta de libros universitarios usados para estudiantes de la Universidad Paraguayo Alemana (UPA).

---

## El problema

El grupo de WhatsApp "TIE UPA" se utiliza para publicar libros en venta, pero los mensajes antiguos se pierden y es imposible rastrear qué libros siguen disponibles.

**Volaxis** resuelve esto con una aplicación de escritorio centralizada donde los alumnos pueden publicar y buscar ofertas de libros de forma ordenada.

---

## Tecnologías

- Java + Java Swing (interfaz de escritorio)
- SQLite (base de datos local)

---

## Equipo

| Módulo | Responsable | Descripción |
|--------|-------------|-------------|
| A1 | Natasha Colman | Autenticación |
| A2 | Theresa Bauer | Catálogo |
| A3 | Pascal Thommen | Ofertas |

---

## Roles y casos de uso

### Alumno
- Registrarse (correo, nombre, contraseña)
- Iniciar sesión
- Explorar catálogo → seleccionar libro → ver ofertas → abrir contacto de WhatsApp
- Ver mis ofertas
  - Crear oferta (libro, condición, precio)
    - Crear nuevo libro si no existe
  - Editar oferta / cambiar estado (ACTIVO → VENDIDO)
- Perfil: cambiar número de WhatsApp / cambiar contraseña

### Administrador
- Crear / editar / eliminar libros
- Estandarizar libro
- Editar cualquier oferta / cambiar estado a BLOQUEADO
- Asignar nueva contraseña a un usuario

---

## Estados

**Oferta:** `ACTIVO` · `VENDIDO` · `BLOQUEADO`

**Libro:** campo booleano `estandarizado` — los libros estándar aparecen primero en el catálogo.

---

## Diagrama ER

<img width="720" height="681" alt="Screenshot 2026-06-29 at 10-43-38 Java Swing mit SQL-Datenbanken - Claude" src="https://github.com/user-attachments/assets/699f9e40-6532-47e3-b005-03fba45cd4e9" />


---


| fk_usuario | INTEGER FK | → usuario.id |
