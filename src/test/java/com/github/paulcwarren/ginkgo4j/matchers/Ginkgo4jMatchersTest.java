package com.github.paulcwarren.ginkgo4j.matchers;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;
import org.junit.Assert;
import org.junit.runner.RunWith;

import java.time.Duration;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static com.github.paulcwarren.ginkgo4j.matchers.Ginkgo4jMatchers.eventually;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Ginkgo4jRunner.class)
public class Ginkgo4jMatchersTest {

    {
        Describe("#eventually", () -> {

            Context("when the subject returns a passing result", () ->
                    It("should be successful", () -> eventually(() -> true, Assert::assertTrue))
            );
            Context("when the duration expires", () -> It("should throw an AssertionError", () -> {
                try {
                    eventually(
                            () -> null,
                            (result) -> fail("shouldn't call this"),
                            Duration.ofMillis(100), Duration.ofSeconds(1));
                    fail("shouldn't call this");
                } catch (Throwable e) {
                    assertTrue(e instanceof AssertionError);
                }
            }));
        });
    }
}
