package com.scaffold.orm.starter.nativeimage;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeSerialization;

import java.io.IOException;
import java.util.Set;

/**
 * Registers serializable application lambdas used by MyBatis-Plus wrappers.
 *
 * <p>Only classpath directories are inspected. Class files are checked before loading, so
 * unrelated application classes that reference optional libraries such as POI or Nimbus are
 * never resolved by this feature.</p>
 */
public final class MyBatisLambdaSerializationFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        LambdaCapturingClassScanner scanner = new LambdaCapturingClassScanner();
        Set<String> classNames = findLambdaCapturingClasses(scanner, access.getApplicationClassLoader());
        System.out.println("[scaffold-orm-native] Registering " + classNames.size()
                + " lambda capturing class(es)");
        for (String className : classNames) {
            Class<?> capturingClass = access.findClassByName(className);
            if (capturingClass == null) {
                throw new IllegalStateException("Unable to load lambda capturing class " + className);
            }
            RuntimeSerialization.registerLambdaCapturingClass(capturingClass);
        }
    }

    private Set<String> findLambdaCapturingClasses(
            LambdaCapturingClassScanner scanner, ClassLoader applicationClassLoader) {
        try {
            return scanner.scan(applicationClassLoader);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to scan application classes", exception);
        }
    }
}
