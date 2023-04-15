package com.bot.service;

import com.bot.model.PostMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Map;
import java.util.logging.Level;

@Service
@Log
public class JobHandler {

	private static final String ACTION_KEY = "postMessageKey";
	final ObjectMapper objectMapper = new ObjectMapper();

	public void sendAction(WebDriver driver, String message, String tab) {
		try {

			var postMessage = convertMessage(message);
			if (!PostMessage.Status.DONE.equals(postMessage.getMessageStatus())
					&& !PostMessage.Status.FAIL.equals(postMessage.getMessageStatus())) {
				System.out.println("có acction mới nè");
				log.log(Level.INFO, "JobHandler >> sendAction >> new action >> {0}", message);
				// after done remove set message in localStorage
				boolean runSelenium = doAction(driver, postMessage);
                  
				if(runSelenium) {
					postMessage.setMessageStatus(PostMessage.Status.DONE);
				}else {
					postMessage.setMessageStatus(PostMessage.Status.FAIL);
				}
				
				var json = objectMapper.writeValueAsString(postMessage);
				var js = (JavascriptExecutor) driver;

				js.executeScript(
						"document.querySelector('body').dispatchEvent(new CustomEvent('CLIENT_OUT', { detail: arguments[0] }));",
						json);

				js.executeScript("window.localStorage.setItem(arguments[0], arguments[1]);", ACTION_KEY + tab, json);
			}

		} catch (Exception e) {
			log.log(Level.WARNING, "JobHandler >> sendAction >> Exception:", e);
		}

	}

	private boolean doAction(WebDriver driver, PostMessage message)   {
		boolean checkaction = false;
		 
		String key = message.getActionType();
		Map<String, Object> data = message.getData();
		String selector = (String) data.get("selector");
		String value = (String) data.get("value");
         System.out.println("da lam action "+key);
		switch (key) {
		case "SEND_KEY":
			checkaction = sendKeysToElementByCssSelector(driver, selector, value);
			break;
		case "CLICK":
			checkaction = clickElementByCssSelector(driver, selector);
			break;
		case "PASTE":
			checkaction = paste(driver, selector, value);
			break;
		case "FOCUS":
			checkaction = focusElementByCssSelector(driver, selector);
			break;
		default:
			System.out.println("ko có acction nay");
			break;
		}
		
		   System.out.println("da lam action "+checkaction);
		
		return checkaction;

		// TODO Auto-generated method stub

	}

	public boolean pressEnter(WebDriver driver, String cssSelector) {
		try {
			WebElement element = new WebDriverWait(driver, Duration.ofMinutes(1))
					.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));
			element.sendKeys(Keys.RETURN);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	public boolean paste(WebDriver driver, String cssSelector, String msg) {
		try {
			// Get the target element
			clickElementByCssSelector(driver, cssSelector);

			// Copy the source text to the clipboard
			StringSelection stringSelection = new StringSelection(msg);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);

			// Focus on the target element and paste the text
			focusElementByCssSelector(driver, cssSelector);
			try {
				Robot robot = new Robot();
				robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_V);
				robot.keyRelease(KeyEvent.VK_V);
				robot.keyRelease(KeyEvent.VK_CONTROL);
			} catch (AWTException e) {
				e.printStackTrace();
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	public boolean sendKeysToElementByCssSelector(WebDriver driver, String cssSelector, String msg) {
		 System.out.println("Wait for the element to be visible "+cssSelector);
		 try { 
		// Wait for the element to be visible
		WebElement element = new WebDriverWait(driver, Duration.ofMinutes(1))
				.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector)));

		// Send the keys to the element
		if (element != null) {
			element.sendKeys(msg);
			return true;
		} else {
			return false;
		}
		 } catch (Exception e) {
				return false;
			}
	}

	public boolean clickElementByCssSelector(WebDriver driver, String cssSelector) {
		 System.out.println("Wait for the element to be visible "+cssSelector);
		try {
		 // Wait for the element to be visible
		WebElement element = new WebDriverWait(driver, Duration.ofMinutes(1))
				.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector)));

		// Send the keys to the element
		if (element != null) {
			element.click();
			return true;
		} else {
			return false;
		}
	 } catch (Exception e) {
			return false;
		}
	}

	public boolean focusElementByCssSelector(WebDriver driver, String cssSelector) {
		 System.out.println("Wait for the element to be visible "+cssSelector);
		// Wait for the element to be present
		try {

			WebElement element = new WebDriverWait(driver, Duration.ofMinutes(1))
					.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));

			// Scroll the element into view
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

			// Set the focus on the element
			((JavascriptExecutor) driver).executeScript("arguments[0].focus();", element);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	public PostMessage convertMessage(String message) {
		try {
			return objectMapper.readValue(message, PostMessage.class);
		} catch (Exception e) {
			log.log(Level.WARNING,
					MessageFormat.format(
							"JobHandler >> convertMessage >> can not convert message from: {0} >> Exception:", message),
					e);
			return new PostMessage();
		}
	}

}
