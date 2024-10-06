package com.example.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class WebConfigTest {
    private WebConfig webConfig;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        webConfig = new WebConfig();
    }

    @Test
    void testConfigurePathMatch() {
        PathMatchConfigurer configurer = mock(PathMatchConfigurer.class);

        webConfig.configurePathMatch(configurer);

        verify(configurer, times(1)).addPathPrefix(eq("/api"), any(HandlerTypePredicate.class));
    }


    @Test
    void testPathMatchingForRestController() {
        PathMatchConfigurer configurer = new PathMatchConfigurer();
        webConfig.configurePathMatch(configurer);

        HandlerTypePredicate predicate = HandlerTypePredicate.forAnnotation(RestController.class);
        assertTrue(predicate.test(MockRestController.class));
        assertFalse(predicate.test(MockNonRestController.class));
    }

    // Mock classes for testing HandlerTypePredicate
    @RestController
    private static class MockRestController {
    }

    private static class MockNonRestController {
    }
}
