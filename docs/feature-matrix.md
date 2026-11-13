# Feature matrix

Status as of 13 November 2026. ClearChoice annotates the original Android shopping app with bounded accessibility annotations. There is no consumer Demo Shop or Shopping Mode. No retailer support is claimed.

The earlier 57-slot blueprint matrix described a reconstructed demo-shop approach. It is retired. Its fixture results are historical and do not count as current capability.

## 1. Demonstrated (test-only source)

Evidence: 17 parser checks and 16 Android source-to-annotation checks passed. Android checks used a test-only native source with no launcher entry. This is not retailer compatibility evidence.

| Capability | Evidence |
|---|---|
| Parse observed price, delivery and mandatory fees; label unknown fees incomplete | 17 parser checks; `apps/android/clearchoice/src/test/run-price-evidence-tests.sh` |
| Price mark at observed price bounds | 16 Android checks (source bounds); [log](qa/price-annotation-instrumentation.log) |
| Touches pass through the mark to the native control | 16 Android checks (tap-through) |
| Annotation follows changed price evidence and scrolling | 16 Android checks |
| Pause; stable handling of own windows | 16 Android checks |
| Dismissible breakdown label | Described in [verification report](qa/user-design-price-annotation.md); screenshots in `docs/qa/ui-v2/` |
| Native control pages (apps, per-app settings, features, activity, permissions, settings) build, install and run | [verification report](qa/user-design-price-annotation.md) |
| Desktop view of the actual emulator (scrcpy/H264, input forwarding) | Health endpoint reported ready; see report |
| Fresh clone builds (JDK required) | [fresh-clone-build.md](qa/fresh-clone-build.md) |

## 2. Unverified

| Capability | State |
|---|---|
| Shopee end to end | Not installed on emulator. Unknown. |
| Lazada end to end | Not installed on emulator. Unknown. |
| Delivery-inclusive totals against a real app | Unknown. |
| Real-app scrolling, navigation, popups, rotation, rapid taps, permission revocation, service restart | Not tested. |
| Login and payment screens left unannotated in a real app | Not tested. |
| Large-text and accessibility review against supplied designs | Not finished. |
| Clean install and onboarding/permission flow of a final APK | Not tested. |
| Reproducible APK | Not achieved. Fresh build hash differs from README hash. |
| Samsung One UI launcher | Not applicable. Emulator does not run One UI; launcher styling unfinished. |

## 3. Unavailable or experimental

| Capability | State |
|---|---|
| Ad removal | Unavailable |
| Network tracking blocking | Unavailable |
| Notification filtering | Unavailable |
| Price/stock alerts | Unavailable |
| Browser extension | Experimental; internal fixture tests only |
| iOS SwiftUI/Safari companion | Experimental; simulator build only; cannot modify arbitrary native apps |
| Optional AI gateway | Opt-in, separate configuration; not part of the deterministic price path |
| Account, sync, backend | Not built |

## Historical internal work

Fixture-store, web control center, extension and DemoShop results (9 + 2 + 1 browser tests, 100-cycle DemoShop run) are indexed in [test-report.md](test-report.md). They belong to the retired approach.
