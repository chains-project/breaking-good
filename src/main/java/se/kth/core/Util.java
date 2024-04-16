package se.kth.core;

import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class Util {


    public boolean isFolder(String path) {
        return Files.isDirectory(Paths.get(path));
    }


    public static String fullyQualifiedName(CtReference ref) {
        String fqn = "";

        if (ref instanceof CtTypeReference<?> tRef)
            fqn = tRef.getQualifiedName();
        else if (ref instanceof CtExecutableReference<?> eRef)
            fqn = eRef.getDeclaringType().getQualifiedName().concat(".").concat(eRef.getSimpleName());
        else if (ref instanceof CtFieldReference<?> fRef)
            fqn = fRef.getDeclaringType().getQualifiedName().concat(".").concat(fRef.getSimpleName());

        return fqn;
    }


}
