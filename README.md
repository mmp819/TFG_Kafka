# TFG_Kafka
Material desarrollado y empleado a lo largo del Trabajo de Fin de Grado: *Diseño e implementación de un servicio de monitorización y aseguramiento de la calidad y seguridad de datos publicados en Kafka*.

------------------------------------------

## Estructura del repositorio:
* **Certificados Kafka**: Certificados utilizados para autenticar a los *principals* de Kafka. Se encuentran divididos en carpetas para cada *principal*.

  Autoridades de Certificación:
  - BrokersCA
  - ClientesCA
  
  *Principals*:
  - mario (Broker1)
  - Admin (Administrador)
  - Druid
  - Productor
  - Spark-Cassandra
* **Configuraciones**: Ficheros *.yml* y *.properties* utilizados para configurar Kafka y otras tecnologías conectadas.
  - Exporter
  - Kafka
  - Prometheus
  - Zookeeper
* **DashboardsGrafana**: Cuadros de mando elaborados con Grafana para la monitorización de Kafka y la visualización de datos del *dataset* MetroPT.
* **KttmSpark**: Programa Spark utilizado para el *dataset* del juego web, *Koalas to the Max*.
* **ProductorJavaGeneric**: Programa Java encargado de leer un fichero de datos como parámetro y mandar sus filas al tópico indicado de Kafka.
* **TrainSpark**: Programa Spark utilizado para el *dataset* MetroPT.

------------------------------------------

## Arquitectura y solución software empleada:


![](Arquitectura-SolucionSwPropuesta.png)
