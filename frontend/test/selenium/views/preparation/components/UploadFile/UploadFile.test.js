import {afterEach, beforeEach, describe, it} from "node:test";
import {Builder, By, Key, until} from 'selenium-webdriver';
import path from 'path';
import { fileURLToPath } from 'url';
import { dirname } from 'path';
import { fail } from 'node:assert';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

describe('UploadFile', function() {
    let driver
    beforeEach(async function () {
        driver = await new Builder().forBrowser('chrome').build();
        await driver.get('http://localhost:5173/login');
        // Fill in the login form and submit
        await driver.findElement(By.css('input[type="email"]')).sendKeys('yohan.jaffre@reseau.eseo.fr');
        await driver.findElement(By.css('input[type="password"]')).sendKeys('letmein8', Key.RETURN);
        await driver.findElement(By.css('.button')).click();
        // Wait until the home page is loaded
        await driver.wait(until.urlIs('http://localhost:5173/'), 10000);
        // Click on the 'Préparation' tab in the navbar
        await driver.get('http://localhost:5173/preparation/');
        // Click on the 'Créer une list' of the progress bar
        await driver.findElement(By.css('div.tab span')).click();
        // Wait until the 'UploadFile' component is loaded
        await driver.wait(until.urlIs('http://localhost:5173/preparation/create'), 10000);
        await driver.wait(until.elementLocated(By.className('file-input')), 10000);
    });

    afterEach(async function () {
        await driver.quit();
    });

    it('importFile', async function () {
        // Select the file to upload
        const fileInput = driver.findElement(By.className('file-input'));
        const filePath = path.resolve(__dirname, 'ListeEleveLD.csv');
        await fileInput.sendKeys(filePath);

        // Wait until the 'Valider' button is enabled
        await driver.wait(until.elementIsEnabled(driver.findElement(By.xpath('//button[normalize-space()="Valider"]'))), 10000);
        console.log('Test passed');
    });
    it('importFileAndValidate', async function () {
        // Select the file to upload
        const fileInput = driver.findElement(By.className('file-input'));
        const filePath = path.resolve(__dirname, 'ListeEleveLD.csv');
        await fileInput.sendKeys(filePath);

        // Click on the 'Valider' button
        await driver.findElement(By.xpath('//button[normalize-space()="Valider"]')).click();

        // Verify that the file has been displayed in the list
        const table = await driver.findElements(By.css('.n-data-table'));
        if (table == null) {
            fail('Test failed : the file has not been uploaded')
        } else {
            console.log('Test passed');
        }
    });
    it('importFileAndCancel', async function () {
        // Select the file to upload
        const fileInput = driver.findElement(By.className('file-input'));
        const filePath = path.resolve(__dirname, 'ListeEleveLD.csv');
        await fileInput.sendKeys(filePath);

        // Click on the 'Annuler' button
        await driver.findElement(By.xpath('//button[normalize-space()="Annuler"]')).click();

        // Verify that the file input is empty
        const fileInputValue = await fileInput.getAttribute('value');
        if (fileInputValue !== '') {
            fail('Test failed : the file input is not empty')
        } else {
            console.log('Test passed');
        }
    });
    it('importFileAfterCancelAndValidate', async function () {
        // Select the file to upload
        const fileInput = driver.findElement(By.className('file-input'));
        const filePath = path.resolve(__dirname, 'ListeVide.csv');
        await fileInput.sendKeys(filePath);

        // Click on the 'Annuler' button
        await driver.findElement(By.xpath('//button[normalize-space()="Annuler"]')).click();
        // Verify that the file input is empty
        const fileInputValue = await fileInput.getAttribute('value');
        if (fileInputValue !== '') {
            fail('Test failed : the file input is not empty')
        }

        // Select the new file to upload
        const fileInput2 = driver.findElement(By.className('file-input'));
        const filePath2 = path.resolve(__dirname, 'ListeEleveLD.csv');
        await fileInput2.sendKeys(filePath2);

        // Click on the 'Valider' button
        await driver.findElement(By.xpath('//button[normalize-space()="Valider"]')).click();

        // Verify that the file has been displayed in the list
        const table = await driver.findElements(By.css('.n-data-table'));
        if (table == null) {
            fail('Test failed : the file has not been uploaded')
        } else {
            console.log('Test passed');
        }
    });
    it('importFileOnFile', async function () {
        // Select the file to upload
        const fileInput = driver.findElement(By.className('file-input'));
        const filePath = path.resolve(__dirname, 'ListeVide.csv');
        await fileInput.sendKeys(filePath);

        // Re import a file
        const filePath2 = path.resolve(__dirname, 'ListeEleveLD.csv');
        await fileInput.sendKeys(filePath2);

        // Click on the 'Valider' button
        await driver.findElement(By.xpath('//button[normalize-space()="Valider"]')).click();

        // Verify that the file has been displayed in the list
        const table = await driver.findElements(By.css('.n-data-table'));
        if (table == null) {
            fail('Test failed : the file has not been uploaded')
        } else {
            console.log('Test passed');
        }
    });
    it('importWrongFile', async function () {
        // Select the wrong file to upload
        const fileInput = driver.findElement(By.className('file-input'));
        const filePath = path.resolve(__dirname, 'logo.jpeg');
        await fileInput.sendKeys(filePath);

        // Verify that the file has not been uploaded
        const fileInputValue = await fileInput.getAttribute('value');
        if (fileInputValue !== '') {
            fail('Test failed : the file has been uploaded')
        } else {
            console.log('Test passed');
        }
    });
    it('changeTab', async function () {
        // Click on the 'Configurer les equipes' tab
        await driver.findElement(By.css('div.tab:nth-child(3) span')).click();

        // Verify that the change of tab has not been done
        const url = await driver.getCurrentUrl();
        if (url !== 'http://localhost:5173/preparation/create') {
            fail('Test failed : the tab has been changed')
        } else {
            console.log('Test passed');
        }
    });
});