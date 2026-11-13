# ClearChoice architecture

Status as of 13 November 2026. ClearChoice annotates the original Android shopping app. It does not replace the app, and it has no Demo Shop or Shopping Mode for consumers. See the [README](../README.md) for current status.

## Capability buckets

| Bucket | Contents |
|---|---|
| Demonstrated | 17 parser checks and 16 Android source-to-annotation checks, run against a test-only native source (not a retailer app). Native control pages build, install and run. Desktop view streams the emulator through scrcpy/H264. |
| Unverified | Shopee and Lazada end to end. Neither app is installed on the current emulator. Compatibility and delivery-inclusive totals are unknown. |
| Unavailable / experimental | Ad removal, tracking blocking, notification filtering and price/stock alerts are unavailable. Browser extension and iOS modules are experimental. |

## Components

- **Native control center** (`apps/android/`): manages app opt-ins, preferences, features, activity, permissions and pause. It uses the supplied navy/light designs.
- **Accessibility annotation service** (`apps/android/`): considers only explicitly opted-in packages. It reads observed price bounds from the source app and draws two bounded elements: a pass-through price mark and a dismissible breakdown label. Touches on the mark reach the original app. The service ignores its own windows.
- **Price evidence parser**: extracts observed price, delivery and mandatory fees from accessible text. It labels missing fee data as incomplete and never invents a final payable price. Covered by the 17 parser checks (`apps/android/clearchoice/src/test/run-price-evidence-tests.sh`).
- **Instrumentation checks** (`apps/android/clearchoice/src/androidTest/`): 16 checks use a test-only native activity with no launcher entry. They cover source bounds, native tap-through, price changes, scrolling, pause and own-window stability. Raw output: [price-annotation-instrumentation.log](qa/price-annotation-instrumentation.log). Passing them is not retailer evidence.
- **Desktop emulator view** (`apps/emulator-viewer/`): streams the actual emulator over loopback port 4300 with input forwarding. It needs a running emulator and is not hosted.
- **Optional AI gateway** (`services/ai-gateway/`): separate process, opt-in, separate configuration. Deterministic price validation does not need it. Provider keys are never embedded in distributed apps.

## Data flow

1. User opts a supported app in through the native control center and enables the accessibility service.
2. User opens the original app normally.
3. The service observes price nodes and their bounds. The parser validates them. Ambiguous currencies, unrelated prices and stale bounds are rejected.
4. The service draws the price mark and breakdown label. Unknown fees show as incomplete.
5. User taps through the mark to the original app, dismisses the label, or pauses ClearChoice. Pause removes annotations.

The annotation does not change a merchant's charge. Login and payment screens must stay unannotated; this is a requirement, and real-app testing of it is not done.

## Historical and internal work

These remain in the repository and are not consumer features:

- `demo/` fixture store, `apps/browser-extension/`, `packages/engine/`, `apps/control-center/` (web) and earlier reconstruction experiments. They serve internal QA and history. Older results (9 control-center, 2 extension, 1 gateway browser test; 100-cycle DemoShop run) validate those experiments only, not the current retailer path.
- `apps/ios/` SwiftUI companion and Safari extension: experimental. iOS cannot modify arbitrary native apps. The Safari target was compiled for the simulator only.

## Build and verify

Fresh-clone results are in [fresh-clone-build.md](qa/fresh-clone-build.md) (13 November 2026): `npm ci`, `npm test` (16 pass), `npm run build`, parser tests (17 pass) and `assembleDebug` all passed. A JDK is required; the default system `java` was a JRE only, so `JAVA_HOME` had to point to a JDK 21. The freshly built APK SHA-256 is `21082609b1b59cf6ddc39a9daa781cff5586ac704cdb9648daf15c0b5a89a693`. It differs from the README hash `59cac52a…443b37`, which matches the committed `apps/android/ClearChoice-debug.apk`. The likely cause is non-reproducible debug builds; this is not verified.

Public-file audit: [secrets-audit.md](qa/secrets-audit.md) found no keys, tokens or keystores. It lists open personal-data items (git author metadata, developer paths in logs, two unviewed design images).
