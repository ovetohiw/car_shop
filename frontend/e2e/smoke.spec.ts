import { expect, test } from '@playwright/test'

test('UI scenario on :8080: brand, parts, configurator', async ({ page }) => {
  await page.goto('/')
  await expect(page.getByText('Car Shop').first()).toBeVisible()
  await expect(page.getByRole('heading', { name: 'Cars' })).toBeVisible()

  await page.getByRole('link', { name: 'Parts' }).click()
  await expect(page.getByRole('heading', { name: 'Parts' })).toBeVisible()

  await page.getByRole('link', { name: 'Configurator' }).click()
  await expect(page.getByText('Server total')).toBeVisible()
})
