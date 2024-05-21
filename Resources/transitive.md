CI detected that the dependency upgrade from version **datafaker-1.3.0** to **datafaker-1.4.0** has failed. Here are
details to help you understand and fix the problem:

Dependency #### was removed in the new version of updated dependency (**datafaker-1.4.0**).
Your client call constructs that belong to the removed dependency

<details>
<summary>Here you can find a list of failures identified from the logs generated in the build process</summary>

   *    > [[ERROR] /flink-faker/src/main/java/com/github/knaufk/flink/faker/DateTime.java:[45,40] incompatible types: java.util.Date cannot be converted to java.sql.Timestamp](https://github.com/chains-project/breaking-good/actions/runs/8110103454/job/22166641300#step:4:709)
         * An error was detected in line 45 which is making use of an outdated API.
          ``` java
          45   super.between(from, to);
          ```
   *    > [[ERROR] /flink-faker/src/main/java/com/github/knaufk/flink/faker/DateTime.java:[45,40] incompatible types: java.util.Date cannot be converted to java.sql.Timestamp](https://github.com/chains-project/breaking-good/actions/runs/8110103454/job/22166641300#step:4:709)
         * An error was detected in line 45 which is making use of an outdated API.
          ``` java
          45   super.between(from, to);
          ```     
</details>

