-injars './build/libs/PTXCreatorJava.jar'
-outjars './build/libs/PTXCreator-obfuscated.jar'

-libraryjars '<java.home>/lib/rt.jar'
-libraryjars './dependencies/gson-2.2.4.jar'
-libraryjars './dependencies/zip4j-1.3.2.jar'

-optimizationpasses 2
-flattenpackagehierarchy root
-repackageclasses root
-keepattributes InnerClasses

-keep class com.kpit.ptxcreator.IPTXCreator {
    *** ***;
    *** ***(...);
}

-keep class com.kpit.ptxcreator.PTXCreatorDTO {
    *** ***;
    *** ***(...);
}

-keep class com.kpit.ptxcreator.PTXCreatorFactory {
    *** ***;
    *** ***(...);
}

-keep class com.kpit.ptxcreator.exception.PTXCreatorException {
    *** ***;
    *** ***(...);
}

-keep class com.kpit.ptxcreator.exception.ErrorCode {*;}
-keep class com.kpit.ptxcreator.signature.SignAlgorithm {*;}
-keep class com.kpit.ptxcreator.signature.HashAlgorithm {*;}
-keep class com.kpit.ptxcreator.PTXCreatorDTO$ComplianceType {*;}