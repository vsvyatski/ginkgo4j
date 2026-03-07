package com.github.paulcwarren.ginkgo4j;

import impl.com.github.paulcwarren.ginkgo4j.chains.ExecutableChain;
import impl.com.github.paulcwarren.ginkgo4j.runner.Runner;
import impl.com.github.paulcwarren.ginkgo4j.runner.SpecRunner;
import impl.com.github.paulcwarren.ginkgo4j.runner.SpecSkipper;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(Ginkgo4jRunner.class)
@Ginkgo4jConfiguration(threads = 1)
public class Ginkgo4jRunnerTests {

    private Ginkgo4jRunner runner;

    //mocks
    private RunNotifier notifier;

    {
        Describe("#run", () -> {
            JustBeforeEach(() -> {
                try {
                    runner.run(notifier);
                } catch (Throwable ignored) {
                }
            });
            BeforeEach(() -> notifier = mock(RunNotifier.class));
            Context("when called with a passing test", () -> {
                BeforeEach(() -> runner = new Ginkgo4jRunner(JustTest.class));
                It("should execute successfully", () -> {
                    verify(notifier, times(3)).fireTestStarted(any());
                    verify(notifier, times(3)).fireTestFinished(any());
                });
            });
            Context("when a test that throws an NPE", () -> {
                Context("from a BeforeEach block", () -> {
                    BeforeEach(() -> {
                        NPEThrowingTestClass.describe = "";
                        NPEThrowingTestClass.context = "";
                        NPEThrowingTestClass.jbe = "";
                        NPEThrowingTestClass.be = null;
                        NPEThrowingTestClass.ae = "";
                        runner = new Ginkgo4jRunner(NPEThrowingTestClass.class);
                    });
                    It("should report failure to junit's notifier", () -> {
                        verify(notifier, times(1)).fireTestStarted(any());

                        ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);
                        verify(notifier, times(1)).fireTestFailure(failureCaptor.capture());
                        assertThat(failureCaptor.getValue().getException().getStackTrace()[0].getLineNumber(), is(20));

                        verify(notifier, times(1)).fireTestFinished(any());
                    });
                });
                Context("from a JustBeforeEach block", () -> {
                    BeforeEach(() -> {
                        NPEThrowingTestClass.describe = "";
                        NPEThrowingTestClass.context = "";
                        NPEThrowingTestClass.jbe = null;
                        NPEThrowingTestClass.be = "";
                        NPEThrowingTestClass.ae = "";
                        runner = new Ginkgo4jRunner(NPEThrowingTestClass.class);
                    });
                    It("should report failure to junit's notifier", () -> {
                        verify(notifier, times(1)).fireTestStarted(any());

                        ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);
                        verify(notifier, times(1)).fireTestFailure(failureCaptor.capture());
                        assertThat(failureCaptor.getValue().getException().getStackTrace()[0].getLineNumber(), is(23));

                        verify(notifier, times(1)).fireTestFinished(any());
                    });
                });
                Context("from an AfterEach block", () -> {
                    BeforeEach(() -> {
                        NPEThrowingTestClass.describe = "";
                        NPEThrowingTestClass.context = "";
                        NPEThrowingTestClass.jbe = "";
                        NPEThrowingTestClass.be = "";
                        NPEThrowingTestClass.ae = null;
                        runner = new Ginkgo4jRunner(NPEThrowingTestClass.class);
                    });
                    It("should report failure to junit's notifier", () -> {
                        verify(notifier, times(1)).fireTestStarted(any());

                        ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);
                        verify(notifier, times(1)).fireTestFailure(failureCaptor.capture());
                        assertThat(failureCaptor.getValue().getException().getStackTrace()[0].getLineNumber(), is(30));

                        verify(notifier, times(1)).fireTestFinished(any());
                    });
                });
            });
            Context("when a test calls method that declare 'throws Throwable'", () -> {
                BeforeEach(() -> runner = new Ginkgo4jRunner(DeclaresThrowableTest.class));
                It("should compile", () -> {
                    // see DeclaresThrowableTest definition below
                    assertThat(true, is(true));
                });
            });
        });

        Describe("#calculateExecutionChains", () -> {
            Context("when called with no focussed specs", () -> {
                BeforeEach(() -> runner = new Ginkgo4jRunner(JustTest.class));
                It("should return a full chain with no focus", () -> {
                    List<ExecutableChain> chains = Ginkgo4jRunner.calculateExecutionChains(JustTest.class);
                    assertThat(chains, is(not(nullValue())));
                    assertThat(chains.size(), is(3));
                    assertThat(getExecutableChain("test1", chains).isFocused(), is(false));
                    assertThat(getExecutableChain("test2", chains).isFocused(), is(false));
                    assertThat(getExecutableChain("test3", chains).isFocused(), is(false));
                });
            });
            Context("when called with at least one focussed spec", () -> {
                BeforeEach(() -> runner = new Ginkgo4jRunner(FittedTest.class));
                It("should return a full chain with just the focused spec", () -> {
                    List<ExecutableChain> chains = Ginkgo4jRunner.calculateExecutionChains(FittedTest.class);
                    assertThat(chains, is(not(nullValue())));
                    assertThat(chains.size(), is(3));
                    assertThat(getExecutableChain("test1", chains).isFocused(), is(false));
                    assertThat(getExecutableChain("test2", chains).isFocused(), is(true));
                    assertThat(getExecutableChain("test3", chains).isFocused(), is(false));
                });
            });
        });

        Describe("#calculateWorkerThreads", () -> {
            Context("when called with no focussed specs", () -> {
                BeforeEach(() -> {
                    runner = new Ginkgo4jRunner(JustTest.class);
                    runner.getDescription();
                });
                It("should return all SpecRunners", () -> {
                    List<ExecutableChain> chains = Ginkgo4jRunner.calculateExecutionChains(JustTest.class);
                    List<Runner> workers = Ginkgo4jRunner.calculateWorkerThreads(chains);

                    assertThat(workers, is(not(nullValue())));
                    assertThat(workers.size(), is(3));
                    assertThat(workers, containsInAnyOrder(is(instanceOf(SpecRunner.class)), is(instanceOf(SpecRunner.class)), is(instanceOf(SpecRunner.class))));
                });
            });
            Context("when called with at least one focused spec", () -> {
                BeforeEach(() -> {
                    runner = new Ginkgo4jRunner(FittedTest.class);
                    runner.getDescription();
                });
                It("should return one SpecRunnerThread and two SpecSkipperThreads", () -> {
                    List<ExecutableChain> chains = Ginkgo4jRunner.calculateExecutionChains(FittedTest.class);
                    List<Runner> workers = Ginkgo4jRunner.calculateWorkerThreads(chains);

                    assertThat(workers, is(not(nullValue())));
                    assertThat(workers.size(), is(3));
                    assertThat(workers, containsInAnyOrder(is(instanceOf(SpecSkipper.class)), is(instanceOf(SpecRunner.class)), is(instanceOf(SpecSkipper.class))));
                });
            });
            Context("when called with at a focused context", () -> {
                BeforeEach(() -> {
                    runner = new Ginkgo4jRunner(FittedContextTest.class);
                    runner.getDescription();
                });
                It("should return a SpecRunnerThread for all tests in the fitted context", () -> {
                    List<ExecutableChain> chains = Ginkgo4jRunner.calculateExecutionChains(FittedContextTest.class);
                    List<Runner> workers = Ginkgo4jRunner.calculateWorkerThreads(chains);

                    assertThat(workers, is(not(nullValue())));
                    assertThat(workers.size(), is(5));
                    assertThat(runnerThreads(workers), is(2));
                });
                It("should return SpecSkipperThreads for everything else", () -> {
                    List<ExecutableChain> chains = Ginkgo4jRunner.calculateExecutionChains(FittedContextTest.class);
                    List<Runner> workers = Ginkgo4jRunner.calculateWorkerThreads(chains);

                    assertThat(workers, is(not(nullValue())));
                    assertThat(workers.size(), is(5));
                    assertThat(skipperThreads(workers), is(3));
                });
            });
            Context("when called with at a focused describe", () -> {
                BeforeEach(() -> {
                    runner = new Ginkgo4jRunner(FittedDescribeTest.class);
                    runner.getDescription();
                });
                It("should return a SpecRunnerThread for all tests in the fitted describe", () -> {
                    List<ExecutableChain> chains = Ginkgo4jRunner.calculateExecutionChains(FittedDescribeTest.class);
                    List<Runner> workers = Ginkgo4jRunner.calculateWorkerThreads(chains);

                    assertThat(workers, is(not(nullValue())));
                    assertThat(workers.size(), is(7));
                    assertThat(runnerThreads(workers), is(2));
                });
                It("should return SpecSkipperThreads for everything else", () -> {
                    List<ExecutableChain> chains = Ginkgo4jRunner.calculateExecutionChains(FittedDescribeTest.class);
                    List<Runner> workers = Ginkgo4jRunner.calculateWorkerThreads(chains);

                    assertThat(workers, is(not(nullValue())));
                    assertThat(workers.size(), is(7));
                    assertThat(skipperThreads(workers), is(5));
                });
            });
            Context("when called with an empty describe label", () -> {
                BeforeEach(() -> {
                    runner = new Ginkgo4jRunner(EmptyDescribeTest.class);
                    runner.getDescription();
                });
                It("should return a SpecRunnerThread for all tests in the fitted describe", () -> {
                    List<ExecutableChain> chains = Ginkgo4jRunner.calculateExecutionChains(EmptyDescribeTest.class);
                    List<Runner> workers = Ginkgo4jRunner.calculateWorkerThreads(chains);

                    assertThat(workers, is(not(nullValue())));
                    assertThat(workers.size(), is(1));
                    assertThat(runnerThreads(workers), is(1));
                });
            });
        });
    }

    public static void declaresThrowable() throws Throwable {
    }

    private ExecutableChain getExecutableChain(String name, List<ExecutableChain> chains) {
        for (ExecutableChain chain : chains) {
            if (chain.getId().equals(name)) {
                return chain;
            }
        }
        throw new IllegalArgumentException();
    }

    private Object runnerThreads(List<Runner> workers) {
        int i = 0;
        for (Runner thread : workers) {
            if (thread instanceof SpecRunner) {
                i++;
            }
        }
        return i;
    }

    private Object skipperThreads(List<Runner> workers) {
        int i = 0;
        for (Runner thread : workers) {
            if (thread instanceof SpecSkipper) {
                i++;
            }
        }
        return i;
    }

    public static class JustTest {
        {
            It("test1", () -> {
            });
            It("test2", () -> {
            });
            It("test3", () -> {
            });
        }
    }

    public static class FittedTest {
        {
            It("test1", () -> {
            });
            FIt("test2", () -> {
            });
            It("test3", () -> {
            });
        }
    }

    public static class FittedContextTest {
        {
            It("test1", () -> {
            });
            It("test2", () -> {
            });
            It("test3", () -> {
            });
            FContext("a context", () -> {
                It("test4", () -> {
                });
                It("test5", () -> {
                });
            });
        }
    }

    public static class FittedDescribeTest {
        {
            It("test1", () -> {
            });
            It("test2", () -> {
            });
            FDescribe("a describe", () -> {
                It("test6", () -> {
                });
                It("test7", () -> {
                });
            });
            It("test3", () -> {
            });
            Context("a context", () -> {
                It("test4", () -> {
                });
                It("test5", () -> {
                });
            });
        }
    }

    public static class EmptyDescribeTest {
        {
            Describe("", () -> It("stuff", () -> {
            }));
        }
    }

    /*
     * For compilation checks ONLY
     */
    public static class DeclaresThrowableTest {
        {
            Describe("describe", () -> {
                declaresThrowable();
                Context("context", () -> {
                    declaresThrowable();
                    JustBeforeEach(Ginkgo4jRunnerTests::declaresThrowable);
                    BeforeEach(Ginkgo4jRunnerTests::declaresThrowable);
                    It("it", Ginkgo4jRunnerTests::declaresThrowable);
                    AfterEach(Ginkgo4jRunnerTests::declaresThrowable);
                });
            });
            FDescribe("fdescribe", () -> {
                declaresThrowable();
                FContext("fcontext", () -> {
                    declaresThrowable();
                    FIt("fit", Ginkgo4jRunnerTests::declaresThrowable);
                });
            });
        }
    }
}
