package dev.api.auth.authservice.common.advice;

import java.lang.annotation.*;

/**
 * Annotation to skip the standard API response formatting.
 * When applied to a controller class or method, the response will not be wrapped in the standard API envelope.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SkipResponseFormat {}
