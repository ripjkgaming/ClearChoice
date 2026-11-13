# Credits and AI disclosure

Compiled from repository contents only. Anything not evidenced in the repo is marked unknown and must be confirmed by the team before submission.

## Third-party code and tools

| Item | Where | License | Notes |
|------|-------|---------|-------|
| `@playwright/test` ^1.65.0 | `package.json` (dev) | Apache-2.0 per `package-lock.json` | Test runner. |
| `ws` ^8.18.3 | `package.json` | MIT per `package-lock.json` | WebSocket server for emulator stream. |
| scrcpy server v5.0.1 | `apps/emulator-viewer/vendor/scrcpy-server-v5.0.1` | Apache-2.0 (`vendor/LICENSE.scrcpy`) | Prebuilt binary vendored. Upstream attribution/NOTICE not included; origin URL not recorded in repo. |
| Gradle wrapper 8.10.2 | `apps/android/gradle/wrapper/` | Apache-2.0 (headers in `gradlew`) | Standard wrapper; jar not verified. |
| Android SDK / AndroidX (compileSdk 35, `android.useAndroidX=true`) | `apps/android/*/build.gradle`, `gradle.properties` | Apache-2.0 (general knowledge; not stated in repo) | Repositories: Google, Maven Central, Gradle Plugin Portal. No explicit AndroidX dependency lines found by grep. |
| Node.js >=20 | `package.json` engines | n/a | Runtime. |

## Fonts

- `demo/store.css:1` imports DM Sans and Manrope from Google Fonts at runtime. Both are normally SIL OFL 1.1 (not stated in repo). Not bundled. Runtime network call to Google; consider self-hosting or noting it.
- Native Android UI appears to use system fonts. Unknown for the iOS app.

## Images and assets

| Asset | Source / rights | Status |
|-------|-----------------|--------|
| `docs/design/user-approved/01-05*.png` | Described as "user-supplied design references" (`README.md` in that folder). Original author and tool are not recorded. Screens depict third-party brand logos (Amazon, Walmart, Target, eBay, Best Buy, Shop, Temu, AliExpress). | **Unclear.** Confirm who made them (and whether an AI image tool was used) and that showing retailer logos is acceptable. Trademarks belong to their owners. |
| `docs/qa/ui-v2/*.png` | App screenshots of this project. The design README calls "generated v2 visual direction" superseded. | Own work (assumed); generation method unknown. |
| `docs/qa/desktop-*.png`, `apps/android/qa/evidence/*.png`, `docs/demo/contact-sheet.jpg` | Screenshots of this project's own demo store / DemoShop fixture. | Own work. Product names/prices are fixture data. |
| App icons and glyphs in native/web UI | Drawn in code or emoji/system glyphs (e.g. `demo/store.js` icon field). No icon pack found in dependencies. | Likely own; not verified file by file. |
| Retailer names in code/docs (Shopee, Lazada, TikTok, Amazon) | Used as target-app labels only. `ANDROID-STATUS.md` states no real-retailer integration is claimed. | Nominative use; no endorsement implied. |
| Demo narration | `docs/demo/narration.md` (script). Audio `narration.aiff` and video are gitignored. | Voice source unknown (likely synthetic text-to-speech; not evidenced). |
| Demo video music | None referenced. | n/a |

## AI-generated contributions and tools

Evidence in the repo:

1. **Commit messages:** two commits, no `Co-Authored-By` or AI trailers. Commit messages do not reveal tooling.
2. **Docs wording:** `docs/design/user-approved/README.md` refers to a "generated v2 visual direction" superseded by user-supplied screens. Docs use "coordinator verified" (`docs/feature-matrix.md`), which suggests agent-assisted orchestration. Not conclusive.
3. **Local path hint:** build logs show the project was built from a folder named `.../ChatGPT/ClearChoice`, suggesting a ChatGPT/Codex-style workflow. Inference only; tool and extent unknown.
4. **`README.md:35`** carries an open checklist item to disclose significant AI-generated contributions.
5. **Runtime AI (product feature, not authoring):** `services/ai-gateway/src/server.js` can call an external or local (Ollama) chat-completions provider for layout/review analysis, only with user consent; otherwise local rule-based fallback. Provider and model are set by env vars and are not named in the repo. `docs/feature-matrix.md` says prompt-to-token is a bounded phrase set, not free-form AI.

Needed from the team before submission (unknown to this audit): which AI tools wrote code, docs, design images and narration; rough share of the codebase; what human review/testing was done. Draft statement to adapt:

> Parts of this project's code, documentation and design mock-ups were produced with AI assistance (tools: <fill in>). The team reviewed, tested and is responsible for the submitted work. The optional runtime AI analysis uses <provider/model: fill in> and runs only with explicit user consent.

## Open items

- Confirm authorship and rights of the five design PNGs.
- Add scrcpy source/URL and NOTICE; verify the vendored binary hash against upstream.
- Confirm fonts license choice or self-host.
- Fill in AI tool list above.
- See `docs/qa/secrets-audit.md` for personal-data items.
