package impl.com.github.paulcwarren.ginkgo4j.builder;

import com.github.paulcwarren.ginkgo4j.ExecutableBlock;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(Ginkgo4jRunner.class)
public class TestClassWalkerTests {

    private TestWalker walker;
    private TestVisitor visitor;
    private TestVisitor visitorSpy;

    {
        Describe("TestWalker", () -> Context("when walking a test class", () -> {
            BeforeEach(() -> {
                visitor = new TestVisitorImpl();
                visitorSpy = spy(visitor);
                walker = new TestWalker(TestClass.class);
                walker.walk(visitorSpy);
            });

            It("should visit all nodes in the tree", () -> {
                InOrder order = inOrder(visitorSpy);
                order.verify(visitorSpy).describe(anyString(), any(), eq(false));
                order.verify(visitorSpy).justBeforeEach(any());
                order.verify(visitorSpy).context(anyString(), any(), eq(false));
                order.verify(visitorSpy).beforeEach(any());
                order.verify(visitorSpy).it(anyString(), any(), eq(false));
                order.verify(visitorSpy).afterEach(any());
                order.verify(visitorSpy).test(any());
                verifyNoMoreInteractions(visitorSpy);
            });
        }));
    }

    static class TestClass {
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
        }}

    static class TestVisitorImpl implements TestVisitor {
        @Override
        public void describe(String text, ExecutableBlock block, boolean isFocused) {
            try {
                block.invoke();
            } catch (Throwable ignored) {
            }
        }

        @Override
        public void context(String text, ExecutableBlock block, boolean isFocused) {
            try {
                block.invoke();
            } catch (Throwable ignored) {
            }
        }

        @Override
        public void beforeEach(ExecutableBlock block) {
        }

        @Override
        public void justBeforeEach(ExecutableBlock block) {
        }

        @Override
        public void it(String text, ExecutableBlock block, boolean isFocused) {
        }

        @Override
        public void afterEach(ExecutableBlock block) {
        }

        @Override
        public void test(Object test) {
        }
    }
}
