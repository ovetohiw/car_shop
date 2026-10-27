import { expect, test } from '@playwright/test'

test('guest sees cars with images and parts section', async ({ page }) => {
  await page.goto('/')
  await expect(page.getByText('Car Shop').first()).toBeVisible()
  await expect(page.locator('.product img').first()).toBeVisible()

  await page.getByRole('link', { name: 'Parts' }).click()
  await expect(page.getByRole('heading', { name: 'Parts' })).toBeVisible()
  await expect(page.locator('.product img').first()).toBeVisible()
})

test('customer registers, places order, sees it; staff advances status', async ({
  page,
}) => {
  const email = `e2e.${Date.now()}@example.com`
  const password = 'Customer1!'

  await page.goto('/configure')
  await expect(page.getByText('Server total')).toBeVisible()
  await expect(page.getByRole('button', { name: 'Place order' })).toBeDisabled()

  await page.getByPlaceholder('email').fill(email)
  await page.getByPlaceholder('password').fill(password)
  await page.getByRole('button', { name: 'Register' }).click()
  await expect(page.locator('.auth-bar__email')).toHaveText(email)

  await expect(page.getByRole('button', { name: 'Place order' })).toBeEnabled({
    timeout: 10_000,
  })
  await page.getByRole('button', { name: 'Place order' }).click()
  await expect(page.getByText(/Order #\d+ created/)).toBeVisible()

  const created = await page.getByText(/Order #\d+ created/).textContent()
  const orderId = created?.match(/Order #(\d+)/)?.[1]
  expect(orderId).toBeTruthy()

  await page.getByRole('link', { name: 'My orders' }).click()
  await expect(page.getByRole('heading', { name: `Order #${orderId}` })).toBeVisible()
  await expect(page.getByText('NEW').first()).toBeVisible()

  await page.getByRole('button', { name: 'Sign out' }).click()
  await page.getByPlaceholder('email').fill('staff@carshop.local')
  await page.getByPlaceholder('password').fill('StaffDemo1!')
  await page.getByRole('button', { name: 'Sign in' }).click()
  await expect(page.locator('.auth-bar__role')).toHaveText('STAFF')

  await page.getByRole('link', { name: 'Staff' }).click()
  const orderCard = page.locator('.panel', {
    has: page.getByRole('heading', { name: `Order #${orderId}` }),
  })
  await expect(orderCard).toBeVisible()
  await orderCard.getByRole('button', { name: '→ SUBMITTED' }).click()
  await expect(orderCard.getByText('SUBMITTED')).toBeVisible()
  await orderCard.getByRole('button', { name: '→ IN_FULFILLMENT' }).click()
  await expect(orderCard.getByText('IN_FULFILLMENT')).toBeVisible()
  await orderCard.getByRole('button', { name: '→ COMPLETED' }).click()
  await expect(orderCard.getByText('COMPLETED')).toBeVisible()
})
