package impl.com.github.paulcwarren.ginkgo4j.builder;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;
import impl.com.github.paulcwarren.ginkgo4j.chains.SpecsCollector;
import org.junit.runner.RunWith;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Ginkgo4jRunner.class)
@Ginkgo4jConfiguration(threads = 1)
public class SpecCollectorTests {

    private TestWalker walker;
    private SpecsCollector collector;
    private Throwable t;

    {
        Describe("Spec Collector", () -> {
            BeforeEach(() -> {
                collector = new SpecsCollector();
                walker = new TestWalker(JustTest.class);
            });

            JustBeforeEach(() -> {
                try {
                    walker.walk(collector);
                } catch (Throwable t) {
                    this.t = t;
                }
            });

            It("should collect all specs", () -> {
                assertThat(collector.getSpecs(), is(not(nullValue())));
                assertThat(collector.getSpecs().size(), is(1));
            });

            Context("when specs are focused", () -> {
                BeforeEach(() -> walker = new TestWalker(FItTest.class));

                It("should collect focussed specs only", () -> {
                    assertThat(collector.getSpecs(), is(not(nullValue())));
                    assertThat(collector.getSpecs().size(), is(2));
                    assertThat(collector.getSpecs().get(0).getId(), is("JustTest Class.A context.focussed"));
                    assertThat(collector.getSpecs().get(0).isFocused(), is(true));
                    assertThat(collector.getSpecs().get(1).getId(), is("JustTest Class.A context.not focussed"));
                    assertThat(collector.getSpecs().get(1).isFocused(), is(false));
                });
            });

            Context("when specs contain empty labels", () -> {
                BeforeEach(() -> walker = new TestWalker(EmptyLabelsTest.class));

                It("should collect specs with parsed IDs", () -> {
                    assertThat(collector.getSpecs(), is(not(nullValue())));
                    assertThat(collector.getSpecs().size(), is(1));
                    assertThat(collector.getSpecs().get(0).getId(), is("_EMPTY_._EMPTY_._EMPTY_"));
                });
            });

            Context("when a describe throws an exception", () -> {
                BeforeEach(() -> walker = new TestWalker(ExceptionThrowingDescribeTest.class));

                It("should throw the exception", () -> assertThat(t, instanceOf(NullPointerException.class)));
            });

            Context("when a context throws an exception", () -> {
                BeforeEach(() -> walker = new TestWalker(ExceptionThrowingContextTest.class));

                It("should throw the exception", () -> assertThat(t, instanceOf(NullPointerException.class)));
            });

            Context("when a beforeEach throws an exception", () -> {
                BeforeEach(() -> walker = new TestWalker(ExceptionThrowingBeforeEachTest.class));

                It("should throw the exception", () -> assertThat(t, is(nullValue())));
            });

            Context("when a justBeforeEach throws an exception", () -> {
                BeforeEach(() -> walker = new TestWalker(ExceptionThrowingJustBeforeEachTest.class));

                It("should not throw the exception", () -> assertThat(t, is(nullValue())));
            });

            Context("when an afterEach throws an exception", () -> {
                BeforeEach(() -> walker = new TestWalker(ExceptionThrowingAfterEachTest.class));

                It("should not throw the exception", () -> assertThat(t, is(nullValue())));
            });
        });
    }

    static class JustTest {
        {
            Describe("JustTest Class", () -> {
                JustBeforeEach(() -> {
                });
                Context("A context", () -> {
                    BeforeEach(() -> {
                    });
                    It("should do something", () -> {
                    });
                });
                AfterEach(() -> {
                });
            });
        }
    }

    static class FItTest {
        {
            Describe("JustTest Class", () -> {
                JustBeforeEach(() -> {
                });
                Context("A context", () -> {
                    BeforeEach(() -> {
                    });
                    FIt("focussed", () -> {
                    });
                    It("not focussed", () -> {
                    });
                });
                AfterEach(() -> {
                });
            });
        }
    }

    static class EmptyLabelsTest {
        {
            Describe("", () -> {
                Context("", () -> It("", () -> {
                }));
                AfterEach(() -> {
                });
            });
        }
    }

    static class ExceptionThrowingDescribeTest {
        private final String something = null;

        {
            Describe("A describe", () -> {
                something.length();
                Context("A context", () -> It("A test", () -> {
                }));
            });
        }
    }

    static class ExceptionThrowingContextTest {
        private final String something = null;

        {
            Describe("A describe", () ->
                    Context("A context", () -> {
                        something.length();
                        It("A test", () -> {
                        });
                    }));
        }
    }

    static class ExceptionThrowingBeforeEachTest {
        private final String something = null;

        {
            Describe("A describe", () -> {
                BeforeEach(() -> something.length());
                Context("A context", () -> It("A test", () -> {
                }));
            });
        }
    }

    static class ExceptionThrowingJustBeforeEachTest {
        private final String something = null;

        {
            Describe("A describe", () -> {
                JustBeforeEach(() -> something.length());
                Context("A context", () -> It("A test", () -> {
                }));
            });
        }
    }

    static class ExceptionThrowingAfterEachTest {
        private final String something = null;

        {
            Describe("A describe", () -> {
                Context("A context", () -> It("A test", () -> {
                }));
                AfterEach(() -> something.length());
            });
        }
    }
}
