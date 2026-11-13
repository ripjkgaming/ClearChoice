# Native control pages: visual and accessibility review

Date: 13 November 2026. Scope: README product blocker 5 (large text, scrolling,
control states, accessibility). Launcher styling is out of scope because phone
home, drawer and frame references are still missing.

## Important limit

**No emulator or adb was available for this review.** Every finding below comes
from reading the code, building the app and doing contrast calculations. None
of the changes were rendered, screenshot-tested or checked with TalkBack. The
screenshots in `docs/qa/ui-v2/` are from **before** this change and were not
regenerated.

## Method

- Compared `docs/design/user-approved/01..05` with `docs/qa/ui-v2/*.png` and
  with the UI code. The UI is built in code in
  `apps/android/clearchoice/src/main/java/com/clearchoice/MainActivity.java`.
  The helpers are in `Design.java`, and there are no XML layouts.
- Earlier unfinished UI work: the git log and working tree had nothing pending.
  The tree was clean at `8c056de`. The last UI commit was `90f5503`.
- Text sizes: all `Design.text` calls use `TextView.setTextSize(float)`, which
  uses sp units. They scale with the system font size.
- Contrast: computed WCAG 2.x ratios for every text/background pair in the
  code, using the code's own colours (results below).

## Fixed (all in `MainActivity.java`)

| Area | Problem | Change |
|---|---|---|
| Large text | Bottom-nav tabs had a fixed height of 56dp. At large font scales the icon, gap and label (10sp) would clip. | Height is now `wrap_content` with a minimum of 56dp. |
| Large text | Activity summary tiles had a fixed height of 72dp and two lines of 13sp text. They would clip at large scales. | Height is now `wrap_content` with a minimum of 72dp. |
| Large text | Status chips ("Unavailable", "Not requested") were capped at 76dp wide, which forced mid-word breaks at large scales. | The width cap now grows with `fontScale`, up to 140dp. |
| Touch target | Pills (Pause/Resume, filter, Add app, Manage/Install, Activity filters, Permissions/Privacy tabs, Clear history) were about 35dp tall. | Touch area is now at least 48x48dp. `InsetDrawable` keeps the visible pill about 36dp tall, so the look matches the approved design. |
| Touch target | The back arrow was 24x28dp. | It is now wrapped in a 48x48dp target. A negative margin keeps the icon in its old position. |
| Touch target | The overflow `⋮` had padding on one side only, which made it less than 48dp. | It is now a centred 48x48dp minimum. |
| Touch target | The header tabs (Manage Apps / Activity / Features) were about 30dp tall. | Minimum is now 48dp. |
| Touch target | Switches had no explicit minimum. | `setMinimumHeight(48dp)` is set. |
| Control states | Clickable rows, pills, cards, tabs and nav items had no pressed or focus feedback. | Added a `RippleDrawable` foreground, masked to each control's shape. This covers both pressed and focused states. Cards use `clipToOutline` so row ripples stay inside the rounded corners. |
| Control states | A disabled switch (Allow app recognition, when the app is not installed) looked the same as an enabled one that was off. | Added `-state_enabled` thumb and track tints. |
| Control states | Selected tab and pill state was only shown by colour. | `setSelected(active)` is set, so TalkBack can announce "selected". |
| contentDescription | Decorative `Design.Icon`s announced internal keys such as "Apps" or "settings". | Icons added through `icon()` are now `IMPORTANT_FOR_ACCESSIBILITY_NO`. |
| contentDescription | The toggle-row switches had no label. | `contentDescription` is now the row title. |
| contentDescription | `⋮`, `⌄`, `＋` and `›` were read as glyphs, and Manage/Install did not name the app. | Labels added: "Settings", "Filter apps: <filter>", "Add app", "Manage/Install <app>". The arrow `›` is hidden from accessibility. The back button is labelled "Back to Manage Apps". |
| Headings | Page and section titles were not marked as headings. | `setAccessibilityHeading(true)` is set on API 28 and higher. |
| Contrast | The active bottom-nav label was `#075cec` on `#e0e3ff`, which is 4.43:1 for 10sp text (below 4.5:1). | Changed to `#0050d8` (5.29:1). |

## Contrast results (calculated, not measured on a device)

muted/background 6.28, muted/card 6.78, muted/status chip 5.80, "Allowed" green
4.89, white on blue pill 5.60, ink on grey pill 15.0, header subtitles 8.1–10.3,
service banner subtitle 7.78, inactive nav 8.66, active nav (after the fix)
5.29. Every text pair is at least 4.5:1. Disabled switch tints are exempt.

## Not changed (deliberate)

- Scrolling: all page content is already in one `ScrollView` with
  `fillViewport`, and the bottom nav sits outside it. No change was needed.
- `Design.java` is shared with `ClearChoiceService` (the overlay). It was not
  changed, so the annotation overlay behaviour is unchanged.
- Illustrative content in the design references (retailer lists, "Enhanced",
  counts, search icon) is not copied. The references' README says runtime
  values must come from device state, and unsupported features must not be
  shown as working.
- Launcher styling is paused.
- Screenshots were not regenerated, because no emulator was available.

## Could not verify without an emulator

1. That nothing clips at font scale 1.3, 1.5 and 2.0 (Settings > Display >
   Font size), especially the header row ("ClearChoice" at 25sp next to `⋮`),
   the Manage Apps rows (icon, name, Manage pill and switch in one row), and the
   counts strip.
2. That bottom-nav labels at 2.0x break across lines. "Permissions" and
   "Manage Apps" are each about 70dp wide per tab, so they may break mid-word.
   The labels grow rather than clip, but this could look poor. One option is
   to cap nav label scaling at about 1.3x; that is a product decision.
3. That ripple masks look right on the inset pills, and that the focus
   highlight shows with a keyboard or D-pad.
4. The TalkBack reading order and announcements. In particular, toggle rows
   expose the title text and the switch as separate focus stops.
5. That the visual size of pills and the back arrow position still match the
   approved references.
6. Real touch-target sizes, using Accessibility Scanner.

## Notes for the reviewer

- The diff is limited to `MainActivity.java`. It is single-line, minified-style
  code, so use `git diff --word-diff` to read it.
- New helpers: `touch(View,int radius)` (ripple, 48dp minimum, focusable) and
  `heading(TextView)`. `pill()` sets the ripple and inset itself, because its
  background is inset.
- Please check these points:
  - The `InsetDrawable` foreground on pills. The ripple bounds should match the
    inset background.
  - `bp.leftMargin=-d(12)` on the back target. The hero has 16dp padding, so
    the target should not go off-screen.
  - `setClipToOutline(true)` on `card()`. It needs the `GradientDrawable`
    background outline, which is present.
- Behaviour is unchanged: no click handlers, preference keys or text strings
  were changed. Only the contentDescriptions are new.
- Verification run: `./gradlew :clearchoice:assembleDebug` passed
  (JAVA_HOME=~/.local/jdk21). `run-price-evidence-tests.sh` reported
  "29 price evidence tests passed". Those tests do not cover the UI.

## Fable review

Date: 13 November 2026. Second pass over commit `aa520e1`, against the same
five approved references.

### Important limit

**No emulator or device was available** (`adb devices` listed nothing). Nothing
below was rendered, screenshot-tested or checked with TalkBack. Widths quoted
are estimates from Roboto glyph metrics on a 360dp-wide phone, not
measurements. The screenshots in `docs/qa/ui-v2/` are still from before both
review passes. The committed `apps/android/ClearChoice-debug.apk` was not
rebuilt, so it does not contain these changes.

### Decision: bottom-nav labels at 2x font

**Cap the scaling, and wrap at spaces only.** Each of the five tabs has about
64dp for its label on a 360dp phone. "Permissions" at 10sp is already about
55dp wide, so at 2.0x (about 110dp) it cannot fit and Android would split it
mid-word. Letting labels grow freely is therefore not an option.

- Nav labels follow the system font size up to 13dp (1.3x), then stop.
- If the longest word still does not fit the tab, all five labels shrink
  together until it does. Labels never break inside a word.
- "Manage Apps" may wrap at the space. When any label needs two lines, every
  label reserves two lines so the icons stay on one row.
- Each tab has a long-press/hover tooltip with its name (`setTooltipText`),
  and TalkBack still reads the full label.

Trade-off, stated plainly: nav labels do not reach 200% text size, so this
bar alone does not meet WCAG 1.4.4. All page content still scales fully. This
is the same compromise platform bottom bars make.

### Fixed (all in `MainActivity.java`)

| Area | Problem | Change |
|---|---|---|
| Large text | The "ClearChoice" wordmark (25sp), hero page titles (27sp, "Permissions & privacy"), hero tabs, the counts strip and the Activity summary tiles would split words at 1.5x–2.0x. | New `fit(width, labels...)` helper shrinks a group of labels together until the longest word fits. It does nothing at normal sizes. |
| Large text | Manage Apps rows keep icon, name, Manage pill and switch on one line. At 2.0x the name had about 80dp left. | At font scale 1.3 and above the pill and switch move to a second line, right-aligned, inside the same card. |
| Large text | Rows with a status chip ("Unavailable") left about 124dp for a 30sp title at 2.0x. | At 1.3 and above the chip sits under the description instead of beside it. |
| Large text | The service banner squeezed its text beside the Pause pill. | At 1.3 and above the pill sits under the text, full width. |
| Large text | Activity filter pills overflowed the row at 2.0x. | At 1.3 and above they stack. |
| Large text | The `⋮` and `›` glyphs doubled in size at 2.0x. | They are icons, so they now have a fixed 25dp size. The touch targets are unchanged. |
| Hierarchy | Counts strip and Activity tiles used one bold weight for number and label. The references show a strong number and a quiet label. | Number is bold and 1.4x; label is regular and muted (`#535b70` on white, 6.78:1; `#c7d2ed` on `#293653`, about 8:1). The service tile's status text stays at label size. |
| Hierarchy | The active hero tab was marked by colour and weight only. The reference has an underline. | Added a 3dp underline (`#8f8cff`) on the active tab. |
| Hierarchy | Every Activity record was its own card. The reference groups records under each date. | One card per day, with dividers between records. |
| Spacing | The last Activity summary tile had a trailing 5dp margin, so the row did not align with the right edge; tile heights could differ. | Margin only between tiles (6dp); tiles share the row height. |
| Spacing | Toggle-row text could touch the switch. | Added an 8dp gap. |
| Accessibility | Toggle rows had two focus stops: the text, then the switch (open point 4 of the first review). | The text is hidden from accessibility and the switch description is "title. detail". One stop, with the on/off and disabled state announced by the switch. |

### Not changed

- No strings, click handlers or preference keys changed. `Design.java`, the
  overlay service, parser, build files and README are untouched.
- The Manage Apps filter row ("All apps" / "Add app") can wrap "Add app" onto
  two lines inside its pill at 2.0x. It wraps at the space, so it was left.
- "Clear local history" is still shown when the list is empty.
- `fit()` uses the full display width. In landscape with side system bars it
  overestimates by the inset; landscape has more room, so this is low risk.
- Launcher styling is still paused.

### Still needs a device

1. Everything in "Could not verify" from the first review, except point 2
   (decided above) and the toggle-row part of point 4 (changed above, but the
   announcement itself is unheard).
2. The stacked layouts at 1.3x, 1.5x and 2.0x, and that the 1.3 threshold is
   the right place to switch.
3. That the hero-tab underline sits on the bottom edge of the tab and that the
   two-weight counts and tiles look right.
4. Nav labels at 1.3x on a narrow (320dp) screen.

### Verification run

`./gradlew :clearchoice:assembleDebug` passed (JAVA_HOME=~/.local/jdk21,
ANDROID_HOME=~/android-sdk). That proves the code compiles and packages; it
does not prove the layout. No UI tests exist, and none were added.
