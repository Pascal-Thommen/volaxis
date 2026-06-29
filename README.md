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

<img width="353" height="699" alt="image" src="https://github.com/user-attachments/assets/f91bacc7-3b0e-4fc5-96fa-da34fcf8d86b" />


---

## Estados

**Oferta:** `ACTIVO` · `VENDIDO` · `BLOQUEADO`

**Libro:** campo booleano `estandarizado` — los libros estándar aparecen primero en el catálogo.

---

## Diagrama ER

<img width="766" height="730" alt="image" src="https://github.com/user-attachments/assets/8850ebc6-2392-448e-bfe4-6c0eb7b28e33" />
