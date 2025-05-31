# 📖  Guía de Versionado y Flujo de Trabajo

Este documento describe las convenciones internas que utilizamos para estructurar el flujo de trabajo con Git en el repositorio de este proyecto. El objetivo es mantener un historial de cambios claro, ordenado y trazable.

## 📦 Commits

Los mensajes de commit deben seguir el siguiente formato:

```
{tipo de cambio}/{nombre del autor}/{código de proyecto}-{nro de ticket}/{descripción}
```

### Ejemplo

```
feat/Lautaro/SDGD-30/agrego-contributing
```

### Tipos de cambio comunes

- `feat` → Nueva funcionalidad
- `fix` → Corrección de errores
- `refactor` → Reestructuración sin cambiar comportamiento
- `chore` → Tareas menores, configuración, cambios no funcionales
- `test` → Agregado o modificación de pruebas
- `docs` → Cambios en la documentación
- `style` → Cambios de formato, indentación, etc. (sin modificar lógica)

> **Nota:** Usar descripciones concisas en minúscula y separadas por guiones (`-`).

---

## 🪵 Branches

Recomendamos crear una rama por cada tarea o ticket en desarrollo, siguiendo la estructura:

```
{código de proyecto}-{nro de ticket}-{tipo de cambio}
```

### Ejemplo

```
SDGD-30-contributing
```

---

## 💾 Pull Requests (PRs)

Al abrir un Pull Request, utilizar la siguiente estructura:

### Título

```
Título representativo del pr
```

### Descripción

- Breve explicación de los cambios incluidos.
- Indicar si hay múltiples commits relevantes.
- Mencionar si el PR depende de otro o si resuelve una issue/ticket.

### Ejemplo

**Título:**

```
Agrego pautas Contributing
```

**Descripción:**

```
Se agregan las pautas de trabajo colaborativo para commits y PRs.
```

---

## 🕵️‍♂️ Revisión y Merge

- Siempre realizar PRs contra la rama principal (`dev`).
- Evitar merges con conflictos sin resolver.

---

## 📣 Gestión de Tickets en Jira

Al finalizar el desarrollo de un ticket (ya sea al hacer merge o cerrar el PR), recordá actualizar su estado en el tablero de Jira:

👉 [https://lautarotoledobdb.atlassian.net/jira/software/projects/SGD/boards/2/](https://lautarotoledobdb.atlassian.net/jira/software/projects/SGD/boards/2/)

Esto permite que mantengamos la trazabilidad y organización entre el código y el flujo de trabajo del equipo.
