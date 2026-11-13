# Fresh-clone build verification

Date: 2026-11-13. Clone: `git clone https://github.com/Atharvahuja13/ClearChoice ~/work/fresh-clone` (default branch `main`, HEAD `90f5503`, identical to phase1 HEAD at start).

## Results

| Step | Command | Result |
|------|---------|--------|
| 1 | `npm ci` | PASS (exit 0): `added 4 packages, and audited 5 packages`; npm reports 1 high severity vulnerability (not investigated) |
| 2 | `npm test` | PASS (exit 0): `# tests 16`, `# pass 16`, `# fail 0` |
| 3 | `npm run build` | PASS (exit 0): `Packaged shared ClearChoice engine and browser adapter for the MV3 extension.`, then `npm run check` (node --check on 6 files) clean |
| 4 | `run-price-evidence-tests.sh` | FAIL with default PATH (exit 127): `line 8: javac: command not found` (only JREs under /usr/lib/jvm). PASS (exit 0) with `JAVA_HOME=~/.local/jdk21`: `17 price evidence tests passed` |
| 5 | `cd apps/android && ./gradlew --no-daemon :clearchoice:assembleDebug` (JDK 21.0.12.1, `ANDROID_HOME=~/Android/Sdk`) | PASS (exit 0): `BUILD SUCCESSFUL in 16s`, `31 actionable tasks: 31 executed`; only warnings (Java source/target 8 obsolete) |

Note: step 4 and 5 needed `JAVA_HOME=$HOME/.local/jdk21`; the system `java` is a JRE only (OpenJDK 25), so a JDK is a prerequisite not documented as satisfied by default.

## APK hash

- Freshly built APK: `~/.gradle/clearchoice-build/clearchoice/outputs/apk/debug/clearchoice-debug.apk` (66794 bytes)
  - SHA-256 `21082609b1b59cf6ddc39a9daa781cff5586ac704cdb9648daf15c0b5a89a693`
- README hash `59cac52a...443b37` does NOT match the freshly built APK. Debug builds are not reproducible bit-for-bit (this is the likely cause; not verified).
- README hash DOES match the committed file `apps/android/ClearChoice-debug.apk` in the clone (`59cac52aa568655db98fc3d03fa504890451135baa9414246a4f38a3eb443b37`). So the README hash describes the committed APK, not a fresh build output.

## Caveats

- `apps/android/build.gradle` sets the build dir to `~/.gradle/clearchoice-build/<project>`, a path shared with any other checkout. This build wrote its output there. No source files in ~/work/ClearChoice, the emulator, or the adb server were touched.
- Gradle was run with `--no-daemon`.
