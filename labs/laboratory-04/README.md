# Laboratory 04 - Docker Compose: Multi-Service Platform

Construir una plataforma multicontenedor utilizando Docker Compose,
integrando NGINX, Tomcat y PostgreSQL, con networking interno,
persistencia, configuración externa, healthchecks y un DataSource JDBC.

Client
  |
  v
NGINX :80
  |
  | Docker Network
  v
Tomcat :8080
  |
  | JDBC
  v
PostgreSQL :5432
  |
  v
postgres-data


Architectural decisión:
Only NGINX is exposed to the host. Tomcat and PostgreSQL
remain internal to the Compose network.

laboratory-04/
├── README.md
├── compose.yaml
├── .env
├── nginx/
│   └── nginx.conf
└── tomcat/
    ├── Dockerfile
    └── app/
        ├── pom.xml
        └── src/



Concepts / Learning Objectives
## Concepts Learned

- Docker Compose service orchestration
- Internal service discovery by service name
- Host port publishing vs internal service communication
- Build time vs runtime configuration
- Environment variable injection
- Bind mounts and named volumes
- Container lifecycle vs data lifecycle
- Healthchecks and service dependencies
- Maven dependencies
- Tomcat JNDI DataSource
- PostgreSQL JDBC connectivity

Mission 1 — NGINX
	NGINX was initially exposed on host port 80.
	The container uses the standard HTTP port 80.

services:
  postgres:
    image: postgres:16
    container_name: laboratory-04-postgres

  tomcat:
    #image: tomcat:10.1-jdk17-temurin sustituido por build para agregar el driver de postgres y el context.xml
    build:
      context: ./tomcat
    
    container_name: laboratory-04-tomcat

  nginx:
    image: nginx:alpine
    container_name: laboratory-04-nginx

Validation:
  docker compose up -d
  docker compose ps
  curl http://localhost

Response:
  StatusCode        : 200
  StatusDescription : 
  Content           : <html>
                    <head><title>Laboratory 04</title></head>
                    <body>
                    <h1>Laboratory 04 - Users</h1>
                    <table border='1'>
                    <tr><th>ID</th><th>Nombre</th></tr>
                    <tr><td>1</td><td>Ethan Hunt</td></tr>
                    </table>
                    </body>
                    </html>

Mission 2 — NGINX → Tomcat
  NGINX communicates with Tomcat using:

  tomcat:8080

  localhost inside the NGINX container refers to NGINX itself.
  Compose provides DNS resolution using the service name.

  server {
        listen 80;
        location / {
          proxy_pass http://tomcat:8080/laboratory-04/;
        }
    }

Mission 3 — PostgreSQL + Persistence
  PostgreSQL
     |
     v
 postgres-data

### Persistence Test

1. Created database `laboratory_bd`.
2. Created table `tbUsuarios`.
3. Inserted `Ethan Hunt`.
4. Executed `docker compose down`.
5. Started the platform again.
6. Verified that the record was still present.

  Conclusión:
    Container lifecycle is independent from persistent data lifecycle when a named volume is used.

   DB exist?    --> PostgreSQL Database directory appears to contain a database;
                    Skipping initialization
   Changing POSTGRES_DB does not recreate or rename an already initialized database stored in the existing volume.

Mission 4 — Environment Variables
   .env
     ↓
  Compose                      compose.yaml
     ↓
  container environment            .env
     ↓
  Tomcat runtime             JVM/Tomcat configuration


Mission 5 — Healthcheck & Dependencies
      service_started
            ≠
      service_healthy

  Tomcat depends_on PostgreSQL
  PostgreSQL condition = service_healthy

  NGINX depends_on Tomcat                                 NGINX initially failed because the Tomcat upstream
  Tomcat condition = service_started                      was not yet available during startup.

Future improvement:
implement a Tomcat HTTP healthcheck and use
condition: service_healthy for NGINX.


Tomcat + JDBC DataSource
      Application
   ↓
JNDI                         java:comp/env/jdbc/LaboratoryDB
   ↓
DataSource
   ↓
PostgreSQL JDBC Driver
   ↓
PostgreSQL


12. Mini Application
WAR: laboratory-04.war

Endpoint:
GET /users

13. Troubleshooting
   Problem 1 -- Cannot create JDBC driver of class '' for connect URL 'null'
   Initial hypothesis:
    environment variable / DataSource configuration.

  Investigation:
  WAR deployment
  JNDI lookup
  context.xml
  JDBC driver
  Tomcat configuration

  Root cause:
    invalid/unknown character in context.xml.

  Problem 2 — NGINX startup
    NGINX could not start because Tomcat
    was not yet available.

    Solution
     depends_on:
       tomcat:
       condition: service_started

   Problem 3 — PostgreSQL initialization
     Skipping initialization
     
     Existing PostgreSQL data directory in named volume.


14: Validation
| Component   | Validation                         |
| ----------- | ---------------------------------- |
| NGINX       | `curl http://localhost`            |
| Tomcat      | Application deployed               |
| PostgreSQL  | `pg_isready` / SQL query           |
| JDBC        | DataSource lookup                  |
| Persistence | Data survives container recreation |
| Integration | `/users` returns DB data           |



15. Lessons Learned
- Docker Compose describes and coordinates the runtime topology.
- Dockerfiles define how custom images are built.
- Service names provide internal service discovery.
- Published ports should represent required external interfaces.
- Named volumes decouple persistent data from container lifecycle.
- Container startup does not necessarily mean service readiness.
- `depends_on` and `healthcheck` solve different problems.
- Build-time dependencies and runtime configuration have different lifecycles.
- Tomcat JNDI DataSource configuration is independent from the application code.
- Small configuration errors can manifest as database or JDBC failures.

## Interview Questions

1. Why is PostgreSQL not exposed to the host?
2. Why does NGINX communicate with `tomcat:8080` instead of `localhost:8080`?
3. What is the difference between a Docker image, container and volume?
4. What is the difference between `service_started` and `service_healthy`?
5. How does the Java application obtain the PostgreSQL connection?

## Future Evolution

- Improve Tomcat healthcheck.
- Externalize remaining configuration/secrets.
- Add automated tests.
- Integrate the Compose workflow with GitHub Actions.
- Map Docker Compose concepts to Kubernetes objects.










