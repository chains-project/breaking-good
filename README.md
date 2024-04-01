# Breaking-good

# Template

CI detected that the dependency upgrade from version <**label indicating the previous version of the dependency**> to <**label indicating the new version of the dependency**> has failed. Here are details to help you understand and fix the problem:
1. Your client utilizes <**label indicate amount of instructions**> instructions which has been modified in the new version of the dependency.
    * <summary> < Method | Class | Field | Import | Constructor> <b>< instruction name ></b> which has been < <b>Removed | Modified </b> > in the new version of the dependency</summary>

        *  <summary>The failure is identified from the logs generated in the build process. </summary>

            * > Label to indicate the error message in the logs
            * An error was detected in line < Label indicates line in client> which is making use of an outdated API.
             ``` java
            < Line in client >
            ```

      To resolve this issue, there are alternative options available in the new version of the dependency that can replace the incompatible method currently used in the client. You can consider substituting the existing method with one of the following options provided by the new version of the dependency
         ``` java
        < New method signature>
         ```


