CI detected that the dependency upgrade from version **google-cloud-pubsublite-0.6.0** to **google-cloud-pubsublite-1.6.3** has failed. Here are details to help you understand and fix the problem:
1. Your client utilizes **9** constructs which has been modified in the new version of the dependency.
   * <details>
        <summary>Method <b>setContext(com.google.cloud.pubsublite.internal.wire.PubsubContext)</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          

          </details>
            
     </details>
   * <details>
        <summary>Class <b>com.google.cloud.pubsublite.PublishMetadata</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          
            *   >[[ERROR] /java-pubsub-group-kafka-connector/src/main/java/com/google/pubsublite/kafka/sink/PublisherFactory.java:[18,35] cannot find symbol<br>&nbsp;&nbsp;&nbsp;&nbsp;  symbol:   class PublishMetadata
  location: package com.google.cloud.pubsublite
](https://github.com/chains-project/breaking-good/actions/runs/8110103454/job/22166641300#step:4:1304)
            *   An error was detected in line 18 which is making use of an outdated API.
             ``` java
             18   import com.google.cloud.pubsublite.PublishMetadata;;
            ```

          </details>
            
     </details>
   * <details>
        <summary>Class <b>com.google.cloud.pubsublite.PublishMetadata</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          
            *   >[[ERROR] /java-pubsub-group-kafka-connector/src/main/java/com/google/pubsublite/kafka/sink/PubSubLiteSinkTask.java:[22,35] cannot find symbol<br>&nbsp;&nbsp;&nbsp;&nbsp;  symbol:   class PublishMetadata
  location: package com.google.cloud.pubsublite
](https://github.com/chains-project/breaking-good/actions/runs/8110103454/job/22166641300#step:4:1307)
            *   An error was detected in line 22 which is making use of an outdated API.
             ``` java
             22   import com.google.cloud.pubsublite.PublishMetadata;;
            ```

          </details>
            
     </details>
   * <details>
        <summary>Class <b>com.google.cloud.pubsublite.PublishMetadata</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          

          </details>
            
     </details>
   * <details>
        <summary>Class <b>com.google.cloud.pubsublite.PublishMetadata</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          

          </details>
            
     </details>
   * <details>
        <summary>Class <b>com.google.cloud.pubsublite.PublishMetadata</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          
            *   >[[ERROR] /java-pubsub-group-kafka-connector/src/main/java/com/google/pubsublite/kafka/sink/PubSubLiteSinkTask.java:[43,31] cannot find symbol<br>&nbsp;&nbsp;&nbsp;&nbsp;  symbol:   class PublishMetadata
  location: class com.google.pubsublite.kafka.sink.PubSubLiteSinkTask
](https://github.com/chains-project/breaking-good/actions/runs/8110103454/job/22166641300#step:4:1308)
            *   An error was detected in line 43 which is making use of an outdated API.
             ``` java
             43   com.google.cloud.pubsublite.internal.Publisher<com.google.cloud.pubsublite.PublishMetadata>;
            ```

          </details>
            
     </details>
   * <details>
        <summary>Class <b>com.google.cloud.pubsublite.PublishMetadata</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          
            *   >[[ERROR] /java-pubsub-group-kafka-connector/src/main/java/com/google/pubsublite/kafka/sink/PublisherFactory.java:[24,13] cannot find symbol<br>&nbsp;&nbsp;&nbsp;&nbsp;  symbol:   class PublishMetadata
  location: interface com.google.pubsublite.kafka.sink.PublisherFactory
](https://github.com/chains-project/breaking-good/actions/runs/8110103454/job/22166641300#step:4:1306)
            *   An error was detected in line 24 which is making use of an outdated API.
             ``` java
             24   com.google.cloud.pubsublite.internal.Publisher<com.google.cloud.pubsublite.PublishMetadata>;
            ```

          </details>
            
     </details>
   * <details>
        <summary>Class <b>com.google.cloud.pubsublite.PublishMetadata</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          
            *   >[[ERROR] /java-pubsub-group-kafka-connector/src/main/java/com/google/pubsublite/kafka/sink/PublisherFactoryImpl.java:[20,35] cannot find symbol<br>&nbsp;&nbsp;&nbsp;&nbsp;  symbol:   class PublishMetadata
  location: package com.google.cloud.pubsublite
](https://github.com/chains-project/breaking-good/actions/runs/8110103454/job/22166641300#step:4:1303)
            *   An error was detected in line 20 which is making use of an outdated API.
             ``` java
             20   import com.google.cloud.pubsublite.PublishMetadata;;
            ```

          </details>
            
     </details>
   * <details>
        <summary>Class <b>com.google.cloud.pubsublite.PublishMetadata</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          
            *   >[[ERROR] /java-pubsub-group-kafka-connector/src/main/java/com/google/pubsublite/kafka/sink/PublisherFactoryImpl.java:[36,20] cannot find symbol<br>&nbsp;&nbsp;&nbsp;&nbsp;  symbol:   class PublishMetadata
  location: class com.google.pubsublite.kafka.sink.PublisherFactoryImpl
](https://github.com/chains-project/breaking-good/actions/runs/8110103454/job/22166641300#step:4:1305)
            *   An error was detected in line 36 which is making use of an outdated API.
             ``` java
             36   com.google.cloud.pubsublite.internal.Publisher<com.google.cloud.pubsublite.PublishMetadata>;
            ```

          </details>
            
     </details>


