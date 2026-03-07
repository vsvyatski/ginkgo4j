package impl.com.github.paulcwarren.ginkgo4j.builder;

import com.github.paulcwarren.ginkgo4j.ExecutableBlock;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;
import impl.com.github.paulcwarren.ginkgo4j.Context;
import impl.com.github.paulcwarren.ginkgo4j.Describe;
import impl.com.github.paulcwarren.ginkgo4j.chains.ExecutableChainBuilder;
import org.junit.runner.RunWith;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Ginkgo4jRunner.class)
@Ginkgo4jConfiguration(threads = 1)
public class ExecutableChainBuilderTests {

    private final InvokeTest invokeTest1 = new InvokeTest();
    private final InvokeTest invokeTest2 = new InvokeTest();
    private ExecutableChainBuilder builder;

    {
        Describe("ExecutableChainBuilder", () -> {

            Context("when a Describe is invoked", () -> {

                BeforeEach(() -> {
                    builder = new ExecutableChainBuilder("describe.does something");
                    Ginkgo4jDSL.setVisitor(builder);
                    Ginkgo4jDSL.Describe("describe", () -> {
                        BeforeEach(() -> {
                        });
                        JustBeforeEach(() -> {
                        });
                        It("does something", () -> {
                        });
                        AfterEach(() -> {
                        });
                    });
                    Ginkgo4jDSL.unsetVisitor(builder);
                });

                It("should capture the test structure", () -> {
                    assertThat(builder.getExecutableChain().getContext().size(), is(1));
                    assertThat(builder.getExecutableChain().getContext().get(0).getBeforeEach(),
                            is(not(nullValue())));
                    assertThat(builder.getExecutableChain().getContext().get(0).getJustBeforeEach(),
                            is(not(nullValue())));
                    assertThat(builder.getExecutableChain().getSpec(), is(not(nullValue())));
                    assertThat(builder.getExecutableChain().getContext().get(0).getAfterEach(),
                            is(not(nullValue())));
                });
            });

            Context("when a Describe not matching the 'it' filter is invoked", () -> {

                BeforeEach(() -> {
                    builder = new ExecutableChainBuilder("describe.does something");
                    Ginkgo4jDSL.setVisitor(builder);
                    Ginkgo4jDSL.Describe("not describe", () -> {
                        BeforeEach(() -> {
                        });
                        It("does something", () -> {
                        });
                        AfterEach(() -> {
                        });
                    });
                    Ginkgo4jDSL.unsetVisitor(builder);
                });

                It("should ignore the Describe", () -> {
                    assertThat(builder.getExecutableChain().getContext().size(), is(0));
                    assertThat(builder.getExecutableChain().getSpec(), is(nullValue()));
                });
            });

            Context("when a Describe has two similar Describes", () -> {

                BeforeEach(() -> {
                    builder = new ExecutableChainBuilder("describe something similar.context.it");
                    Ginkgo4jDSL.setVisitor(builder);
                    Ginkgo4jDSL.Describe("describe", () ->
                            Context("context", () -> It("it", invokeTest1)));
                    Ginkgo4jDSL.Describe("describe something similar", () ->
                            Context("context", () -> It("it", invokeTest2)));
                    Ginkgo4jDSL.unsetVisitor(builder);
                });

                It("should capture the correct InvokeTest", () ->
                        assertThat(builder.getExecutableChain().getSpec(), is(invokeTest2)));
            });

            Context("when a Describe has two similar Contexts", () -> {

                BeforeEach(() -> {
                    builder = new ExecutableChainBuilder("describe.does something similar.it");
                    Ginkgo4jDSL.setVisitor(builder);
                    Ginkgo4jDSL.Describe("describe", () -> {
                        Context("does something", () -> It("it", invokeTest1));
                        Context("does something similar", () -> It("it", invokeTest2));
                    });
                    Ginkgo4jDSL.unsetVisitor(builder);
                });

                It("should capture the correct InvokeTest", () ->
                        assertThat(builder.getExecutableChain().getSpec(), is(invokeTest2)));
            });

            Context("when a Describe has two similar Its", () -> {

                BeforeEach(() -> {
                    builder = new ExecutableChainBuilder("describe.does something similar");
                    Ginkgo4jDSL.setVisitor(builder);
                    Ginkgo4jDSL.Describe("describe", () -> {
                        It("does something", invokeTest1);
                        It("does something similar", invokeTest2);
                    });
                    Ginkgo4jDSL.unsetVisitor(builder);
                });

                It("should capture the correct InvokeTest", () ->
                        assertThat(builder.getExecutableChain().getSpec(), is(invokeTest2)));
            });

            Context("when a top-level Context has a Describe block", () -> {

                BeforeEach(() -> {
                    builder = new ExecutableChainBuilder("context.has a describe.it");
                    Ginkgo4jDSL.setVisitor(builder);
                    Ginkgo4jDSL.Context("context", () ->
                            Describe("has a describe", () -> It("it", invokeTest1)));
                    Ginkgo4jDSL.unsetVisitor(builder);
                });

                It("should capture the correct InvokeTest", () -> {
                    assertThat(builder.getExecutableChain().getContext().size(), is(2));
                    assertThat(builder.getExecutableChain().getContext().get(0), instanceOf(Context.class));
                    assertThat(builder.getExecutableChain().getContext().get(0).getId(), is("context"));
                    assertThat(builder.getExecutableChain().getContext().get(1), instanceOf(Describe.class));
                    assertThat(builder.getExecutableChain().getContext().get(1).getId(), is("has a describe"));
                    assertThat(builder.getExecutableChain().getSpec(), is(invokeTest1));
                });
            });

            Context("when a Describe has a Context has a Describe block", () -> {

                BeforeEach(() -> {
                    builder = new ExecutableChainBuilder("a describe.has a context.has a describe.it");
                    Ginkgo4jDSL.setVisitor(builder);
                    Ginkgo4jDSL.Describe("a describe", () ->
                            Ginkgo4jDSL.Context("has a context", () ->
                                    Describe("has a describe", () -> It("it", invokeTest1))));
                    Ginkgo4jDSL.unsetVisitor(builder);
                });

                It("should capture the correct InvokeTest", () -> {
                    assertThat(builder.getExecutableChain().getContext().size(), is(3));
                    assertThat(builder.getExecutableChain().getContext().get(0), instanceOf(Describe.class));
                    assertThat(builder.getExecutableChain().getContext().get(0).getId(), is("a describe"));
                    assertThat(builder.getExecutableChain().getContext().get(1), instanceOf(Context.class));
                    assertThat(builder.getExecutableChain().getContext().get(1).getId(), is("has a context"));
                    assertThat(builder.getExecutableChain().getContext().get(2), instanceOf(Describe.class));
                    assertThat(builder.getExecutableChain().getContext().get(2).getId(), is("has a describe"));
                    assertThat(builder.getExecutableChain().getSpec(), is(invokeTest1));
                });
            });

            Context("when block descriptions contain regex special characters", () -> {

                Context("when the describe contains regex special characters", () -> {
                    BeforeEach(() -> {
                        builder = new ExecutableChainBuilder("/{special}/{characters}/.context.it");
                        Ginkgo4jDSL.setVisitor(builder);
                        Ginkgo4jDSL.Describe("/{special}/{characters}/", () ->
                                Context("context", () -> It("it", invokeTest1)));
                        Ginkgo4jDSL.unsetVisitor(builder);
                    });

                    It("should capture the InvokeTest", () ->
                            assertThat(builder.getExecutableChain().getSpec(), is(invokeTest1)));
                });

                Context("when the context contains regex special characters", () -> {
                    BeforeEach(() -> {
                        builder = new ExecutableChainBuilder("describe./{special}/{characters}/.it");
                        Ginkgo4jDSL.setVisitor(builder);
                        Ginkgo4jDSL.Describe("describe", () ->
                                Context("/{special}/{characters}/", () -> It("it", invokeTest1)));
                        Ginkgo4jDSL.unsetVisitor(builder);
                    });

                    It("should capture the InvokeTest", () ->
                            assertThat(builder.getExecutableChain().getSpec(), is(invokeTest1)));
                });

                Context("when the it contains regex special characters", () -> {
                    BeforeEach(() -> {
                        builder = new ExecutableChainBuilder("describe.context./{special}/{characters}/");
                        Ginkgo4jDSL.setVisitor(builder);
                        Ginkgo4jDSL.Describe("describe", () ->
                                Context("context", () ->
                                        It("/{special}/{characters}/", invokeTest1)));
                        Ginkgo4jDSL.unsetVisitor(builder);
                    });

                    It("should capture the InvokeTest", () ->
                            assertThat(builder.getExecutableChain().getSpec(), is(invokeTest1)));
                });

                Context("when a test class with empty labels is invoked", () -> {

                    BeforeEach(() -> {
                        builder = new ExecutableChainBuilder("_EMPTY_._EMPTY_._EMPTY_");
                        Ginkgo4jDSL.setVisitor(builder);
                        Ginkgo4jDSL.Describe("", () ->
                                Context("", () ->
                                        It("", () -> {
                                        })));
                        Ginkgo4jDSL.unsetVisitor(builder);
                    });

                    It("should capture the test structure", () -> {
                        assertThat(builder.getExecutableChain().getContext().size(), is(2));
                        assertThat(builder.getExecutableChain().getSpec(), is(not(nullValue())));
                    });
                });
            });
        });
    }

    public static class InvokeTest implements ExecutableBlock {
        @Override
        public void invoke() {
        }
    }
}
