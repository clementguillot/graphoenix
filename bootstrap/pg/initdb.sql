CREATE DATABASE keycloak;
CREATE ROLE keycloak
  WITH LOGIN PASSWORD 'keycloak';
GRANT ALL PRIVILEGES
  ON DATABASE keycloak
  TO keycloak;
ALTER DATABASE keycloak OWNER TO keycloak;
