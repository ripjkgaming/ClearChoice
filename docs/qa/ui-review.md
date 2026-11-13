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

## Purple restyle

The native control center was restyled to match the look of
`docs/qa/desktop-shopping.png`. All six pages changed: Manage Apps, app
settings, Features, Activity, Permissions and Settings. No behaviour, strings,
click handlers, preference keys or data sources changed. No Demo Shop,
Shopping Mode or fixture data was added back. The overlay service still uses
the original light `Design` constants, so it is unchanged.

### What changed

- **Page background.** Near-black `#0D0B12` with two faint violet radial glows,
  one at the top centre and one at the lower left (`Design.night`). The navy
  hero gradient and the light rounded body sheet are gone. Window, status bar
  and navigation bar are `#0D0B12`, and the light-bar icon flags are cleared so
  the system icons draw light.
- **Cards.** Dark translucent fill `#E01E1A2A` (88% alpha), 16dp radius, 1dp
  border `#1FFFFFFF` (`Design.surface`). This covers app rows, groups, the
  counts strip, the service banner, the app header, the Activity tiles and the
  privacy note. Group header strips use a 5% white wash.
- **Text.** Titles `#F5F2FA` bold. Secondary text `#ACA6BB`.
- **Values.** Counts on Manage Apps and the Activity summary tiles are lilac
  `#C9A0FF`, with muted labels. Row icons are lilac.
- **Buttons.** Primary buttons use a violet `#8247E6` to pink `#AD3A96`
  gradient with white text: "Add app", "Install", the active hero tab, the
  active Activity filter and the active Permissions/Privacy tab. Secondary
  buttons are dark grey `#2B2638` with a thin `#38FFFFFF` border: "Manage",
  "Pause"/"Resume", the app filter, inactive filters and "Clear local
  history". The button radius is now 14dp (it was a 24dp pill).
- **Chips.** Status chips are small rounded chips with a thin border:
  `#DDD7EA` on `#2B2638`, and "Allowed" in green `#8DE5AF` on `#17382A`.
- **Navigation.** The hero tabs are a pill segmented control (`#17141F`
  track, gradient active pill). The bottom navigation is a floating pill bar
  with 10dp side margins. The active item has a `#3A2A5A` pill and a `#DCC3FF`
  icon and label.
- **Switches.** The on track is `#8B5CF6`, the off track `#6E6882` and the thumb
  white. Disabled switches use a `#6B6578` thumb on a `#2E2A3A` track.
- **Dialogs.** All dialogs use a new `ClearChoiceDialog` style
  (`res/values/styles.xml`). It is based on `Theme.Material.Dialog.Alert`, with
  a `#1F192D` background and lilac buttons.
- **Press feedback.** Ripples are lilac at 20% alpha (`#33C9A0FF`).

### Kept from earlier passes

The changes below were made without touching these:

- 48dp minimum touch targets (`touch()`, `pill()`, switches, nav items).
- All `contentDescription` and tooltip text.
- Accessibility headings and the single focus stop on toggle rows.
- The `fit()` shrink-to-fit labels and the stacked layouts at font scale 1.3
  and above.
- The two-line nav labels.

### Contrast (WCAG 2.x, computed)

Ratios were computed from the code colours. Where a colour is translucent, it
was first blended onto what is beneath it. "Glow" means the brightest point of
both glows stacked (`#2F1957`); this is the worst case for text on the page.
The card fill over the glow blends to about `#1F192D`–`#201A2F`.

| Pair | Ratio |
|------|-------|
| Title `#F5F2FA` on page `#0D0B12` / glow `#2F1957` | 17.66 / 13.57 |
| Secondary `#ACA6BB` on page / glow | 8.31 / 6.39 |
| Lilac `#C9A0FF` on page / glow | 9.27 / 7.12 |
| Title on card `#1F192D` | 15.35 |
| Secondary on card / on group header `#2A2438` | 7.23 / 6.34 |
| Lilac values on card / on group header | 8.06 / 7.07 |
| White on gradient ends `#8247E6` / `#AD3A96` | 5.26 / 5.49 |
| Title on secondary button `#2B2638` | 13.20 |
| Chip text `#DDD7EA` on `#2B2638` | 10.43 |
| "Allowed" `#8DE5AF` on `#17382A` | 8.54 |
| Inactive nav and tab text `#ACA6BB` on `#17141F` | 7.72 |
| Active nav `#DCC3FF` on `#3A2A5A` | 8.01 |
| Dialog buttons `#C9A0FF` on `#1F192D` | 8.06 |
| Green banner icon `#8DE5AF` on card | 11.54 |

Non-text contrast (target 3:1):

| Pair | Ratio | Result |
|------|-------|--------|
| Switch on track `#8B5CF6` / off track `#6E6882` vs card | 4.02 / 3.21 | Passes |
| White thumb on on track / on off track | 4.23 / 5.30 | Passes |
| Gradient button ends vs card | 3.23 / 3.09 | Passes |
| Active gradient tab vs `#17141F` track | 3.45 / 3.31 | Passes |
| Secondary button border vs card | about 2.0 | Below 3:1 |
| Active nav pill `#3A2A5A` vs `#17141F` bar | 1.43 | Below 3:1 |

The two pairs below 3:1 are not the only cue:

- Secondary buttons carry a text label, and their fill differs from the card.
- The active nav item also changes its icon and label colour (lilac against
  grey) and its label weight (bold), and it reports the selected state.

The disabled switch (`#6B6578` thumb on `#2E2A3A`, 2.49) is an inactive
control, so WCAG does not require 3:1 for it. The existing Shopee tile ("S" in
white on `#F45324`, 3.44) is large bold text and was not changed.

### Not verified

No emulator or device was available, so nothing was rendered. Everything below
is unverified:

1. The overall look against the reference, including how strong the glow is,
   the card translucency and the gradient.
2. That the system bar icons draw light on API 26–29 and on API 30 and later.
3. Whether a white window flashes at launch. The manifest theme is still
   `Theme.Material.Light.NoActionBar`. It was left alone to keep the change to
   UI code. The code sets a dark window background, but only after the activity
   starts.
4. How the dark dialog style looks with the app picker and the list dialog.
5. Switch tints on OEM skins.
6. The floating nav bar with gesture navigation and with three-button
   navigation.
7. Large-text layouts at 1.3x, 1.5x and 2.0x after the radius and padding
   changes. The hero tab label width now subtracts 12dp per tab for the track
   padding.
8. TalkBack announcements. No accessibility attributes were changed.

### Verification run

`./gradlew :clearchoice:assembleDebug` passed (JAVA_HOME=~/.local/jdk21,
ANDROID_HOME=~/android-sdk). This proves the code compiles and packages. It
does not prove the layout. Screenshots were not regenerated.

## Liquid glass buttons

All buttons now follow the `liquid-glass-design` skill
(`.agents/skills/liquid-glass-design/SKILL.md`). iOS uses the real iOS 26
Liquid Glass APIs. Android has no equivalent API, so it uses the skill's design
rules drawn with ordinary Canvas APIs.

### iOS (`apps/ios/ClearChoice/App.swift`)

- `View.glassButton(prominent:tint:)` is the single entry point for buttons. On
  iOS 26 and later it applies `.buttonStyle(.glass)` (secondary) or
  `.buttonStyle(.glassProminent)` with a violet tint (primary). On iOS 17–25 it
  falls back to `.bordered` / `.borderedProminent`.
- The hero "Launch Shopping Mode" button (`GlowButton`) uses
  `.glassEffect(.regular.tint(violet 85%).interactive(), in: .rect(cornerRadius: 15))`
  on iOS 26. Older iOS keeps the gradient fill.
- `GlassGroup` wraps sibling glass buttons in a `GlassEffectContainer(spacing: 8)`
  on iOS 26. It is used for the View/Add pair on each demo-store row.
- Buttons converted: Manage ›, Pause/Resume, Reset local preferences, Apply
  prompt, Studio "Add to cart", Save profile, demo-store View/Add, and
  product-sheet "Add to cart".
- Two kinds of control were left as they were on purpose:
  - The shopping-mode radio rows are list rows. The skill says not to put glass
    on every view.
  - The toolbar "Done" item gets the system's shared toolbar glass on iOS 26.
- The tint changed from system `.purple` (`#BF5AF2` in dark mode) to `#8247E6`.
  White text on `#BF5AF2` is only 3.52:1, so this change also fixes the old
  `.borderedProminent` fallback. White on `#8247E6` is 5.26:1 opaque, and about
  6.2–6.8:1 when tinted at 85% over dark backgrounds. The real glass mixes in
  whatever is behind it, so these iOS numbers are estimates.
- The glass code is behind `#available(iOS 26, *)` and `#if compiler(>=6.2)`, so
  Xcode 16 and older SDKs still build the fallback. The deployment target stays
  17.0.

### Android (`Design.java`, `MainActivity.java`, `ClearChoiceService.java`, `styles.xml`, `drawable/glass_dialog_button.xml`)

- `Design.glass(view, style, radiusDp, insetDp)` is the single shared button
  factory. It sets the glass background, a ripple clipped to the shape, a
  48dp minimum width and height, clickable/focusable, and the label colour.
  `Design.button` (QA overlay) and `MainActivity.pill` both go through it. The
  old `Design.primary`, `Design.raised` and `MainActivity.fill` were removed.
- `Design.Glass` is a stateful `Drawable` with these layers:
  1. Translucent tinted fill. Regular glass is `#8B5CF6` at 18%, or 30% when
     selected. Prominent glass is a `#8247E6` to `#AD3A96` gradient at 92%.
  2. A soft inner highlight: white 8% fading to 0 over the top 55%.
  3. A 1dp top-lit specular rim: a white gradient from 70% at the top to 40% at
     the bottom.
  4. A 2dp lilac `#C9A0FF` focus ring when the control has keyboard or D-pad
     focus.
- When pressed, the rim brightens (90% to 50%), prominent glass darkens by 12%
  black, and the ripple plays. Disabled controls draw at 45% alpha.
- **No blur.** Android has no backdrop blur for views inside a window.
  `RenderEffect` blurs the view itself, not what is behind it, so it was not
  used. The "glass" here is translucency plus light effects only.
- `Design.glassBar` is non-interactive regular glass, the stand-in for
  `GlassEffectContainer`. It is used for the hero tab track and the bottom nav
  bar. Children inside a bar stay clear so glass is not nested. Only the
  selected child gets glass: a prominent glass segment in the tab track, and a
  regular selected glass lens in the nav bar.
- Converted: every pill (Pause/Resume, filter, ＋ Add app, Manage/Install,
  activity filters, Clear local history, Permissions/Privacy tabs), the hero
  tab track and selected tab, the nav bar and its active item, the back and ⋮
  icon buttons (48dp glass circles), and the QA overlay buttons. The overlay
  uses light-glass variants, and its "Add to cart" uses light prominent glass.
- Dialog buttons use `ClearChoiceDialog.GlassButton`. It is regular glass in
  XML: a gradient rim shape under an inset fill. Because the fill must be opaque
  to cover the rim layer, it is pre-composited against the dialog background
  `#1F192D`: `#322551`, with the highlight at `#42365F`.
- List rows and app cards keep their ripple-only treatment. They are rows, not
  buttons.
- Large text still wraps. No `maxLines` or fixed height was added to buttons.
  Content descriptions are unchanged.

### Contrast (Android, computed)

Method: WCAG 2.x relative luminance. Each layer is alpha-composited over the
real background. "Highlight" is the brightest point, at the top of the inner
highlight, and is the worst case for light text. The backgrounds are page
`#0D0B12`, page under the top glow `#251542`, card `#1C1827`/`#1F192D`, nav
`#17141F`, and dialog `#1F192D`. The script is
`~/work/agents/ui-glass/calc*.py`, outside the repo.

Text (target 4.5:1). Each cell is the worst case across all backgrounds.

| Pair | Rest | Highlight | Pressed (ripple peak) |
|------|------|-----------|-----------------------|
| `#F5F2FA` on regular glass | ≥ 11.8 | ≥ 9.64 | ≥ 5.88 (selected) |
| White on prominent glass, violet / pink end | ≥ 5.76 / 6.04 | ≥ 4.96 / 5.20 | ≥ 4.71 / 4.93 |
| Selected nav `#DCC3FF` on selected glass in nav bar | 5.20 | 4.82 (under top glow) | 3.63 |
| Inactive tab/nav `#ACA6BB` on glass bar | 5.69 | 4.54 | 3.15 |
| Dialog `#C9A0FF` on dialog glass | — | 5.17 | 4.14 |
| QA overlay `#5642FF` on light glass | 4.65 | 5.22 | 3.72 |
| QA overlay white on light prominent, violet / pink | 4.91 / 5.16 | (no highlight) | 5.99 / 6.45 |

All rest and highlight values pass 4.5:1. The worst is 4.54, for inactive tab
labels under the top glow. Values in the pressed column drop below 4.5:1 only
for the length of the press ripple, at its peak. The ripple colour
(`Design.RIPPLE`, 20% lilac) is the same one the app used before. WCAG
measures contrast at rest, but these numbers are listed so nothing is hidden.

Non-text (target 3:1):

| Pair | Ratio | Result |
|------|-------|--------|
| Rim bottom (white 40%) vs every background | ≥ 3.72 | Passes |
| Rim top (white 70%) vs every background | ≥ 8.66 | Passes |
| Pressed rim bottom (white 50%) | ≥ 5.04 | Passes |
| Focus ring `#C9A0FF` vs every background | ≥ 7.87 | Passes |
| Dialog button rim vs `#1F192D` | ≥ 3.78 | Passes |
| QA overlay rim (`#5642FF` 80%) vs `#F5F6FC` | 3.82 | Passes |
| QA overlay focus ring `#5642FF` vs `#F5F6FC` | 5.38 | Passes |
| Prominent fill alone vs background | 2.75–3.31 | Below 3:1 under the glow, but the rim gives the boundary |
| Regular fill alone vs background | 1.20–1.47 | Below 3:1, but the rim gives the boundary |

The earlier failures are now fixed by the rim. "Secondary button border vs
card, about 2.0" now has a rim of at least 3.72:1. "Active nav pill vs bar,
1.43" now has a selected lens with the same rim.

### Not verified

- **iOS: not compiled and not rendered.** No Xcode or Swift toolchain was
  available. These points need a check on Xcode 26:
  - the `.glass` / `.glassProminent` / `.glassEffect(..., in: .rect(cornerRadius:))` /
    `GlassEffectContainer` signatures;
  - `if #available` inside the `@ViewBuilder` `ButtonStyle.makeBody`;
  - the `#if compiler(>=6.2)` proxy for "iOS 26 SDK present";
  - the real glass contrast on the `.ultraThinMaterial` hero card.
- **Android: compiled, not rendered.** No emulator or device was available.
  Nothing below was checked on screen:
  - how the rim, highlight and focus ring look;
  - ripple clipping inside the 6dp-inset pills;
  - the dialog button layout in the system alert layout;
  - nav and tab layouts at large font scales;
  - OEM dialog themes that ignore `buttonBarButtonStyle`.
- The contrast numbers come from the formulas above, not from screenshots.
  Anti-aliased rim pixels will be a little lower than the computed values.

### Verification run

`./gradlew :clearchoice:assembleDebug` passed (JAVA_HOME=~/.local/jdk21,
ANDROID_HOME=~/android-sdk).
