# 📱 WatchView

**WatchView** es una aplicación móvil desarrollada en Kotlin con Android Studio. Su objetivo es centralizar la información sobre estrenos y rankings de contenido audiovisual de plataformas de streaming como Netflix. El proyecto ha sido realizado como parte del Trabajo de Fin de Grado del Ciclo Formativo de Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM).

---

## ✨ Características principales

- Visualización de **estrenos** y **Top 10** de Netflix España (películas y series).
- Gestión de perfil de usuario con selección de foto.
- Wishlist personalizada para cada usuario.
- Notificaciones automáticas al producirse un estreno.
- Modo **usuario**, **administrador** e **invitado**.
- Interfaz oscura y moderna, diseñada con [Figma]([https://www.figma.com/](https://www.figma.com/proto/BgUxgvSB3banSWmLuBIqng/Mock-Up-TFG?node-id=0-1&t=1Cs6yt1wetdi3N6W-1)).

---

## ⚙️ Configuración: uso local vs. llamadas a API

Según el entorno, puedes configurar la aplicación para trabajar con **datos locales** o con **la API externa** de Streaming Availability.

### 🔄 Cambios en `LoginActivity.kt`

- **Para trabajar en local:**
  - Comentar líneas `103–105` → llamada a la API para géneros.
  - Descomentar línea `107` → inserción local de géneros.
  - Comentar líneas `110–111` → llamadas a la API para Top 10 de series y películas.

### 🔄 Cambios en `AppActivity.kt`, `AppActivityAdmin.kt`, `AppActivityInvitado.kt`

- Comentar líneas `23–25` → llamadas a la API para inserción de títulos con contador. Actualmente no muestran los resultados correctamente.

### 🗃️ Cambios en `BBDD.kt`

- En la función `insertAllTop10Data`, dentro de `listOf(...)`:
  - Usar `insertPosicionesTop102()` → si trabajas con datos **locales**.
  - Usar `insertPosicionesTop10()` → si trabajas con la **API**.
- Esto evita errores de claves inexistentes cuando las llamadas a la API están comentadas.

---

## 🔔 Notificaciones

Para mantener el diseño visual coherente con el estilo de la app (incluyendo el logotipo), se recomienda usar la función:

kotlin
mostrarNotificacionDeEstreno2(context, it)

Esta función mejora la apariencia de las notificaciones incluyendo el logo de la aplicación y se recomienda frente a versiones anteriores como `mostrarNotificacionDeEstreno`.

---

## 🔗 Enlaces del proyecto

- 🎨 [Mock Up (Figma)](https://www.figma.com/design/BgUxgvSB3banSWmLuBIqng/Mock-Up-TFG?node-id=0-1&t=rJtpRuwSQewEYwCy-1)
- 🧩 [Entidad-Relación y Normalización (Canva)](https://www.canva.com/design/DAGhcRI-BeI/LcxlKLU8Q5BUvBKc5zxaHQ/view?utm_content=DAGhcRI-BeI&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=hc769c2eaca)
- 🎭 [Diagrama de Casos de Uso (Canva)](https://www.canva.com/design/DAGiwsp5pFA/UabYwMZwaf_cCL0gB8vTEA/view?utm_content=DAGiwsp5pFA&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=h3d611980f6)
- 🧱 [Diagrama de Clases (Google Drive)](https://drive.google.com/file/d/1bajEPvLTbsaBxQwRXmrGIl4mjfQbiu2F/view?usp=sharing)
- 📋 [Trello - Gestión del proyecto](https://trello.com/invite/b/67d9c5d1d603dcf5241a07c5/ATTIc36b54e7538fdf804223637e60a930edE27CB7CA/tfg-watchview)

---

## 🛠️ Tecnologías utilizadas

- [Kotlin](https://kotlinlang.org/)
- [Android Studio](https://developer.android.com/studio)
- [SQLite (Room)](https://developer.android.com/jetpack/androidx/releases/room)
- [Figma](https://www.figma.com/) – Diseño UI/UX
- [Streaming Availability API](https://docs.movieofthenight.com/)
- [GitHub](https://github.com/) – Control de versiones
- [Trello](https://trello.com/) – Gestión ágil de tareas

---

## 📋 Estado del proyecto

✅ Primera versión funcional completa con funcionalidades principales.  
⚠️ Algunas limitaciones en la actualización dinámica de datos debido a restricciones de la API.  
🚀 Abierto a futuras mejoras, como recomendaciones personalizadas y despliegue en Google Play.

---

## 📄 Licencia

Proyecto desarrollado con fines educativos como Trabajo de Fin de Grado (TFG).  
© 2025 – *[Patricia Aguayo Escudero]*. Todos los derechos reservados.



