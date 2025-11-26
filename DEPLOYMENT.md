# 🚀 Documentación de Despliegue - Render

## 📍 URLs del Proyecto

### Producción
- **API Base URL:** https://integrador-desarrollo-utn-2025.onrender.com
- **Swagger UI:** https://integrador-desarrollo-utn-2025.onrender.com/swagger-ui/index.html
- **OpenAPI JSON:** https://integrador-desarrollo-utn-2025.onrender.com/v3/api-docs

---

## 🌐 Endpoints Disponibles

### 1. POST /mutant - Verificar si un ADN es mutante

**URL Completa:**
```
POST https://integrador-desarrollo-utn-2025.onrender.com/mutant
```

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "dna": [
    "ATGCGA",
    "CAGTGC",
    "TTATGT",
    "AGAAGG",
    "CCCCTA",
    "TCACTG"
  ]
}
```

**Respuestas:**

| Código | Descripción | Significado |
|--------|-------------|-------------|
| `200 OK` | El ADN es mutante | Encontró 2+ secuencias de 4 letras iguales |
| `403 Forbidden` | El ADN es humano | Encontró 0-1 secuencias |
| `400 Bad Request` | ADN inválido | Matriz no cuadrada, caracteres inválidos, etc. |

**Ejemplo con cURL:**
```bash
curl -X POST https://integrador-desarrollo-utn-2025.onrender.com/mutant \
  -H "Content-Type: application/json" \
  -d '{
    "dna": ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
  }'
```

**Ejemplo con PowerShell:**
```powershell
$body = @{
    dna = @("ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG")
} | ConvertTo-Json

Invoke-RestMethod -Uri "https://integrador-desarrollo-utn-2025.onrender.com/mutant" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

---

### 2. GET /stats - Obtener estadísticas

**URL Completa:**
```
GET https://integrador-desarrollo-utn-2025.onrender.com/stats
```

**Respuesta exitosa (200 OK):**
```json
{
  "count_mutant_dna": 40,
  "count_human_dna": 100,
  "ratio": 0.4
}
```

**Ejemplo con cURL:**
```bash
curl https://integrador-desarrollo-utn-2025.onrender.com/stats
```

**Ejemplo con PowerShell:**
```powershell
Invoke-RestMethod -Uri "https://integrador-desarrollo-utn-2025.onrender.com/stats"
```

---

## 🧪 Ejemplos de Prueba

### Caso 1: ADN Mutante ✅

```bash
curl -X POST https://integrador-desarrollo-utn-2025.onrender.com/mutant \
  -H "Content-Type: application/json" \
  -d '{
    "dna": [
      "ATGCGA",
      "CAGTGC",
      "TTATGT",
      "AGAAGG",
      "CCCCTA",
      "TCACTG"
    ]
  }'
```

**Resultado esperado:** `200 OK`  
**Razón:** Tiene 2 secuencias (horizontal: CCCC + diagonal: AAAA)

---

### Caso 2: ADN Humano ❌

```bash
curl -X POST https://integrador-desarrollo-utn-2025.onrender.com/mutant \
  -H "Content-Type: application/json" \
  -d '{
    "dna": [
      "ATGCGA",
      "CAGTGC",
      "TTATTT",
      "AGACGG",
      "GCGTCA",
      "TCACTG"
    ]
  }'
```

**Resultado esperado:** `403 Forbidden`  
**Razón:** Solo tiene 1 secuencia (horizontal: TTT)

---

### Caso 3: ADN Inválido ⚠️

```bash
curl -X POST https://integrador-desarrollo-utn-2025.onrender.com/mutant \
  -H "Content-Type: application/json" \
  -d '{
    "dna": [
      "ATGC",
      "CAGT",
      "TTAT"
    ]
  }'
```

**Resultado esperado:** `400 Bad Request`  
**Razón:** Matriz no cuadrada (3x4)

---

## 🔧 Configuración de Render

### Información del Servicio

| Configuración | Valor |
|---------------|-------|
| **Tipo de Servicio** | Web Service |
| **Runtime** | Docker |
| **Rama de Despliegue** | `main` |
| **Build Command** | Automático (Dockerfile) |
| **Puerto** | 8080 |
| **Plan** | Free Tier |

### Variables de Entorno

Render detecta automáticamente el puerto 8080 definido en el Dockerfile con `EXPOSE 8080`.

**No se requieren variables de entorno adicionales** para el funcionamiento básico.

---

## ⚙️ Proceso de Despliegue

### 1. Build Automático

Render ejecuta automáticamente:

1. **Clona el repositorio** desde GitHub
2. **Lee el Dockerfile** en la raíz del proyecto
3. **Ejecuta build multi-stage:**
   - **Etapa 1 (Build):** Compila el código con Gradle y genera el JAR
   - **Etapa 2 (Runtime):** Copia solo el JAR a una imagen limpia
4. **Expone el puerto 8080**
5. **Inicia la aplicación** con `java -jar app.jar`

### 2. Despliegue Continuo (CD)

Cada vez que haces `push` a la rama `main`:

```bash
git add .
git commit -m "Descripción del cambio"
git push origin main
```

Render automáticamente:
- ✅ Detecta el cambio
- ✅ Inicia un nuevo build
- ✅ Ejecuta tests (durante el build de Gradle)
- ✅ Despliega la nueva versión
- ✅ Reemplaza la versión anterior

**Tiempo estimado:** 2-5 minutos

---

## 🐛 Troubleshooting

### Problema 1: Servicio caído después de inactividad

**Síntoma:** La primera request tarda mucho (30-60 segundos)

**Causa:** Render Free Tier suspende servicios inactivos después de 15 minutos

**Solución:** 
- Es normal, la primera request "despierta" el servicio
- Alternativa: Usar un servicio de ping (como UptimeRobot) para mantenerlo activo

### Problema 2: Error 502 Bad Gateway

**Posibles causas:**
1. El servicio está iniciando (espera 1-2 minutos)
2. Error en el código que impide el inicio
3. Puerto incorrecto

**Solución:**
1. Revisa los logs en Render Dashboard
2. Verifica que el Dockerfile expone el puerto 8080
3. Verifica que Spring Boot escucha en 8080

### Problema 3: Build falla

**Revisa los logs de build en Render** para identificar:
- ❌ Tests que fallan
- ❌ Dependencias faltantes
- ❌ Errores de compilación

**Solución:**
- Ejecuta localmente: `.\gradlew.bat clean build`
- Corrige los errores antes de hacer push

### Problema 4: Base de datos vacía después de redeploy

**Causa:** H2 usa memoria RAM, los datos se pierden al reiniciar

**Esto es normal en desarrollo.** Para producción, considera:
- PostgreSQL (Render ofrece un plan free)
- MySQL

---

## 📊 Monitoreo y Logs

### Ver Logs en Tiempo Real

1. Ve a [Render Dashboard](https://dashboard.render.com/)
2. Selecciona tu servicio
3. Click en "Logs"
4. Verás los logs de Spring Boot en tiempo real

### Métricas Importantes

Render muestra automáticamente:
- 📈 CPU Usage
- 💾 Memory Usage
- 🌐 HTTP Requests
- ⏱️ Response Time

---

## 🔐 Base de Datos en Producción (Opcional)

Si quieres persistir los datos entre deploys, configura PostgreSQL:

### 1. Crear Base de Datos en Render

1. Dashboard → New → PostgreSQL
2. Configurar nombre: `mutantes-db`
3. Plan: Free (90 días)
4. Click "Create Database"

### 2. Conectar a la Aplicación

En Render Service → Environment:

```env
SPRING_DATASOURCE_URL=<Internal Database URL>
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
```

### 3. Actualizar `build.gradle`

Agregar dependencia de PostgreSQL:

```gradle
dependencies {
    // Reemplazar H2 por PostgreSQL
    runtimeOnly 'org.postgresql:postgresql'
}
```

---

## 🚦 Health Check

Render hace health checks automáticos cada 60 segundos:

**Endpoint verificado:** `GET /` o `GET /actuator/health`

Si el servicio no responde después de 3 intentos, Render lo reinicia automáticamente.

---

## 📝 Comandos Útiles

### Probar API desde terminal

```bash
# Verificar que el servicio está activo
curl https://integrador-desarrollo-utn-2025.onrender.com/stats

# Probar detección de mutante
curl -X POST https://integrador-desarrollo-utn-2025.onrender.com/mutant \
  -H "Content-Type: application/json" \
  -d '{"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}'

# Ver respuesta con detalles
curl -v https://integrador-desarrollo-utn-2025.onrender.com/stats
```

### PowerShell

```powershell
# Probar stats
Invoke-RestMethod -Uri "https://integrador-desarrollo-utn-2025.onrender.com/stats"

# Probar mutant
$body = @{dna=@("ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG")} | ConvertTo-Json
Invoke-RestMethod -Uri "https://integrador-desarrollo-utn-2025.onrender.com/mutant" -Method Post -ContentType "application/json" -Body $body
```

---

## 📚 Recursos Adicionales

- **Render Documentation:** https://render.com/docs
- **Spring Boot on Render:** https://render.com/docs/deploy-spring-boot
- **Dashboard de Render:** https://dashboard.render.com/
- **Swagger UI:** https://integrador-desarrollo-utn-2025.onrender.com/swagger-ui/index.html

---

## ✅ Checklist de Verificación

Antes de considerar el deploy exitoso, verifica:

- [ ] ✅ La aplicación responde en la URL base
- [ ] ✅ Swagger UI se carga correctamente
- [ ] ✅ POST /mutant retorna 200 para ADN mutante
- [ ] ✅ POST /mutant retorna 403 para ADN humano
- [ ] ✅ POST /mutant retorna 400 para ADN inválido
- [ ] ✅ GET /stats retorna estadísticas correctas
- [ ] ✅ Los logs no muestran errores críticos
- [ ] ✅ El tiempo de respuesta es < 5 segundos (después del primer request)

---

## 🎓 Información del Proyecto

**Universidad:** Universidad Tecnológica Nacional - Facultad Regional Mendoza (UTN FRM)  
**Materia:** Desarrollo de Software 2025  
**Profesor:** Cortez, Alberto  
**Alumno:** Ignacio Berridy  
**Legajo:** 50714

---

**Última actualización:** Noviembre 2025
