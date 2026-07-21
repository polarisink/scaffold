package com.scaffold.orm.starter.nativeimage;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Finds lambda capturing classes without loading every application class.
 */
public final class LambdaCapturingClassScanner {

    private static final int CLASS_FILE_MAGIC = 0xCAFEBABE;
    private static final String DESERIALIZE_LAMBDA_METHOD = "$deserializeLambda$";

    public Set<String> scan(ClassLoader applicationClassLoader) throws IOException {
        Set<String> classNames = new LinkedHashSet<>();
        Enumeration<URL> roots = applicationClassLoader.getResources("");
        while (roots.hasMoreElements()) {
            URL root = roots.nextElement();
            if (!"file".equals(root.getProtocol())) {
                continue;
            }
            try {
                classNames.addAll(scan(Path.of(root.toURI())));
            } catch (URISyntaxException exception) {
                throw new IOException("Invalid application classes URL " + root, exception);
            }
        }
        return classNames;
    }

    public Set<String> scan(Path classesDirectory) throws IOException {
        Set<String> classNames = new LinkedHashSet<>();
        if (!Files.isDirectory(classesDirectory)) {
            return classNames;
        }
        try (Stream<Path> paths = Files.walk(classesDirectory)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".class"))
                    .filter(this::containsDeserializeLambdaMethod)
                    .map(classesDirectory::relativize)
                    .map(this::toClassName)
                    .forEach(classNames::add);
        }
        return classNames;
    }

    private boolean containsDeserializeLambdaMethod(Path classFile) {
        try (DataInputStream input = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(classFile)))) {
            if (input.readInt() != CLASS_FILE_MAGIC) {
                return false;
            }
            input.readUnsignedShort();
            input.readUnsignedShort();
            int constantPoolCount = input.readUnsignedShort();
            for (int index = 1; index < constantPoolCount; index++) {
                int tag = input.readUnsignedByte();
                switch (tag) {
                    case 1 -> {
                        if (DESERIALIZE_LAMBDA_METHOD.equals(input.readUTF())) {
                            return true;
                        }
                    }
                    case 3, 4 -> input.skipNBytes(4);
                    case 5, 6 -> {
                        input.skipNBytes(8);
                        index++;
                    }
                    case 7, 8, 16, 19, 20 -> input.skipNBytes(2);
                    case 9, 10, 11, 12, 17, 18 -> input.skipNBytes(4);
                    case 15 -> input.skipNBytes(3);
                    default -> throw new IOException("Unsupported class constant pool tag " + tag
                            + " in " + classFile);
                }
            }
            return false;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to inspect class file " + classFile, exception);
        }
    }

    private String toClassName(Path relativeClassFile) {
        String className = relativeClassFile.toString();
        className = className.substring(0, className.length() - ".class".length());
        return className.replace('/', '.').replace('\\', '.');
    }
}
