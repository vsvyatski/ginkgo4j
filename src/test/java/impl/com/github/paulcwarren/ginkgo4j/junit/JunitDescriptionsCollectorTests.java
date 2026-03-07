package impl.com.github.paulcwarren.ginkgo4j.junit;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;
import impl.com.github.paulcwarren.ginkgo4j.builder.TestWalker;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

import java.lang.annotation.Annotation;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Ginkgo4jRunner.class)
@Ginkgo4jConfiguration(threads = 1)
public class JunitDescriptionsCollectorTests {

    private TestWalker walker;
    private JunitDescriptionsCollector collector;
    private Description description;

    {
        Describe("JunitDescriptionsCollector", () -> {
            JustBeforeEach(() -> walker.walk(collector));
            Context("given a test class with a conventional Describe->Context->InvokeTest structure", () -> {
                BeforeEach(() -> {
                    description = Description.createSuiteDescription(TestClass.class.getName(), (Annotation[]) null);
                    collector = new JunitDescriptionsCollector(TestClass.class, description);
                    walker = new TestWalker(TestClass.class);
                });
                It("should collect all descriptions into a tree structure under the root description", () -> {
                    assertThat(description.getChildren().size(), is(1));
                    assertThat(description.getChildren().get(0).getDisplayName(), is("JustTest Class.A context.should do something(impl.com.github.paulcwarren.ginkgo4j.junit.JunitDescriptionsCollectorTests$JustTest)"));
                });
                It("should collect all descriptions into a quick access map", () -> {
                    assertThat(collector.getDescriptions(), is(not(nullValue())));
                    assertThat(collector.getDescriptions().get("JustTest Class.A context.should do something"), is(not(nullValue())));
                });
            });
            Context("given a test class with a top-level Context", () -> {
                BeforeEach(() -> {
                    description = Description.createSuiteDescription(TestClass.class.getName(), (Annotation[]) null);
                    collector = new JunitDescriptionsCollector(TestClass.class, description);
                    walker = new TestWalker(TestClass.class);
                });
                It("should collect all descriptions into a tree structure under the root description", () -> {
                    assertThat(description.getChildren().size(), is(1));
                    assertThat(description.getChildren().get(0).getDisplayName(), is("JustTest Class.A context.should do something(impl.com.github.paulcwarren.ginkgo4j.junit.JunitDescriptionsCollectorTests$JustTest)"));
                });
                It("should collect all descriptions into a quick access map", () -> {
                    assertThat(collector.getDescriptions(), is(not(nullValue())));
                    assertThat(collector.getDescriptions().get("JustTest Class.A context.should do something"),
                            is(not(nullValue())));
                });
            });
            Context("given a test class with a nested Describe", () -> {
                BeforeEach(() -> {
                    description = Description.createSuiteDescription(TestClass.class.getName(), (Annotation[]) null);
                    collector = new JunitDescriptionsCollector(TestClass.class, description);
                    walker = new TestWalker(NestedDescribeTest.class);
                });
                It("should collect all descriptions into a tree structure under the root description", () -> {
                    assertThat(description.getChildren().size(), is(1));
                    assertThat(description.getChildren().get(0).getDisplayName(),
                            is("JustTest Class.A context.A nested describe.should do something(impl.com.github.paulcwarren.ginkgo4j.junit.JunitDescriptionsCollectorTests$JustTest)"));
                });
                It("should collect all descriptions into a quick access map", () -> {
                    assertThat(collector.getDescriptions(), is(not(nullValue())));
                    assertThat(collector.getDescriptions().get("JustTest Class.A context.A nested describe.should do something"), is(not(nullValue())));
                });
            });
            Context("given a test class with empty labels", () -> {
                BeforeEach(() -> {
                    description = Description.createSuiteDescription(EmptyLabelsTest.class.getName(), (Annotation[]) null);
                    collector = new JunitDescriptionsCollector(EmptyLabelsTest.class, description);
                    walker = new TestWalker(EmptyLabelsTest.class);
                });
                It("should use spaces for the description display names", () -> {
                    assertThat(description.getChildren().size(), is(1));
                    assertThat(description.getChildren().get(0).getDisplayName(),
                            is("_EMPTY_._EMPTY_._EMPTY_(impl.com.github.paulcwarren.ginkgo4j.junit.JunitDescriptionsCollectorTests$EmptyLabelsTest)"));
                });
                It("should use a placeholder for the description fully-qualified IDs", () -> {
                    assertThat(collector.getDescriptions(), is(not(nullValue())));
                    assertThat(collector.getDescriptions().get("_EMPTY_._EMPTY_._EMPTY_"), is(not(nullValue())));
                });
            });
        });
    }

    public static class TestClass {
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

    public static class TopLevelContextTest {
        {
            Context("JustTest Class", () -> {
                JustBeforeEach(() -> {
                });
                Describe("A context", () -> {
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

    public static class NestedDescribeTest {
        {
            Describe("JustTest Class", () -> {
                JustBeforeEach(() -> {
                });
                Context("A context", () -> {
                    BeforeEach(() -> {
                    });
                    Describe("A nested describe", () -> It("should do something", () -> {
                    }));
                });
                AfterEach(() -> {
                });
            });
        }
    }

    public static class EmptyLabelsTest {
        {
            Describe("", () -> Context("", () -> It("", () -> {
            })));
        }
    }
}
