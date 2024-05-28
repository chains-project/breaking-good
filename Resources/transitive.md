CI detected that the dependency upgrade from version <**label indicating the previous version of the dependency**> to <**label indicating the new version of the dependency**> has failed. Here are
details to help you understand and fix the problem:

Dependency <**label indicating dependency**> was removed in the new version of the updated dependency (<**label indicating the new version of the dependency**>).
Your client calls constructs that belong to the removed dependency

<details>
<summary>Here you can find a list of failures identified from the logs generated in the build process</summary>

   *    > Label to indicate the error message in the logs
         * An error was detected in line < Label indicate line in client> which is making use of an outdated API.
             ``` java
            < Line in client >
            ```
   *    > Label to indicate the error message in the logs
         * An error was detected in line < Label indicate line in client> which is making use of an outdated API.
             ``` java
            < Line in client >
            ```     
</details>

