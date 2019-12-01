package litecart;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Created by Lilia Andreeva on 29.11.2019
 */

public class Basic extends WebDriverSettings {
    public static final String urlLitecart = "http://localhost/litecart/";
    public static final String titleLitecart = "My Store | Online Store";
    public static final String noSizeChange = "use default size";

    // Add Darck to the chart with specified values
    public Double addDuckToChart(String duckName, String duckSize, Integer duckQuantity){

        // Remember amount of goods in chart before adding new item
        Integer goodsBeforeAdd = getCartSize();
        System.out.println("=== Add new good: <"+duckName+">, size <"+duckSize+">, quantity <"+duckQuantity+">. === ");

        // Find Search element on the page
        WebElement searchElement = driver.findElement(By.className("navbar-header"));
        WebElement searchEditBox =  searchElement.findElement(By.className("form-control"));
        searchEditBox.click();
        // Search for Duck
        searchEditBox.sendKeys("Duck");
        searchEditBox.sendKeys(Keys.ENTER);

        // Find necessury Duck
        WebElement duckButton = driver.findElement(By.cssSelector("[data-name=\""+duckName+"\"]"));
        duckButton.click();

        // Is necessury - select size
        if (!duckSize.equals(noSizeChange)) {
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.name("options[Size]")));
            Select size = new Select(driver.findElement(By.name("options[Size]")));
            size.selectByVisibleText("Small");
        }

        // Select Quantity
        WebElement numberEditBox =  driver.findElement(By.name("quantity"));
        numberEditBox.click();
        numberEditBox.clear();
        numberEditBox.sendKeys(duckQuantity.toString());

        // Click button Add to Cart
        WebElement buttonAddToCart = driver.findElement(By.name("add_cart_product"));
        buttonAddToCart.click();

        // Remember Price
        WebElement cartQuantity = driver.findElement(By.cssSelector("[class=\"price-wrapper\"]"));
        WebElement price;

        try{
            price = cartQuantity.findElement(By.cssSelector("[class=\"campaign-price\"]"));
        }
        catch (NoSuchElementException e) {
            price = cartQuantity.findElement(By.cssSelector("[class=\"price\"]"));
        }
        Double actualPrice = Double.valueOf(price.getText().substring(1));

        // Wait until Cart icon will be refreshed with new values
        Integer goodsAfterAdd = getCartSize();
        Integer goodsExpected = goodsBeforeAdd + Integer.valueOf(duckQuantity);

        int i = 0;
        while ((goodsAfterAdd != goodsExpected) || (i==2000))
        {
             goodsAfterAdd = getCartSize();
             i++;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Assert.assertTrue("Goods were not added to the cart", (i!=2000));
        System.out.println("=== <"+duckName+"> was added, price <"+actualPrice.toString()+">. === ");

        return actualPrice;

    }

    public int getCartSize(){
        WebElement cartQuantity = driver.findElement(By.cssSelector("[class=\"badge quantity\"]"));
        String cartSize = cartQuantity.getText();

        if (cartSize.equals(""))
                return 0;
        else
            return Integer.valueOf(cartSize);

    }

    // Verify that in cart we have the same as we ordered
    public Double verifyDuckInCart(String duckName, String duckSize, Integer duckQuantity, Double duckPrice) {
        // Find requested Duck in on the page
        WebElement dataElement = driver.findElement(By.cssSelector("[data-name=\""+duckName+"\"]"));

        // Verify price
        Double dataPrice = Double.valueOf(dataElement.getAttribute("data-price"));
        Assert.assertTrue("<"+duckName+"> - Wrong price. Expected <"+duckPrice+">, Found <"+dataPrice+">.",
                dataPrice.equals(duckPrice));

        // Verify quantity
        Double dataQuantity = Double.valueOf(dataElement.getAttribute("data-quantity"));
        Assert.assertTrue("<"+duckName+"> - Wrong quantity. Expected <"+duckQuantity+">, Found <"+dataQuantity+">.",
                dataQuantity.equals(duckQuantity.doubleValue()));

        // Is necessary - verify size
        if (!duckSize.equals(noSizeChange)) {
            WebElement dataSize = dataElement.findElement(By.cssSelector("[class=\"options\"]"));
            String dataSizeValue = dataSize.getText().substring(6);
            Assert.assertTrue("<"+duckName+"> - Wrong size. Expected <"+duckSize+">, Found <"+dataSizeValue+">.",
                    duckSize.equals(dataSizeValue));
            System.out.println("=== <"+duckName+"> size <"+dataSizeValue+">. === ");
        }

        // Verify total for requested duck
        List<WebElement> sumList = dataElement.findElements(By.cssSelector("[class=\"text-right\"]"));
        WebElement dataTotal = sumList.get(1);
        Double dataTotalSum = Double.valueOf(dataTotal.getText().substring(1));
        Double dataTotalSumExpected = duckQuantity * duckPrice;
        Assert.assertTrue("<"+duckName+"> - Wrong total. Expected <"+dataTotalSumExpected+">, Found <"+dataTotalSum+">.",
                dataTotalSum.equals(dataTotalSumExpected));

        System.out.println("=== <"+duckName+"> verified in Cart, all data as expected. SubTotal:  <"+dataTotalSum+">. === ");

        return dataTotalSum;

    }

    // Enter data to specified edit box
    public void enterEditBox(WebElement customerDetailsElement, String editBoxName, String editBoxValue) {
        WebElement editBox = customerDetailsElement.findElement(By.name(editBoxName)) ;
        editBox.click();
        editBox.clear();
        editBox.sendKeys(editBoxValue);
        editBox.sendKeys(Keys.TAB);
    }



    // Fill all data in Customer Details section and save
    public void fillCustomerDetails() throws InterruptedException {
         WebElement customerDetailsElement = driver.findElement(By.id("box-checkout-customer"));

        WebElement companyName = customerDetailsElement.findElement(By.name("company")) ;
        actions.moveToElement(companyName).click().perform();

        companyName.sendKeys("Company Duck Buyer");
        companyName.sendKeys(Keys.TAB);

        enterEditBox( customerDetailsElement, "tax_id", "Some Tax Id");
        enterEditBox( customerDetailsElement, "firstname", "Ivan");
        enterEditBox( customerDetailsElement, "lastname", "Ivanov");
        enterEditBox( customerDetailsElement, "address1", "string for address1");
        enterEditBox( customerDetailsElement, "address2", "string for address 2");
        enterEditBox( customerDetailsElement, "postcode", "12345");
        enterEditBox( customerDetailsElement, "city", "Boston");
        enterEditBox( customerDetailsElement, "email", "Boston@ooo.us");
        enterEditBox( customerDetailsElement, "phone", "+17987654321");

        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("save_customer_details"))));
        WebElement saveCustomerDetails = driver.findElement(By.name("save_customer_details")) ;
        saveCustomerDetails.click();
        waitForJavascript(20000, 100);

        wait.until(ExpectedConditions.attributeToBe(driver.findElement(By.cssSelector("[class=\"summary wrapper\"]")), "style", "opacity: 1;"));
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.name("terms_agreed")));
        saveCustomerDetails = driver.findElement(By.name("save_customer_details")) ;
        actions.moveToElement(saveCustomerDetails).click().perform();

        waitForJavascript(20000, 100);


    }

    public void verifyCheckOutSummary(Double goodsTotalExpected, Double zoneBasedShippingExpected,
                                      Double cashOnDeliveryExpected ) {
        // Verify subTotal for whole order
        WebElement CheckOutSummeryElement = driver.findElement(By.cssSelector("[class=\"table table-striped table-bordered data-table\"]"));
        List<WebElement> sumList = CheckOutSummeryElement.findElements(By.cssSelector("[class=\"text-right\"]"));
        WebElement goodsTotalElement = sumList.get(1);
        Double goodsTotal = Double.valueOf(goodsTotalElement.getText().substring(1));
        Assert.assertTrue("Wrong goods total. Expected <"+goodsTotalExpected+">, Found <"+goodsTotal+">.",
                goodsTotal.equals(goodsTotalExpected));
        System.out.println("=== SubTotal:  <" + goodsTotal + ">. === ");

        WebElement zoneBasedShippingElement = sumList.get(3);
        Double zoneBasedShipping = Double.valueOf(zoneBasedShippingElement.getText().substring(1));
        Assert.assertTrue("Wrong Zone Based Shipping. Expected <"+zoneBasedShippingExpected+">, Found <"+zoneBasedShipping+">.",
                zoneBasedShipping.equals(zoneBasedShippingExpected));
        System.out.println("=== Zone Based Shipping:  <" + zoneBasedShipping + ">. === ");

        WebElement cashOnDeliveryElement = sumList.get(5);
        Double cashOnDelivery = Double.valueOf(cashOnDeliveryElement.getText().substring(1));
        Assert.assertTrue("Wrong Cash on Delivery. Expected <"+cashOnDeliveryExpected+">, Found <"+cashOnDelivery+">.",
                cashOnDelivery.equals(cashOnDeliveryExpected));
        System.out.println("=== Cash on Delivery:  <" + cashOnDelivery + ">. === ");

        WebElement paymentDueElement = sumList.get(7);
        Double paymentDue = Double.valueOf(paymentDueElement.getText().substring(1));
        Double paymentDueExpected = goodsTotal + zoneBasedShipping + cashOnDelivery;
        Assert.assertTrue("Wrong Payment Due. Expected <"+paymentDueExpected+">, Found <"+paymentDue+">.",
                paymentDue.equals(paymentDueExpected));
        System.out.println("=== Payment Due:  <" + paymentDue + ">. === ");
    }

    /// ========================================================================================
    /*
     Добавить в корзину 2 жёлтых уточки, продающихся со скидкой и размером Small, 
     а так же 3 фиолетовых уточки. 
     Открыть корзину, заполнить все необходимые поля в Customer Details. 
     Подтвердить заказ.
    Добавить проверки:
    - На странице подтверждения заказа, количество и название товаров отображается согласно выбранным, 
        так же итоговая стоимость рассчитывается корректно.
    - Корзина очищается после того, как заказ был подтверждён.
     */
    @Test
    public void verifyCreateOrder() throws InterruptedException {

        // ----------------------------------------------------
        // Start with browser open
        driver.get(urlLitecart);
        String title = driver.getTitle();
        Assert.assertTrue("Wrong page was opened", title.equals(titleLitecart));

        // ----------------------------------------------------
        // Add Yellow Duck to the Cart and get the price
        Double yellowDuckPrice = addDuckToChart("Yellow Duck", "Small", 2);

        // Add Purple Duck to the Cart and get the price
        Double purpleDuckPrice = addDuckToChart("Purple Duck", noSizeChange, 3);

        // ----------------------------------------------------
        // Click to Cart icon and wait until Cart is open
        WebElement cartButtonElement = driver.findElement(By.id("cart"));//driver.findElement(By.cssSelector("[class=\"badge quantity\"]"));
        cartButtonElement.click();

        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("box-checkout-cart")));
        waitForJavascript(20000, 100);

        // ----------------------------------------------------
        // Verify that Yellow Duck presented in Cart with expected values
        Double yellowDuckTotal = verifyDuckInCart("Yellow Duck", "Small", 2, yellowDuckPrice);

        // Verify that Purple Duck presented in Cart with expected values
        Double purpleDuckTotal = verifyDuckInCart("Purple Duck", noSizeChange, 3, purpleDuckPrice);

        // Verify subTotal
        WebElement subTotalElement = driver.findElement(By.cssSelector("[class=\"subtotal\"]"));
        WebElement subTotalElementValue = subTotalElement.findElement(By.cssSelector("[class=\"formatted-value\"]"));
        Double subTotalValue = Double.valueOf(subTotalElementValue.getText().substring(1));
        Double subTotalExpected = yellowDuckTotal + purpleDuckTotal;
        Assert.assertTrue("Wrong SubTotal. Expected <"+subTotalExpected+">, Found <"+subTotalValue+">.",
                subTotalValue.equals(subTotalExpected));
        System.out.println("=== Cart subtotal sum verified. SubTotal:  <"+subTotalValue+">. === ");

        // ----------------------------------------------------
        // Fill all data in Customer Details section and save
        fillCustomerDetails();

        // ----------------------------------------------------
        // Verify order summary is correct
        verifyCheckOutSummary(subTotalValue,8.95, 5.0 );

        WebElement commentsElement = driver.findElement(By.name("comments"));
        commentsElement.sendKeys("Hello word!");
        commentsElement.sendKeys(Keys.TAB);

        // Click 'I have read the Privacy Policy and Terms of Purchase and I consent.'
        WebElement termsAgreedElement = driver.findElement(By.name("terms_agreed"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", termsAgreedElement);
        termsAgreedElement.click();

        // ----------------------------------------------------
        // Click 'Confirm Order'
        WebElement confirmOrderElement = driver.findElement(By.name("confirm_order"));
        confirmOrderElement.click();

        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("box-order-success")));
        waitForJavascript(20000, 100);

        WebElement orderSuccessElement = driver.findElement(By.id("box-order-success"));
        WebElement orderSuccessTitle = orderSuccessElement.findElement(By.className("title"));
        String orderSuccessMessage = orderSuccessTitle.getText();
        System.out.println("=== "+orderSuccessMessage+" === ");

        // ----------------------------------------------------
        // Click to Cart icon and wait until Cart is open
        cartButtonElement = driver.findElement(By.id("cart"));
        cartButtonElement.click();
        waitForJavascript(20000, 100);

        wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("box-checkout"))));
        // Verify the Cart is empty
        WebElement boxCheckoutElement = driver.findElement(By.id("box-checkout"));
        String boxCheckoutText = boxCheckoutElement.getText();
        Assert.assertTrue("Cart not empty!", (boxCheckoutText.indexOf("There are no items in your cart") == 0));
        System.out.println("=== Cart is empty: <There are no items in your cart.> === ");
    }
}
