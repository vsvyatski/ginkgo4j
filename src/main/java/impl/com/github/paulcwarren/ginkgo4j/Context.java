package impl.com.github.paulcwarren.ginkgo4j;

import com.github.paulcwarren.ginkgo4j.ExecutableBlock;

import java.util.ArrayList;
import java.util.List;

public class Context {

    private final String id;

    private final List<ExecutableBlock> be = new ArrayList<>();
    private final List<ExecutableBlock> jbe = new ArrayList<>();
    private final List<ExecutableBlock> ae = new ArrayList<>();

    public Context(String id) {
        this.id = id;
    }

    public Object getId() {
        return this.id;
    }

    public void addBeforeEach(ExecutableBlock be) {
        this.be.add(be);
    }

    public List<ExecutableBlock> getBeforeEach() {
        return this.be;
    }

    public List<ExecutableBlock> getJustBeforeEach() {
        return this.jbe;
    }

    public void setJustBeforeEach(ExecutableBlock jbe) {
        this.jbe.add(jbe);
    }

    public List<ExecutableBlock> getAfterEach() {
        return this.ae;
    }

    public void setAfterEach(ExecutableBlock a) {
        this.ae.add(a);
    }
}
