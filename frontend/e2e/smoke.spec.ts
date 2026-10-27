import { expect, test } from '@playwright/test'

test('home shows Car Shop brand', async ({ page }) => {
  await page.goto('/')
  await expect(page.getByText('Car Shop')).toBeVisible()
})
