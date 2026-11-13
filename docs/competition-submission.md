# ClearChoice — price clarity over the shopping apps you already use

Draft, not submitted. Status as of 13 November 2026. Deadline: Monday 16 November 2026, 10:00 AM Singapore time (check the [official dates](https://icehacks.devpost.com/details/dates)).

## Fit to IceHacks judging criteria

IceHacks lists five equally weighted criteria: Technical Execution, Creativity & Originality, Impact & Usefulness, Design & User Experience, and Presentation ([official page](https://icehacks.devpost.com/)). The sections below state what is demonstrated, what is unverified and what is unavailable.

## The problem

Shopping apps can show a price that is not the amount paid. Delivery and mandatory fees appear later. Shoppers need the observed price and its fees in one place without leaving the app they trust.

## What ClearChoice does

ClearChoice adds price clarity over existing Android shopping apps. The original app stays visible and handles browsing, accounts, carts and checkout. ClearChoice uses bounded accessibility annotations: a price mark at the observed price and a dismissible breakdown label showing observed delivery and mandatory fees. Unknown fees are labelled incomplete. ClearChoice never invents a final payable price and does not change what a merchant charges. Touches on the price mark pass through to the original app.

A native control center manages app opt-ins, preferences, features, activity, permissions and pause. There is no consumer Demo Shop or Shopping Mode.

## Evidence: three buckets

**Demonstrated.** 17 parser checks and 16 Android source-to-annotation checks passed. The Android checks used a test-only native source. They covered source bounds, native tap-through, price changes, scrolling, pause and own-window stability. Native control pages build, install and run. The desktop view streams the actual emulator. A fresh clone builds with a JDK ([report](qa/fresh-clone-build.md)). The secrets audit found no keys or tokens ([report](qa/secrets-audit.md)).

**Unverified.** Shopee and Lazada are not installed on the current emulator. Their compatibility and delivery-inclusive totals are unknown. Real-app scrolling, popups, rotation, permission revocation, service restart and login/payment handling are not tested. We do not claim retailer support.

**Unavailable or experimental.** Ad removal, tracking blocking, notification filtering and price/stock alerts are unavailable. Browser extension and iOS modules are experimental. The launcher is unfinished, and the emulator does not run Samsung One UI.

The passing Android test is not retailer compatibility evidence. Earlier fixture-shop, web, extension and DemoShop results are historical and are not claimed as current capability.

## Demo storyboard (target: three minutes or less)

Text only. The existing video shows the retired approach and must be replaced. Full narration: [docs/demo/narration.md](demo/narration.md).

- **Problem.** Price shown differs from the amount paid.
- **Control center.** Show opt-in, preferences, permissions and pause.
- **Annotation.** Show the price mark and breakdown label. Use a real retailer only if it has been proven end to end by recording time; otherwise say the source is a test-only app.
- **Native interaction.** Tap through the mark; dismiss the label.
- **Pause.** Pause removes annotations.
- **Limits.** State the three buckets above plainly.

## Technologies and implementation

Android (Java), accessibility service, native control center, JavaScript tooling, scrcpy/H264 desktop view. Optional opt-in AI gateway, not used by deterministic price validation. Build and test: see the [README](../README.md).

## Challenges, learning, impact, next steps

Challenges: reading reliable price bounds from a third-party app; rejecting ambiguous currencies, unrelated prices and stale bounds. Next steps: install Shopee and Lazada, prove one end to end, then validate fees and real-app behavior. Impact on real shoppers: unknown until a retailer is verified.

## Submission fields

- AI-tool disclosure: [TEAM TO FILL] (see [credits-and-disclosure.md](credits-and-disclosure.md))
- Team and registration: [TEAM TO FILL]
- Tracks: [TEAM TO FILL]
- Public video link: not yet recorded.
- Repository: public link to verify signed out.

## Known blockers

Shopee/Lazada end-to-end proof; new video; APK hash decision (fresh build differs from README hash); secrets-audit items (git author metadata, developer paths in logs, unviewed images); Devpost submission and signed-out link checks.
