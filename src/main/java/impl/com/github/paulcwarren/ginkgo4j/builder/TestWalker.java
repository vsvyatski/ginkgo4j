package impl.com.github.paulcwarren.ginkgo4j.builder;

import com.github.paulcwarren.ginkgo4j.ExecutableBlock;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL;

import java.lang.reflect.InvocationTargetException;

public class TestWalker implements TestVisitor {

    private final Class<?> testClass;
    private TestVisitor[] visitors;

    public TestWalker(Class<?> testClass) {
        this.testClass = testClass;
    }

    public void walk(TestVisitor... visitors) {
        this.visitors = visitors;
        try {
            Ginkgo4jDSL.setVisitor(this);
            try {
                Object test = testClass.getDeclaredConstructor().newInstance();
                test(test);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                e.printStackTrace(System.err);
            }
        } finally {
            Ginkgo4jDSL.unsetVisitor(this);
        }
    }

    @Override
    public void describe(String text, ExecutableBlock block, boolean isFocused) throws Throwable {
        for (TestVisitor visitor : visitors) {
            visitor.describe(text, block, isFocused);
        }
    }

    @Override
    public void context(String text, ExecutableBlock block, boolean isFocused) throws Throwable {
        for (TestVisitor visitor : visitors) {
            visitor.context(text, block, isFocused);
        }
    }

    @Override
    public void beforeEach(ExecutableBlock block) throws Throwable {
        for (TestVisitor visitor : visitors) {
            visitor.beforeEach(block);
        }
    }

    @Override
    public void justBeforeEach(ExecutableBlock block) throws Throwable {
        for (TestVisitor visitor : visitors) {
            visitor.justBeforeEach(block);
        }
    }

    @Override
    public void it(String text, ExecutableBlock block, boolean isFocused) throws Throwable {
        for (TestVisitor visitor : visitors) {
            visitor.it(text, block, isFocused);
        }
    }

    @Override
    public void afterEach(ExecutableBlock block) throws Throwable {
        for (TestVisitor visitor : visitors) {
            visitor.afterEach(block);
        }
    }

    @Override
    public void test(Object test) {
        for (TestVisitor visitor : visitors) {
            visitor.test(test);
        }
    }
}
