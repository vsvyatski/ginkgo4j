package impl.com.github.paulcwarren.ginkgo4j;

public class TitleBuilder {

    private static final String EMPTY_TEXT = " ";

    private TitleBuilder() {
    }

    public static String title(String title) {
        if (title == null || title.isBlank()) {
            return EMPTY_TEXT;
        }
        return title;
    }
}
