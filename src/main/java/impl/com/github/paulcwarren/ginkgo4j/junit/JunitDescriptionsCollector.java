package impl.com.github.paulcwarren.ginkgo4j.junit;

import com.github.paulcwarren.ginkgo4j.ExecutableBlock;
import impl.com.github.paulcwarren.ginkgo4j.IdBuilder;
import impl.com.github.paulcwarren.ginkgo4j.TitleBuilder;
import impl.com.github.paulcwarren.ginkgo4j.builder.TestVisitor;
import org.junit.runner.Description;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class JunitDescriptionsCollector implements TestVisitor {

	private final Map<String, Description> descriptions = new HashMap<>();
	private final Stack<String> idContext = new Stack<>();
	private final Description description;
	private final Class<?> clazz;

	public JunitDescriptionsCollector(Class<?> clazz, Description description) {
		this.clazz = clazz;
		this.description = description;
	}

	public Map<String,Description> getDescriptions() {
		return descriptions;
	}
	
	public void describe(String text, ExecutableBlock block, boolean isFocused) {
		text = TitleBuilder.title(text);
		String id = IdBuilder.id(text);
		idContext.push(id);
		try {
			block.invoke();
		} catch (Throwable ignored) {}
		finally {
			idContext.pop();
		}
	}
	
	public void context(String text, ExecutableBlock block, boolean isFocused) {
		text = TitleBuilder.title(text);
		String id = IdBuilder.id(text);
		idContext.push(id);
		try {
			block.invoke();
		} catch (Throwable ignored) {}
		finally {
			idContext.pop();
		}
	}

	public void beforeEach(ExecutableBlock block) {
	}

	public void justBeforeEach(ExecutableBlock block) {
	}

	public void it(String text, ExecutableBlock block, boolean isFocused) {
		text = TitleBuilder.title(text);
		String id = IdBuilder.fqId(text, idContext);
		Description itDesc = Description.createTestDescription(this.clazz.getName(), id, id);
		description.addChild(itDesc);
		descriptions.put(id, itDesc);
	}
	
	public void afterEach(ExecutableBlock block) {
	}

	@Override
	public void test(Object test) {
	}
}
