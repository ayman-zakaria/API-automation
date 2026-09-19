package Listeners;

import BasesAndConfig.LogUtil;
import org.testng.IExecutionListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;

/* Clears out stale Allure results
 * before a fresh run and logs each test's outcome - request/response detail on
 * failure is already captured by AllureRestAssured on the service calls themselves,
 * so there's no need to duplicate that here the way the GUI project needs screenshots.
 */
public class TestListener implements IExecutionListener, ITestListener {

    private static final String ALLURE_RESULTS_DIR = "target/allure-results";

    @Override
    public void onExecutionStart() {
        clearDirectory(ALLURE_RESULTS_DIR);
        LogUtil.info("Test execution starting");
    }

    @Override
    public void onExecutionFinish() {
        LogUtil.info("Test execution finished");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LogUtil.info("PASSED: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LogUtil.error("FAILED: " + result.getName() + " - " + result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LogUtil.warn("SKIPPED: " + result.getName());
    }

    private void clearDirectory(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }
}
