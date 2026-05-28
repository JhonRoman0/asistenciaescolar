-- MySQL dump 10.13  Distrib 8.0.36, for Linux (x86_64)
--
-- Host: localhost    Database: asistenciaescolar
-- ------------------------------------------------------
-- Server version	8.0.45-0ubuntu0.24.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Alumno`
--

DROP TABLE IF EXISTS `Alumno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Alumno` (
  `idAlumno` int NOT NULL AUTO_INCREMENT,
  `apellidoMaterno` varchar(20) NOT NULL,
  `apellidoPaterno` varchar(20) NOT NULL,
  `codigoUnico` varchar(255) NOT NULL,
  `estado` int NOT NULL,
  `nombre` varchar(20) NOT NULL,
  `rutaFoto` varchar(255) NOT NULL,
  `idGrado` int NOT NULL,
  `idSeccion` int NOT NULL,
  `dni` varchar(255) NOT NULL,
  PRIMARY KEY (`idAlumno`),
  KEY `FK4q1k0u5qec6w6ysr7m3v3eebo` (`idGrado`),
  KEY `FKhetti0x4hhoglmarr9qjajg8w` (`idSeccion`),
  CONSTRAINT `FK4q1k0u5qec6w6ysr7m3v3eebo` FOREIGN KEY (`idGrado`) REFERENCES `Grado` (`idGrado`),
  CONSTRAINT `FKhetti0x4hhoglmarr9qjajg8w` FOREIGN KEY (`idSeccion`) REFERENCES `Seccion` (`idSeccion`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Alumno`
--

LOCK TABLES `Alumno` WRITE;
/*!40000 ALTER TABLE `Alumno` DISABLE KEYS */;
INSERT INTO `Alumno` VALUES (1,'Quito','Roman','ALU399418',1,'Liam Jhon','liam-profile.png',1,1,'61234567'),(2,'Chavez','Mendoza','ALU774286',1,'Thiago Andre','thiago-avatar.png',1,1,'72415839'),(3,'Quito','Roman','ALU769998',1,'Milan Andre','milan-foto-perfil-actualizada.png',1,1,'73625148'),(4,'Quito','Roman','ALU292797',1,'Danna Valentina','danna-profile.png',1,1,'75849302'),(5,'Quito','Roman','ALU140504',1,'Liam Gabriel','liam-modificado.png',1,1,'76543210'),(6,'Quito','Roman','ALU709455',1,'Thiago Alexander','thiago-profile.png',2,1,'74125896'),(7,'Quito','Roman','ba044d789700ef511fa1badc32e00e6f',1,'Liam Gabriel','liam-profile.png',1,1,'71112723');
/*!40000 ALTER TABLE `Alumno` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AlumnoApoderado`
--

DROP TABLE IF EXISTS `AlumnoApoderado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AlumnoApoderado` (
  `id` int NOT NULL AUTO_INCREMENT,
  `idAlumno` int NOT NULL,
  `idApoderado` int NOT NULL,
  `es_principal` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkocy2xsttlk9eobf9vw55ksqj` (`idAlumno`),
  KEY `FKbwudwhbtd03abbca21lpa8rwg` (`idApoderado`),
  CONSTRAINT `FKbwudwhbtd03abbca21lpa8rwg` FOREIGN KEY (`idApoderado`) REFERENCES `Apoderado` (`idApoderado`),
  CONSTRAINT `FKkocy2xsttlk9eobf9vw55ksqj` FOREIGN KEY (`idAlumno`) REFERENCES `Alumno` (`idAlumno`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AlumnoApoderado`
--

LOCK TABLES `AlumnoApoderado` WRITE;
/*!40000 ALTER TABLE `AlumnoApoderado` DISABLE KEYS */;
INSERT INTO `AlumnoApoderado` VALUES (1,1,1,_binary '\0'),(2,1,2,_binary '\0'),(3,2,3,_binary '\0'),(4,3,4,_binary '\0'),(5,4,4,_binary '\0'),(6,4,5,_binary '\0'),(7,5,6,_binary '\0'),(8,5,7,_binary ''),(9,6,1,_binary ''),(10,6,2,_binary '\0'),(11,7,1,_binary ''),(12,7,2,_binary '\0');
/*!40000 ALTER TABLE `AlumnoApoderado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Apoderado`
--

DROP TABLE IF EXISTS `Apoderado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Apoderado` (
  `idApoderado` int NOT NULL AUTO_INCREMENT,
  `apellidoMaterno` varchar(20) NOT NULL,
  `apellidoPaterno` varchar(20) NOT NULL,
  `celular` int NOT NULL,
  `email` varchar(50) NOT NULL,
  `nombre` varchar(20) NOT NULL,
  `dni` varchar(9) NOT NULL,
  PRIMARY KEY (`idApoderado`),
  UNIQUE KEY `UKcoq5h9vq1v47bjtwmgnkggdpb` (`dni`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Apoderado`
--

LOCK TABLES `Apoderado` WRITE;
/*!40000 ALTER TABLE `Apoderado` DISABLE KEYS */;
INSERT INTO `Apoderado` VALUES (1,'Perez','Roman',945123456,'carlos.roman@mail.com','Carlos Alberto','40123456'),(2,'Mendoza','Quito',978654321,'maria.quito@mail.com','Maria Elena','45678901'),(3,'Castro','Mendoza',951842637,'ricardo.mendoza@example.com','Ricardo Javier','48925163'),(4,'Quito','Roman',988077666,'marcos.nuevo.contacto@mail.com','Marcos Javier','42615378'),(5,'Solis','Castro',932145678,'ana.castro@example.com','Ana Maria','47586921'),(6,'Perez','Roman',987654321,'juan.roman@mail.com','Juan Carlos','45678912'),(7,'Solis','Quito',955555555,'maria.quito@mail.com','Maria Elena','47586923');
/*!40000 ALTER TABLE `Apoderado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Asistencias`
--

DROP TABLE IF EXISTS `Asistencias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Asistencias` (
  `idAsistencias` int NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `horaEntrada` time(6) NOT NULL,
  `idAlumno` int NOT NULL,
  `idEstado` int NOT NULL,
  `idJustificaicon` int NOT NULL,
  `idTurno` int NOT NULL,
  PRIMARY KEY (`idAsistencias`),
  KEY `FKn1i6uv0nkoeqh14kka1l8qbs` (`idAlumno`),
  KEY `FKbjdx6qfn1gwi7kpgldrdnui5i` (`idEstado`),
  KEY `FKarjceitqbb3i4srbhyaby4cyr` (`idJustificaicon`),
  KEY `FKe8qkl2wdf4ntiaqoa6a7ycssm` (`idTurno`),
  CONSTRAINT `FKarjceitqbb3i4srbhyaby4cyr` FOREIGN KEY (`idJustificaicon`) REFERENCES `Justificacion` (`idJustificaicon`),
  CONSTRAINT `FKbjdx6qfn1gwi7kpgldrdnui5i` FOREIGN KEY (`idEstado`) REFERENCES `Estado` (`idEstado`),
  CONSTRAINT `FKe8qkl2wdf4ntiaqoa6a7ycssm` FOREIGN KEY (`idTurno`) REFERENCES `Turno` (`idTurno`),
  CONSTRAINT `FKn1i6uv0nkoeqh14kka1l8qbs` FOREIGN KEY (`idAlumno`) REFERENCES `Alumno` (`idAlumno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Asistencias`
--

LOCK TABLES `Asistencias` WRITE;
/*!40000 ALTER TABLE `Asistencias` DISABLE KEYS */;
/*!40000 ALTER TABLE `Asistencias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Colegio`
--

DROP TABLE IF EXISTS `Colegio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Colegio` (
  `idColegio` int NOT NULL AUTO_INCREMENT,
  `celular` smallint NOT NULL,
  `codigo` smallint NOT NULL,
  `colegio` varchar(100) NOT NULL,
  `direccion` varchar(100) NOT NULL,
  `gmail` varchar(50) NOT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idColegio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Colegio`
--

LOCK TABLES `Colegio` WRITE;
/*!40000 ALTER TABLE `Colegio` DISABLE KEYS */;
/*!40000 ALTER TABLE `Colegio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ConfiHorario`
--

DROP TABLE IF EXISTS `ConfiHorario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ConfiHorario` (
  `idConfiHorario` int NOT NULL AUTO_INCREMENT,
  `horaEntradaLimite` time(6) NOT NULL,
  `horaFaltaLimite` time(6) NOT NULL,
  `Turno_idTurno` int NOT NULL,
  PRIMARY KEY (`idConfiHorario`),
  KEY `FKfp239e8nrp5kkyirhs4q8qrn0` (`Turno_idTurno`),
  CONSTRAINT `FKfp239e8nrp5kkyirhs4q8qrn0` FOREIGN KEY (`Turno_idTurno`) REFERENCES `Turno` (`idTurno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ConfiHorario`
--

LOCK TABLES `ConfiHorario` WRITE;
/*!40000 ALTER TABLE `ConfiHorario` DISABLE KEYS */;
/*!40000 ALTER TABLE `ConfiHorario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Estado`
--

DROP TABLE IF EXISTS `Estado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Estado` (
  `idEstado` int NOT NULL AUTO_INCREMENT,
  `estado` varchar(10) NOT NULL,
  PRIMARY KEY (`idEstado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Estado`
--

LOCK TABLES `Estado` WRITE;
/*!40000 ALTER TABLE `Estado` DISABLE KEYS */;
/*!40000 ALTER TABLE `Estado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Grado`
--

DROP TABLE IF EXISTS `Grado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Grado` (
  `idGrado` int NOT NULL AUTO_INCREMENT,
  `grado` varchar(3) NOT NULL,
  PRIMARY KEY (`idGrado`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Grado`
--

LOCK TABLES `Grado` WRITE;
/*!40000 ALTER TABLE `Grado` DISABLE KEYS */;
INSERT INTO `Grado` VALUES (1,'1ro'),(2,'2do'),(3,'3ro'),(4,'4to'),(5,'5to');
/*!40000 ALTER TABLE `Grado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Justificacion`
--

DROP TABLE IF EXISTS `Justificacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Justificacion` (
  `idJustificaicon` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  PRIMARY KEY (`idJustificaicon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Justificacion`
--

LOCK TABLES `Justificacion` WRITE;
/*!40000 ALTER TABLE `Justificacion` DISABLE KEYS */;
/*!40000 ALTER TABLE `Justificacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Modulo`
--

DROP TABLE IF EXISTS `Modulo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Modulo` (
  `idModulo` int NOT NULL AUTO_INCREMENT,
  `estado` smallint NOT NULL,
  `fechaCreacion` date NOT NULL,
  `nombre` varchar(20) NOT NULL,
  PRIMARY KEY (`idModulo`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Modulo`
--

LOCK TABLES `Modulo` WRITE;
/*!40000 ALTER TABLE `Modulo` DISABLE KEYS */;
INSERT INTO `Modulo` VALUES (1,0,'2026-05-24','Asistencia Vir');
/*!40000 ALTER TABLE `Modulo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Roles`
--

DROP TABLE IF EXISTS `Roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Roles` (
  `idRoles` int NOT NULL AUTO_INCREMENT,
  `Color` varchar(7) NOT NULL,
  `estado` smallint NOT NULL,
  `fechaCreacion` date NOT NULL,
  `nombreRol` varchar(20) NOT NULL,
  PRIMARY KEY (`idRoles`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Roles`
--

LOCK TABLES `Roles` WRITE;
/*!40000 ALTER TABLE `Roles` DISABLE KEYS */;
INSERT INTO `Roles` VALUES (1,'#4361EE',1,'2026-05-23','Administrador'),(2,'#4361EE',1,'2026-05-23','admin'),(3,'#4361EE',1,'2026-05-24','Coordinador');
/*!40000 ALTER TABLE `Roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RolesModulo`
--

DROP TABLE IF EXISTS `RolesModulo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RolesModulo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `idModulo` int NOT NULL,
  `idRoles` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr808k8tb17aikbp7alij0307q` (`idModulo`),
  KEY `FK8ydwk5vq3ey0wlvmap3mw5e91` (`idRoles`),
  CONSTRAINT `FK8ydwk5vq3ey0wlvmap3mw5e91` FOREIGN KEY (`idRoles`) REFERENCES `Roles` (`idRoles`),
  CONSTRAINT `FKr808k8tb17aikbp7alij0307q` FOREIGN KEY (`idModulo`) REFERENCES `Modulo` (`idModulo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RolesModulo`
--

LOCK TABLES `RolesModulo` WRITE;
/*!40000 ALTER TABLE `RolesModulo` DISABLE KEYS */;
INSERT INTO `RolesModulo` VALUES (1,1,3),(2,1,1),(3,1,2);
/*!40000 ALTER TABLE `RolesModulo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Seccion`
--

DROP TABLE IF EXISTS `Seccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Seccion` (
  `idSeccion` int NOT NULL AUTO_INCREMENT,
  `seccion` varchar(20) NOT NULL,
  PRIMARY KEY (`idSeccion`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Seccion`
--

LOCK TABLES `Seccion` WRITE;
/*!40000 ALTER TABLE `Seccion` DISABLE KEYS */;
INSERT INTO `Seccion` VALUES (1,'A');
/*!40000 ALTER TABLE `Seccion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Turno`
--

DROP TABLE IF EXISTS `Turno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Turno` (
  `idTurno` int NOT NULL AUTO_INCREMENT,
  `turno` varchar(20) NOT NULL,
  PRIMARY KEY (`idTurno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Turno`
--

LOCK TABLES `Turno` WRITE;
/*!40000 ALTER TABLE `Turno` DISABLE KEYS */;
/*!40000 ALTER TABLE `Turno` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Usuario`
--

DROP TABLE IF EXISTS `Usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Usuario` (
  `idUsuario` int NOT NULL AUTO_INCREMENT,
  `apellidoMaterno` varchar(20) NOT NULL,
  `apellidoPaterno` varchar(20) NOT NULL,
  `codigUsuario` varchar(255) NOT NULL,
  `contraseña` varchar(255) NOT NULL,
  `email` varchar(50) NOT NULL,
  `estado` smallint NOT NULL,
  `fechaCreacion` datetime(6) NOT NULL,
  `nombre` varchar(20) NOT NULL,
  PRIMARY KEY (`idUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Usuario`
--

LOCK TABLES `Usuario` WRITE;
/*!40000 ALTER TABLE `Usuario` DISABLE KEYS */;
INSERT INTO `Usuario` VALUES (1,'Castro','Mendoza','UCM266260','$2a$10$R8E3kcKYEBajL5EsF6pkRe8OVHC4YJhWxOCTysR0C082Hx9UsTQK6','carlos.mendoza@gmail.com',1,'2026-05-23 14:55:08.848435','Carlos Modificado'),(2,'Quito','Roman','AJR267FD2','$2a$10$epmBKJ3zgUUxnbvhVKKBouXRBGhzFCWJGbgCa1BzZg1WG6wTsg4gC','jonromanquito@gmail.com',1,'2026-05-24 03:57:38.775972','Jhon Carlos');
/*!40000 ALTER TABLE `Usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UsuarioRoles`
--

DROP TABLE IF EXISTS `UsuarioRoles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `UsuarioRoles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fechaAsignacion` date DEFAULT NULL,
  `idRoles` int NOT NULL,
  `idUsuario` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9so6tdnncs3osq23j5afrug6l` (`idRoles`),
  KEY `FKafrmrq5s8l56dujdpxjvle345` (`idUsuario`),
  CONSTRAINT `FK9so6tdnncs3osq23j5afrug6l` FOREIGN KEY (`idRoles`) REFERENCES `Roles` (`idRoles`),
  CONSTRAINT `FKafrmrq5s8l56dujdpxjvle345` FOREIGN KEY (`idUsuario`) REFERENCES `Usuario` (`idUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UsuarioRoles`
--

LOCK TABLES `UsuarioRoles` WRITE;
/*!40000 ALTER TABLE `UsuarioRoles` DISABLE KEYS */;
INSERT INTO `UsuarioRoles` VALUES (2,'2026-05-23',2,2),(3,'2026-05-23',1,1);
/*!40000 ALTER TABLE `UsuarioRoles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-27 22:51:49
