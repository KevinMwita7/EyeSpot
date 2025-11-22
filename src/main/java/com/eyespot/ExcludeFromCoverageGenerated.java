package com.eyespot;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to exclude methods, constructors, or classes from JaCoCo code coverage reports.
 *
 * <p>This annotation is automatically recognized by JaCoCo (version 0.8.2+) because it contains
 * "Generated" in its name. Code annotated with this will be excluded from coverage metrics.
 *
 * <p>This annotation is primarily used to exclude main() methods and other entry points that are
 * difficult to test directly from coverage metrics.
 *
 * <p>Usage:
 *
 * <pre>
 * &#64;ExcludeFromCoverageGenerated
 * public static void main(String[] args) {
 *   // This method will be excluded from coverage
 * }
 * </pre>
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface ExcludeFromCoverageGenerated {}
