package com.transportsim.bridge.annotations

/**
 * Used to mark classes/methods that should be kept by ProGuard
 * because they are accessed via JNI from native code.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.BINARY)
annotation class Keep