import {after, before, describe, it} from "node:test";

const {Builder, By, until, Key} = await import('selenium-webdriver');


// All the tests are done on the protocol page with a sprint number of 3
describe('Protocol', function () {
    let driver;

    async function login(driver) {
        try {
            await driver.get('http://localhost:5173/login');

            const emailField = await driver.findElement(By.css('div.n-input input[type="email"]'));
            const passwordField = await driver.findElement(By.css('div.n-input input[type="password"]'));

            await emailField.sendKeys('cecile.tessier@reseau.eseo.fr');
            await passwordField.sendKeys('123');

            const loginButton = await driver.findElement(By.css('button.button'));
            await loginButton.click();

            // Wait for the login to complete
            await driver.wait(until.urlIs('http://localhost:5173'), 10000);

        } catch (error) {
            console.error('An error occurred during the login process:', error);
        }
    }

    before(async function () {
        driver = await new Builder().forBrowser('chrome').build();
        await login(driver);
        // Navigate to the preview page
        await driver.get('http://localhost:5173/preparation/preview');

        // Wait for the navigation to complete
        await driver.wait(until.urlIs('http://localhost:5173/preparation/preview'), 3000);

        // Wait for the button with id 'nextstep' to be loaded and then click it
        const nextStepButton = await driver.wait(until.elementLocated(By.id('nextstep')), 10000);
        await driver.wait(until.elementIsVisible(nextStepButton), 10000);
        await nextStepButton.click();

        // Wait for the navigation to complete
        await driver.wait(until.urlIs('http://localhost:5173/preparation/protocol'), 10000);
    });

    after(async function () {
        await driver.quit();
    });

    // Test description: Test the sprint number input field, the button and the number of sprints that are displayed on the screen
    it('sprintNumberTest', async function () {
        let inputSprintNumber = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.input_sprint_number input')), 10000)), 10000);
        await inputSprintNumber.sendKeys('1');

        let primaryButton = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('button.primaryButton')), 10000)), 10000);

        await primaryButton.click();

        let sprints = await driver.wait(until.elementsLocated(By.css('div.sprintN')), 10000);

        if (sprints.length === 1) {
            console.log('Test passed');
        } else {
            console.log('Test failed');
        }
    }, 10000);

    // Test description: Test the message error that is displayed when endDate is before startDate
    it('messageErrorEndDateInfStartDateInputTest', async function () {
        let inputStartDate = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.input_sprint_startDate input')), 10000)), 10000);
        let inputEndDate = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.input_sprint_endDate input')), 10000)), 10000);

        await inputStartDate.sendKeys('31/12/2023');
        await inputEndDate.sendKeys('01/01/2023');

        let errorMessageElement = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.n-message__content')), 10000)), 10000);
        let errorMessage = await errorMessageElement.getText();

        if (errorMessage === 'La date de fin ne peut pas être antérieure à la date de début') {
            console.log('Test passed');
        } else {
            console.log('Test failed');
        }
    }, 10000);

    // Test description: Test the change of the status of the input field when the endDate is before the startDate
    it('changeStatusEndDateInputTest', async function () {
        let inputEndDate = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.input_sprint_endDate')), 10000)), 10000);
        let status = await inputEndDate.getAttribute('data-status');

        if (status === "error") {
            console.log('Test passed');
        } else {
            console.log('Test failed');
        }
    }, 10000);

    // Test description: Test the message error that is displayed when the input fields are empty
    it('messageErrorMissingInputFieldsTest', async function () {
        let inputStartDate = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.input_sprint_startDate input')), 10000)), 10000);
        let inputEndDate = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.input_sprint_endDate input')), 10000)), 10000);

        await inputStartDate.sendKeys(Key.chord(Key.CONTROL, "a"), Key.BACK_SPACE); // clear the input field
        await inputEndDate.sendKeys(Key.chord(Key.CONTROL, "a"), Key.BACK_SPACE);
        await inputStartDate.sendKeys('31/12/2023');

        await driver.sleep(3000);

        let primaryButton = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('button.primaryButton2')), 10000)), 10000);
        await primaryButton.click();

        let errorMessageElement = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.n-message__content')), 10000)), 10000);
        let errorMessage = await errorMessageElement.getText();

        if (errorMessage === 'Veuillez remplir tous les champs') {
            console.log('Test passed');
        } else {
            console.log('Test failed');
        }
    });

    // Test description: Test the message error that is displayed when the endDate is before the startDate when the button is clicked
    it('messageErrorEndDateInfStartDateButtonTest', async function () {
        let inputStartDate = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.input_sprint_startDate input')), 10000)), 10000);
        let inputEndDate = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.input_sprint_endDate input')), 10000)), 10000);

        await inputStartDate.sendKeys('31/12/2023');
        await inputEndDate.sendKeys('01/01/2023');

        await driver.sleep(3000);

        let primaryButton = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('button.primaryButton2')), 10000)), 10000);
        await primaryButton.click();

        let errorMessageElement = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.n-message__content')), 10000)), 10000);
        let errorMessage = await errorMessageElement.getText();

        if (errorMessage === 'La date de fin ne peut pas être antérieure à la date de début') {
            console.log('Test passed');
        } else {
            console.log('Test failed');
        }
    });

    // Test description: Test the success message that is displayed when the sprint is updated when the button is clicked
    it('successMessageTestUpdatedSprint', async function () {
        let inputStartDate = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.input_sprint_startDate input')), 10000)), 10000);
        let inputEndDate = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.input_sprint_endDate input')), 10000)), 10000);
        let selectEndType = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.input_sprint_endType div.n-base-selection--selected')), 10000)), 10000);

        await selectEndType.click();
        await driver.wait(until.elementLocated(By.css('div.n-base-select-option__content')), 10000).click();

        await inputStartDate.sendKeys(Key.chord(Key.CONTROL, "a"), Key.BACK_SPACE); // clear the input field
        await inputEndDate.sendKeys(Key.chord(Key.CONTROL, "a"), Key.BACK_SPACE);

        await inputStartDate.sendKeys('01/02/2023');
        await inputEndDate.sendKeys('01/03/2023');

        await driver.sleep(4000);

        let primaryButton2 = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('button.primaryButton2')), 10000)), 10000);
        await primaryButton2.click();

        let successMessageElement = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.n-message__content')), 10000)), 10000);
        let successMessage = await successMessageElement.getText();

        if (successMessage === 'Les 1 sprints ont été créés avec succès') {
            console.log('Test passed');
        } else {
            console.log('Test failed');
        }
    }, 20000);

    // Test description: Test that the grade scale value is updated in the table when the detail field is filled and Enter is pressed
    it('updateGradeScaleValueInTableTest', async function () {
        // Locate the cell in the table and click it to enter edit mode
        let cellInTable = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.grade_scale td.n-data-table-td')), 10000)), 10000);
        await cellInTable.click();

        // Locate the edit field in the cell and fill it
        let editFieldInCell = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.grade_scale td.n-data-table-td input')), 10000)), 10000);
        await editFieldInCell.sendKeys('Nouvelle valeur', Key.ENTER);

        // Wait for the update to complete
        await driver.sleep(3000);

        // Verify that the cell in the table now displays the new value
        let updatedValueInTable = await cellInTable.getText();
        if (updatedValueInTable === 'Nouvelle valeur') {
            console.log('Test passed');
        } else {
            console.log('Test failed');
        }
    }, 20000);

    // Test description: Test that a new row is added to the table when the plus icon is clicked
    it('addNewRowInTableTest', async function () {
        // Locate the plus icon and click it
        let plusIcon = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.grdSc i.fa-solid.fa-plus')), 10000)), 10000);
        await plusIcon.click();

        // Wait for the update to complete
        await driver.sleep(3000);

        // Get the number of rows in the table after the click
        let rowsAfter = await driver.findElements(By.css('div.grdSc tr.n-data-table-tr'));

        // Verify that a new row has been added to the table
        if (rowsAfter.length) {
            console.log('Test passed');
        } else {
            console.log('Test failed');
        }
    }, 20000);

    // Test description: Test that the value in the new row added to the table is updated when the detail field is filled and Enter is pressed
    it('updateValueInNewRowInTableTest', async function () {
        // Locate the cell in the last row and click it to enter edit mode
        let cellInRow = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.grdSc td.n-data-table-td')), 10000)), 10000);
        await cellInRow.click();

        // Locate the edit field in the cell and fill it
        let editFieldInCell = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.grdSc td.n-data-table-td input')), 10000)), 10000);
        await editFieldInCell.sendKeys('Nouvelle valeur', Key.ENTER);

        // Wait for the update to complete
        await driver.sleep(3000);

        // Verify that the cell in the last row now displays the new value
        let updatedValueInLastRow = await cellInRow.getText();
        if (updatedValueInLastRow === 'Nouvelle valeur') {
            console.log('Test passed');
        } else {
            console.log('Test failed');
        }
    }, 20000);

    // Test description: Test that the row is deleted when the trash icon is clicked
    it('deleteRowInTableTest', async function () {
        // Locate the plus icon and click it
        let plusIcon = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.grdSc i.fa-solid.fa-plus')), 10000)), 10000);
        await plusIcon.click();

        await driver.sleep(3000);

        // Get the number of rows in the table before the click
        let rowsBefore = await driver.findElements(By.css('div.grdSc tr.n-data-table-tr'));

        // Locate the trash icon and click it
        let trashIcon = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.grdSc td.n-data-table-td--last-col i.fa.fa-trash')), 10000)), 10000);
        await trashIcon.click();

        // Wait for the update to complete
        await driver.sleep(3000);

        // Get the number of rows in the table after the click
        let rowsAfter = await driver.findElements(By.css('div.grdSc tr.n-data-table-tr'));

        // Verify that the row has been deleted from the table
        if (rowsAfter.length === rowsBefore.length - 1) {
            console.log('Test passed');
        } else {
            console.log('Test failed');
        }
    }, 20000);

    // Test description: Test that the success message is displayed and the page is redirected to the home page when the Publish button is clicked
    it('publishAndRedirectTest', async function () {
        // Locate the Publish button and click it
        let publishButton = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.gradeScale div.button button.primaryButton')), 10000)), 10000);
        await publishButton.click();

        // Verify that the page is redirected to the home page
        let currentUrl = await driver.getCurrentUrl();

        if (currentUrl !== 'http://localhost:5173/') {
            console.log('Test failed: Not redirected to home page');
            return;
        }

        // Verify that the success message is displayed
        let successMessage = await driver.wait(until.elementIsVisible(await driver.wait(until.elementLocated(By.css('div.n-message__content')), 10000)), 10000);

        if (!successMessage) {
            console.log('Test failed: Success message not displayed');
        } else {
            console.log('Test passed');
        }
    }, 20000);

});

