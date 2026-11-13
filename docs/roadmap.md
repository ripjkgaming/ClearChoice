# Roadmap

Status as of 13 November 2026. Current approach: bounded accessibility annotations over the original Android shopping app. See the [README](../README.md) for the full checklist and deadline (Monday 16 November 2026, 10:00 AM Singapore time).

## Done (demonstrated, test-only source)

17 parser checks and 16 Android source-to-annotation checks passed. Native control pages build and run. Fresh clone builds ([report](qa/fresh-clone-build.md)). Secrets audit done ([report](qa/secrets-audit.md)).

## Before submission

1. Install Shopee and Lazada from Google Play on the emulator. Credentials stay in the emulator.
2. Prove one retailer end to end: opt-in, normal launch, observed price, cancel mark, observed delivery/fee breakdown, native interaction, pause/removal. Record app version, region, screens and limits. Test the second retailer before claiming support. Until then: unverified.
3. Validate delivery, taxes, mandatory fees, quantities, variants and conditional discounts against the original app.
4. Test real-app scrolling, navigation, popups, rotation, rapid taps, permission revocation, service restart and pause/resume. Keep login and payment screens unannotated.
5. Finish visual and accessibility review, including large text.
6. Final APK: clean install test, publish with hash and limits. Fresh build hash differs from the README hash; decide which APK to publish and state its hash.
7. Resolve open items in the secrets audit (git author metadata, developer paths in logs, unviewed design images 02 and 03, untracked media files).
8. Record and host a video of three minutes or less. Replace the outdated demo video. Verify the link signed out.
9. Complete Devpost, credits and AI disclosure. Verify public links signed out and confirm submitted status.

## Not planned for this submission

Ad removal, tracking blocking, notification filtering and alerts are unavailable. Browser and iOS modules stay experimental.

## Later (unscheduled)

- Add a retailer only after end-to-end proof and documented limits.
- Encrypted sync only after account, retention, export and deletion are specified.
- Cloud AI only as a separate, explicitly consented option; no provider keys in distributed apps.

## Video storyboard

Text storyboard: [demo/narration.md](demo/narration.md). Nothing is submitted externally yet.
