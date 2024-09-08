//package com.scaffold.core.openjdk;
//
//import org.junit.jupiter.api.Test;
//
//import java.lang.foreign.*;
//import java.lang.invoke.MethodHandle;
//
//import static java.lang.foreign.ValueLayout.ADDRESS;
//import static java.lang.foreign.ValueLayout.JAVA_INT;
//
///**
// * java21使用ffm调用本地dll
// */
//public class FfmTest {
//    @Test
//    void myMethod() {
//        String myNativeMethod = """
//                #include <stdio.h>
//                //编译为dll：gcc -shared -o MyNativeLib.dll MyNativeLib.c
//                void myNativeMethod(int number,const char* message){
//                    printf("received number: %d and message: %s", number, message);
//                }
//                """;
//    }
//
//    static {
//        //加载位于classpath下的dll文件
//        try {
//            System.loadLibrary("myNativeLib");
//        } catch (UnsatisfiedLinkError e) {
//            System.load(Object.class.getResource("/MyNativeLib.dll").getPath());
//        }
//    }
//
//    /**
//     * 本地方法
//     *
//     * @param number  num
//     * @param message message
//     * @throws Throwable
//     */
//    public static void callNativeMethod(int number, String message) throws Throwable {
//        try (Arena arena = Arena.ofConfined()) {
//            SymbolLookup lookup = SymbolLookup.loaderLookup();
//            MemorySegment memorySegment = arena.allocateUtf8String(message);
//            MethodHandle methodHandle = Linker.nativeLinker().downcallHandle(
//                    lookup.find("myNativeMethod").orElseThrow(),
//                    FunctionDescriptor.ofVoid(JAVA_INT, ADDRESS)
//            );
//            methodHandle.invoke(number, memorySegment);
//        }
//    }
//}
