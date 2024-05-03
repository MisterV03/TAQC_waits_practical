import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class DevexpressTableTest {
	private String defaultURL = "https://devexpress.github.io/devextreme-reactive/react/grid/docs/guides/filtering/";
	private static WebDriver driver;
	private static WebDriverWait wait;

	@BeforeAll
	static void setUpBeforeClass() {
		System.setProperty("webdriver.chrome.driver", "D:\\WebDrivers\\chromedriver-win64\\chromedriver.exe");
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		driver.manage().window().maximize();
	}

	@BeforeEach
	void setUp() {
		driver.get(defaultURL);
	}

	@ParameterizedTest
	@ValueSource(strings = { "Las Vegas", "London" })
	void test(String in) {
		int targetColumnIndex = 2;
		int columnNumber = 1;
		boolean isTestFailed = true;
		List<WebElement> frames = driver.findElements(By.cssSelector("iframe"));
		// перемикаємося на першу вкладену сторінку та шукаємо таблицю
		driver.switchTo().frame(frames.get(0));
		WebElement targetTable = wait.until(
				ExpectedConditions.presenceOfElementLocated(By.cssSelector(".MuiTable-root.Table-table.css-oyg2rb")));
		new Actions(driver).scrollToElement(targetTable).perform();
		// отримуємо перший рядок таблиці
		List<WebElement> headColumns = targetTable
				.findElements(By.cssSelector(".MuiTableRow-root.MuiTableRow-head.css-e42jjo:first-child th"));
		columnNumber = headColumns.size();
		// Шукаємо стовпець City
		for (int i = 0; i < columnNumber; i++) {
			if (headColumns.get(i).findElement(By.cssSelector(
					".MuiTableRow-root.MuiTableRow-head.css-e42jjo:first-child th span.Title-title.css-1mrv5kl"))
					.getText().equals("City")) {
				targetColumnIndex = i;
			}
		}
		List<WebElement> inputFields = driver
				.findElements(By.cssSelector("input.MuiInputBase-input.MuiInput-input.Editor-input.css-mnn31"));
		WebElement targetField = inputFields.get(targetColumnIndex);
		targetField.click();
		targetField.clear();
		targetField.sendKeys("L");
		List<WebElement> tableCells = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
				By.cssSelector(".MuiTableBody-root .MuiTableRow-root .TableCell-cell")));
		for (int i = targetColumnIndex; i < tableCells.size(); i += columnNumber) {// рухаємося по стовпцю
			if (tableCells.get(i).getText().equals(in)) {
				isTestFailed = false;
			}
		}
		Assertions.assertFalse(isTestFailed);
	}

	@AfterAll
	static void tearDownAfterClass() {
		if (driver != null) {
			driver.quit();
		}
	}

}
