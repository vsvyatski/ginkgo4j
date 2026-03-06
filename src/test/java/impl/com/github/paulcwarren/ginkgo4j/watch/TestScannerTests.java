package impl.com.github.paulcwarren.ginkgo4j.watch;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.runner.RunWith;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;

@RunWith(Ginkgo4jRunner.class)
public class TestScannerTests {

    File testDir;

    {
        Describe("TestScanner", () -> Context("#scan", () -> {
                    BeforeEach(() -> testDir = mkTestDir());
                    AfterEach(() -> FileUtils.deleteDirectory(testDir));
                    Context("when the directory contains *Test.java tests", () -> {
                        BeforeEach(() -> {
                            new File(testDir, "MyClassTest.java").createNewFile();
                            new File(testDir, "HisClassTest.java").createNewFile();
                        });
                        It("should return 2 tests", () -> {
                            List<File> tests = TestScanner.scan(testDir);
                            assertThat(tests, is(not(nullValue())));
                            assertThat(tests.size(), is(2));
                        });
                    });
                    Context("when the directory contains *Tests.java tests", () -> {
                        BeforeEach(() -> {
                            new File(testDir, "MyClassTests.java").createNewFile();
                            new File(testDir, "HisClassTests.java").createNewFile();
                        });
                        It("should return 2 tests", () -> {
                            List<File> tests = TestScanner.scan(testDir);
                            assertThat(tests, is(not(nullValue())));
                            assertThat(tests.size(), is(2));
                        });
                    });
                    Context("when the directory contains non-test files", () -> {
                        BeforeEach(() -> new File(testDir, "MyClass.java").createNewFile());
                        It("should return an empty list", () -> {
                            List<File> tests = TestScanner.scan(testDir);
                            assertThat(tests, is(not(nullValue())));
                            assertThat(tests.size(), is(0));
                        });
                    });
                    Context("when the directory contains a directory", () -> {
                        BeforeEach(() -> new File(testDir, "MyDirectoryTest.java").mkdirs());
                        It("should return an empty list", () -> {
                            List<File> tests = TestScanner.scan(testDir);
                            assertThat(tests, is(not(nullValue())));
                            assertThat(tests.size(), is(0));
                        });
                    });
                })
        );
    }

    static File mkTestDir() {
        File testdir;
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = "testdir-" + System.currentTimeMillis() + "-";

        for (int counter = 0; counter < Integer.MAX_VALUE; counter++) {
            testdir = new File(baseDir, baseName + counter);
            if (testdir.mkdir()) {
                return testdir;
            }
        }
        return baseDir;
    }
}
