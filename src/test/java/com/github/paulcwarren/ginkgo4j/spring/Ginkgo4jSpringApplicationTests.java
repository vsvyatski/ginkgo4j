package com.github.paulcwarren.ginkgo4j.spring;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.github.paulcwarren.ginkgo4j.spring.Ginkgo4jSpringApplication.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("deprecation")
@RunWith(Ginkgo4jSpringRunner.class)
@SpringApplicationConfiguration(classes = Ginkgo4jSpringApplication.class)
public class Ginkgo4jSpringApplicationTests {

    @Autowired
    HelloService helloService;

    {
        Describe("Spring integration", () -> {
            It("should be able to use spring beans", () -> assertThat(helloService, is(not(nullValue()))));
            Context("hello service", () ->
                    It("should say hello", () ->
                            assertThat(helloService.sayHello("World"), is("Hello World!"))));
        });
    }

    @Test
    public void noop() {
    }
}
