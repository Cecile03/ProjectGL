import { beforeEach, afterEach, describe, it} from "node:test";
import {Builder, By, Key, until} from 'selenium-webdriver';
import path from 'path';
import { fileURLToPath } from 'url';
import { dirname } from 'path';
import { fail } from 'node:assert';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

describe('PageUpload', function() {
    let driver;

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
    });
    afterEach(async function () {
        await driver.quit();
    });

    it('navigateToPageUpload', async function () {
        // Click on the 'Importer des étudiants' tab of the progress bar
        await driver.findElement(By.css('div.tab:nth-child(2) span')).click();
        // Wait until the 'PageUpload' component is loaded
        await driver.wait(until.urlIs('http://localhost:5173/preparation/import'), 10000);
        await driver.wait(until.elementLocated(By.className('uploader-container')), 10000);
    });
    it('uploadStudentsWithCoef', async function () {
        // Select the file to upload
        const fileInput = driver.findElement(By.css('input[type="file"]'));
        const filePath = path.resolve(__dirname, 'ListeElevesCoef.csv');
        await fileInput.sendKeys(filePath);

        // Click on the 'Valider' button
        await driver.findElement(By.xpath('//button[normalize-space()="Valider"]')).click();

        // Verify that the students have been added
        const table = await driver.findElements(By.css('.n-data-table'));
        if (table == null) {
            fail('Test failed : the students have not been added')
        } else {
            console.log('Test passed');
        }
    });
});