### Breaking dependency updates 
* Commit: 0ed34fa61c9e31bd4ebb7e4bf12ce792aec96091
  * Cambios detectados por maracas y por breaking good pero no hay nunguno relaciondo con el cliente.


* Commit: 6ac25d3a60ea29a41a37ad47b7feb2908ee84fff
  * False Negative
    * [ClientLine](../projects/6ac25d3a60ea29a41a37ad47b7feb2908ee84fff/myfaces-tobago/tobago-core/src/test/java/org/apache/myfaces/tobago/internal/mock/servlet/MockHttpSession.java)
  
      ```java
       import jakarta.servlet.http.HttpSessionContext;
      ```
      Result from japicmp
    >  ---! REMOVED INTERFACE: PUBLIC(-) ABSTRACT(-) jakarta.servlet.http.HttpSessionContext  (not serializable)
    
        Maracas can't identify the change because it is not in the list of changes that it can detect.
  
    [Line from log: 1765](../projects/6ac25d3a60ea29a41a37ad47b7feb2908ee84fff/myfaces-tobago/6ac25d3a60ea29a41a37ad47b7feb2908ee84fff.log)
  * >[ERROR] /myfaces-tobago/tobago-core/src/test/java/org/apache/myfaces/tobago/internal/mock/servlet/MockHttpSession.java:[24,28] cannot find symbol
    symbol:   class HttpSessionContext
    location: package jakarta.servlet.http