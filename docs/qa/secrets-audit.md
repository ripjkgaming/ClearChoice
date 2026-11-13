# Secrets and personal-data audit

Scope: all 153 tracked files on branch `phase1` and the full history of all refs (2 commits, 2026-11-11). Method: regex scans (`git grep`, `git log --all -p`) for API keys, tokens, private-key blocks, emails, phone numbers, IPs, personal paths, signing material, plus manual review of docs, logs and a sample of screenshots. Findings are redacted. Not independently verified: binary contents beyond strings/unzip checks, and any file not listed below.

## Result

No API keys, tokens, passwords, private keys, keystores, `.env` files or signing configs found in tracked files or history. No phone numbers found. Four personal-data items need a decision before publishing (below).

## Findings

| # | Severity | Location | Finding | Suggested action |
|---|----------|----------|---------|------------------|
| 1 | Medium | git history, both commits | Author and committer metadata contains a real name and a school email (`A…@students.edu.sg`). Public once the repo is public. | Confirm the owner accepts this, or rewrite history with a noreply address before publishing. |
| 2 | Low | `apps/android/qa/build.log:30`, `final-build.log:30`, `gateway-build.log:30`, `hardening-build.log:30`, `e2e.log:10,13` | Absolute developer paths `/Users/<username>/Documents/ChatGPT/ClearChoice/...` (username is the owner's real name, redacted here). | Redact or drop the logs. |
| 3 | Low | `apps/android/qa/ui-preference-contract.md:15` | Local JDK path under `~/.gradle/jdks/...`. No username, only local layout. | Optional cleanup. |
| 4 | Low | `docs/design/user-approved/01..05*.png` | Phone screenshots/mock-ups show clock, battery, signal. Viewed 01, 04, 05: no names, accounts, emails or phone numbers visible. 02 and 03 not viewed. | Check 02 and 03 manually. |
| 5 | Info | `tests/js/gateway.test.js:28,38` | `person@example.com`, `https://private.example/...?token=do-not-send`, `test-secret-server-only`, `API_KEY:'stub'`. Fake test fixtures used to prove sanitisation. | None. |
| 6 | Info | `services/ai-gateway/src/server.js:7,48,76` | Reads `AI_PROVIDER_API_KEY` from env. Only literal default is `ollama-local` in local mode. No key committed. | None. |
| 7 | Info | `.gitignore` | Ignores `.env*`, `*.pem`, `*.key`, `*.jks`, `*.keystore`, `*.p12`, `*.mobileprovision`, `local.properties`, and local video/audio captures. | None. |

## Binaries checked

- `apps/android/ClearChoice-debug.apk`, `DemoShop-debug.apk`, `qa/evidence/*-stress-tested.apk`: debug builds. `strings` on ClearChoice-debug.apk found no username or home path. Debug-signed (standard Android debug cert); no release keystore in repo. Other three APKs not string-scanned.
- `apps/android/gradle/wrapper/gradle-wrapper.jar`: standard Gradle wrapper, hash not verified against upstream.
- `apps/emulator-viewer/vendor/scrcpy-server-v5.0.1`: sha256 in the tracked `.sha256` file is `764eb6f7…bc536`; I did not recompute it or compare to upstream.
- Screenshots and `contact-sheet.jpg`: EXIF/metadata not inspected (`exiftool` not installed). Unknown.

## Not covered

- Untracked/ignored files (e.g. `ClearChoice-evidence.mp4`, `ClearChoice-demo.mp4`, `narration.aiff`) are not in the repo and were not scanned. Check before any upload.
- Only 7 of ~22 images were viewed.
