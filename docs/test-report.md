# Verification report (index)

Status as of 13 November 2026. Current approach: bounded accessibility annotations over the original Android shopping app. No retailer support is claimed.

## Demonstrated: current approach (test-only source)

- 17 parser checks passed. 16 Android source-to-annotation checks passed (instrumentation code 0). Source: a test-only native activity, not a retailer app. Details: [user-design-price-annotation.md](qa/user-design-price-annotation.md). Raw output: [price-annotation-instrumentation.log](qa/price-annotation-instrumentation.log). Screenshots: `qa/ui-v2/`.
- Fresh clone, 13 November 2026 ([fresh-clone-build.md](qa/fresh-clone-build.md)): `npm ci` pass; `npm test` 16 pass, 0 fail; `npm run build` pass; parser tests 17 pass; `assembleDebug` pass. Parser tests and Gradle need a JDK; the default PATH had a JRE only and the parser script failed with `javac: command not found` until `JAVA_HOME` was set. `npm ci` reports 1 high-severity vulnerability, not investigated.
- APK hash: fresh build `21082609b1b59cf6ddc39a9daa781cff5586ac704cdb9648daf15c0b5a89a693` differs from README hash `59cac52a…443b37`. README hash matches the committed `apps/android/ClearChoice-debug.apk`. Cause not verified.
- Secrets and personal data: [secrets-audit.md](qa/secrets-audit.md). No keys, tokens or keystores found. Open items: author metadata in history, developer paths in some logs, design images 02 and 03 not viewed, untracked media not scanned.

## Unverified

- Shopee and Lazada end to end. Neither is installed on the emulator.
- Delivery-inclusive totals, real-app scrolling, navigation, popups, rotation, permission revocation, service restart and login/payment screen handling.
- Large-text accessibility review and clean-install onboarding of a final APK.

## Unavailable or experimental

Ad removal, tracking blocking, notification filtering and price/stock alerts are unavailable. Browser extension and iOS modules are experimental.

## Historical results (retired approach)

These test the older fixture shop, web control center, extension and DemoShop reconstruction. They do not validate the current retailer path and are not counted as demonstrated capability.

- Web control center on a fixture store: 9 Playwright tests passed. Extension: 2 passed. Gateway CORS: 1 passed. Shared-engine/gateway unit suite: 16 passed.
- iOS app and Safari extension: simulator build succeeded (`BUILD SUCCEEDED`). No on-device Safari run.
- Android DemoShop: instrumentation passed; 100 cycles over 1,816.48 seconds, no recorded failure. See `apps/android/qa/`.
- Local gateway: one live local-model review returned HTTP 200 with three source-anchored annotations; an earlier layout request ignored lowest-price-first ordering. Evidence: [local-review-result.json](qa/local-review-result.json). Consented browser AI request not verified. Free-form model discovery not proved.
- Older desktop screenshots ([home](qa/desktop-home.png), [shopping](qa/desktop-shopping.png)) show the retired fixture-shop view.
