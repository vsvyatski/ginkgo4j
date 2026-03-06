package impl.com.github.paulcwarren.ginkgo4j.watch;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public final class TestScanner {

    private TestScanner() {
    }

    public static List<File> scan(File directory) {
        FileFilter fileFilter = WildcardFileFilter.builder()
                .setWildcards("*Test.java", "*Tests.java").setIoCase(IOCase.INSENSITIVE).get();
        File[] files = directory.listFiles(fileFilter);
        List<File> tests = new ArrayList<>();
        assert files != null;
        for (File f : files) {
            if (f.isFile()) {
                tests.add(f);
            }
        }
        return tests;
    }
}
