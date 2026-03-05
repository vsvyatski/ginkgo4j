package impl.com.github.paulcwarren.ginkgo4j.chains;

import com.github.paulcwarren.ginkgo4j.ExecutableBlock;
import impl.com.github.paulcwarren.ginkgo4j.IdBuilder;
import impl.com.github.paulcwarren.ginkgo4j.Spec;
import impl.com.github.paulcwarren.ginkgo4j.builder.TestVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SpecsCollector implements TestVisitor {
	
	private final Stack<String> context = new Stack<>();
	private final List<Spec> specs = new ArrayList<>();

	public List<Spec> getSpecs() {
		return specs;
	}
	
	public void describe(String text, ExecutableBlock block, boolean isFocused) throws Throwable {
		text = IdBuilder.id(text);
		
		context.push(text);
		try {
			block.invoke();
		} finally {
			context.pop();
		}
	}
	
	public void context(String text, ExecutableBlock block, boolean isFocused) throws Throwable {
		text = IdBuilder.id(text);
		
		context.push(text);
		try {
			block.invoke();
		} finally {
			context.pop();
		}
	}
	
	public void beforeEach(ExecutableBlock block) {
	}

	public void justBeforeEach(ExecutableBlock block) {
	}

	public void it(String text, ExecutableBlock block, boolean isFocused) {
		String fqId = IdBuilder.fqId(text, context);
		
		Spec spec = new Spec(fqId, block, isFocused);
		specs.add(spec);
	}
	
	public void afterEach(ExecutableBlock block) {
	}

	@Override
	public void test(Object test) {
	}
}
