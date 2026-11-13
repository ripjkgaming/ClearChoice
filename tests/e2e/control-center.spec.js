import { test, expect } from '@playwright/test';

const baseURL = process.env.CC_BASE_URL || 'http://127.0.0.1:4173';
const frame = page => page.frameLocator('iframe[title="Developer test store"]');

test.beforeEach(async ({ page }) => {
  await page.goto(baseURL);
  await page.evaluate(() => localStorage.clear());
  await page.reload();
});
const openStore = async page => { await page.goto(baseURL + '/#developer-test'); await expect(page.locator('#title')).toHaveText('Developer test store'); };

test('control center opens on the live Android emulator viewer', async ({ page }) => {
  await expect(page.getByLabel('Samsung Galaxy S26 Ultra preview')).toBeVisible();
  await expect(page.locator('iframe[title="Live Android emulator"]')).toBeVisible();
});

test('developer test store is reachable at #developer-test', async ({ page }) => {
  await openStore(page);
  await expect(page.locator('iframe[title="Developer test store"]')).toBeVisible();
  await expect(frame(page).locator('[data-cc-product]')).toHaveCount(12);
});

test('reconstructed product layout maps Add to the source cart and verifies one change', async ({ page }) => {
  await openStore(page);
  const shop = frame(page);
  await expect(shop.locator('[data-cc-product]')).toHaveCount(12);
  await expect(shop.locator('#cc-adapter-panel')).toBeVisible();
  await expect(shop.locator('#cc-adapter-panel .cc-card')).toHaveCount(12);
  await shop.locator('#cc-adapter-panel .cc-card').first().getByRole('button', { name: 'Add to cart' }).click();
  await expect(shop.getByRole('button', { name: 'Cart 1' })).toBeVisible();
  await expect(shop.locator('#cc-adapter-panel')).toContainText('Added once; cart count verified.');
  await shop.getByRole('button', { name: 'Close' }).click();
  await shop.getByRole('button', { name: 'Cart 1' }).click();
  await expect(shop.locator('#cartview')).toContainText('QuietComfort Wireless Headphones');
  await expect(shop.locator('#cartview .cartline')).toContainText('$279.00');
});

test('source store Add remains usable without the reconstructed layer', async ({ page }) => {
  await openStore(page);
  const shop = frame(page);
  await shop.locator('#cc-adapter-panel').waitFor({state:'visible'});
  await shop.getByRole('button', {name:'Close',exact:true}).click();
  await shop.locator('[data-cc-product="w1"] [data-cc-action="add-to-cart"]').click();
  await expect(shop.getByRole('button', { name: 'Cart 1' })).toBeVisible();
  await expect(shop.locator('#cc-adapter-launch')).toBeVisible();
});

test('product details keep variant selection in the original store modal', async ({ page }) => {
  await openStore(page);
  const shop = frame(page);
  await shop.locator('#cc-adapter-panel').waitFor({state:'visible'});
  await shop.getByRole('button', {name:'Close',exact:true}).click();
  await shop.locator('[data-cc-product="h2"] [data-detail]').first().click();
  await shop.locator('#variant').selectOption('Graphite');
  await shop.locator('#detail [data-add="h2"]').click();
  await shop.getByRole('button', { name: 'Cart 1' }).click();
  await expect(shop.locator('#cartview')).toContainText('Graphite');
  await expect(shop.locator('#cartview')).toContainText('$129.00');
});

test('cart quantity controls update the selected product variant line', async ({ page }) => {
  await openStore(page);
  const shop = frame(page);
  await shop.locator('#cc-adapter-panel').waitFor({state:'visible'});
  await shop.getByRole('button', {name:'Close',exact:true}).click();
  await shop.getByRole('button', { name: 'View Studio One Headphones' }).click();
  await shop.locator('#variant').selectOption('White');
  await shop.locator('#detail [data-add="h2"]').click();
  await shop.locator('[data-cc-product="h2"] [data-detail]').first().click();
  await shop.locator('#variant').selectOption('Graphite');
  await shop.locator('#detail [data-add="h2"]').click();
  await shop.getByRole('button', { name: 'Cart 2' }).click();
  await expect(shop.locator('#cartview')).toContainText('White · Qty');
  await expect(shop.locator('#cartview')).toContainText('Graphite · Qty');
  await shop.locator('[data-qty="h2"][data-variant="White"][data-delta="1"]').click();
  await expect(shop.locator('#cartview .cartline').filter({ hasText: 'White' })).toContainText(/Qty.*2/);
  await expect(shop.locator('#cartview .cartline').filter({ hasText: 'Graphite' })).toContainText(/Qty.*1/);
});
