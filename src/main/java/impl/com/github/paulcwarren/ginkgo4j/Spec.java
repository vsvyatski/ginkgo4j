package impl.com.github.paulcwarren.ginkgo4j;

import com.github.paulcwarren.ginkgo4j.ExecutableBlock;

public class Spec {

    private final String id;

    private final boolean focused;

    private final String description;
    private final ExecutableBlock block;

    public Spec(String description, ExecutableBlock block, boolean focused) {
        this.id = description;
        this.description = description;
        this.block = block;
        this.focused = focused;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFocused() {
        return focused;
    }

    public ExecutableBlock getExecutableBlock() {
        return block;
    }

    @Override
    public String toString() {
        return "Spec [" + id + "]";
    }
}
