CI detected that the dependency upgrade from version **struts2-core-2.3.37** to **struts2-core-2.5.22** has failed. Here are details to help you understand and fix the problem:
1. Your client utilizes **3** constructs which has been modified in the new version of the dependency.
   * <details>
        <summary>Class <b>org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          
            *   >[[ERROR] /guice/extensions/struts2/test/com/google/inject/struts2/Struts2FactoryTest.java:[55,19] error: cannot find symbol<br>&nbsp;&nbsp;&nbsp;&nbsp;](XXXX)
            *   An error was detected in line 55 which is making use of an outdated API.
             ``` java
             55   org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter.class;
            ```

          </details>
            
     </details>
   * <details>
        <summary>Class <b>org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          
            *   >[[ERROR] /guice/extensions/struts2/test/com/google/inject/struts2/Struts2FactoryTest.java:[55,19] error: cannot find symbol<br>&nbsp;&nbsp;&nbsp;&nbsp;](XXXX)
            *   An error was detected in line 55 which is making use of an outdated API.
             ``` java
             55   bind(java.lang.Class);
            ```

          </details>
            
     </details>
   * <details>
        <summary>Class <b>org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          
            *   >[[ERROR] /guice/extensions/struts2/test/com/google/inject/struts2/Struts2FactoryTest.java:[57,35] error: cannot find symbol<br>&nbsp;&nbsp;&nbsp;&nbsp;](XXXX)
            *   An error was detected in line 57 which is making use of an outdated API.
             ``` java
             57   org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter.class;
            ```

          </details>
            
     </details>


