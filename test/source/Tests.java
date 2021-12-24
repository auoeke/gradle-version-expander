import java.io.File;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
class Tests {
    @Test
    void test() {
        runner().withDebug(true).build();
    }

    private static GradleRunner runner() {
        return GradleRunner.create()
            .withProjectDir(new File("test/project"))
            .withPluginClasspath()
            .withArguments("-s", "clean", "publishToMavenLocal")
            .forwardOutput();
    }
}
