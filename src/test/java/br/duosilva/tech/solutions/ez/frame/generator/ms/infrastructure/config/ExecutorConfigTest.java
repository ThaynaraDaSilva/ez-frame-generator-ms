package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;


public class ExecutorConfigTest {
	@Test
    void shouldCreateExecutorServiceWithConfiguredThreadPoolSize() {
        ExecutorConfig config = new ExecutorConfig();
        ReflectionTestUtils.setField(config, "threadPoolSize", 10);

        ExecutorService executorService = config.frameProcessorExecutor();

        assertNotNull(executorService);
        assertTrue(executorService instanceof ThreadPoolExecutor);
        assertEquals(10, ((ThreadPoolExecutor) executorService).getMaximumPoolSize());
    }
}
