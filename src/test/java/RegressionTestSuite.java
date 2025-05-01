import courgette.api.CourgetteOptions;
import courgette.api.CourgetteRunLevel;
import courgette.api.junit.Courgette;
import cucumber.api.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Courgette.class)
@CourgetteOptions(
        threads = 10,
        runLevel = CourgetteRunLevel.SCENARIO,
        rerunFailedScenarios = true,
        rerunAttempts = 1,
        showTestOutput = true,
        reportTargetDir = "target/",
        cucumberOptions = @CucumberOptions(
                features = "src/test/resources/features",
                glue = "api.test",
                plugin = {
                        "pretty",
                        "json:reports/cucumber-report/cucumber.json",
                        "html:reports/cucumber-report/cucumber.html",
                        "junit:reports/cucumber-report/cucumber.xml"},
                strict = true,
                monochrome = true
        )
)

public class RegressionTestSuite {
}
