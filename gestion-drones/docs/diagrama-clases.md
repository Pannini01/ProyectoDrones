# Diagrama de clases corregido

El archivo fuente editable es `diagrama-clases.puml`. Las dependencias de
`DronDAO` se conectan directamente con `Dron`, `Agricultura`, `Vigilancia` y
`PostgresConnection`, aunque pertenezcan a paquetes distintos.

```mermaid
classDiagram
direction TB

class Dron {
  <<abstract>>
  -String id
  -String serial
  -String modelo
  -String fabricante
  -double peso
  +clonar() Dron
}
class Agricultura { -double capacidadTanque }
class Vigilancia { -boolean deteccionTermica }
class Piloto
class Sensor
class Mision
Dron <|-- Agricultura
Dron <|-- Vigilancia
Dron --> Piloto
Dron o-- Sensor
Mision o-- Dron

class Prototipo~T~ {
  <<interface>>
  +clonar() T
}
class DronBuilder~T~ {
  <<interface>>
  +build() T
}
class AgriculturaDronBuilder
class VigilanciaDronBuilder
class PrototypeRegistry~T~
Dron ..|> Prototipo
DronBuilder <|.. AgriculturaDronBuilder
DronBuilder <|.. VigilanciaDronBuilder
AgriculturaDronBuilder --> Agricultura
VigilanciaDronBuilder --> Vigilancia
PrototypeRegistry o-- Prototipo

class Crud~T_ID~ {
  <<interface>>
  +crear(T) T
  +buscarPorId(ID) Optional~T~
  +listar() List~T~
  +actualizar(T) boolean
  +eliminar(ID) boolean
}
class DronDAO
class PostgresConnection {
  <<Singleton>>
  +getInstance() PostgresConnection
  +getConnection() Connection
}
Crud <|.. DronDAO
DronDAO --> Dron : persiste
DronDAO --> Agricultura : reconstruye
DronDAO --> Vigilancia : reconstruye
DronDAO --> PostgresConnection : conecta
```
