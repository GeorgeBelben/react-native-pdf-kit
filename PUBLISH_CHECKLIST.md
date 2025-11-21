# Pre-Publish Checklist for react-native-pdf-kit

## ✅ Completed

### Build & Quality
- [x] TypeScript compilation successful (no errors)
- [x] Bunchee build successful (dist/ folder generated)
- [x] All linting rules passing (Biome configured)
- [x] Dependencies installed and up-to-date
- [x] Build artifacts generated: `dist/index.js`, `dist/index.mjs`, `dist/index.d.ts`

### Code Quality
- [x] Modern function component with `forwardRef`
- [x] Proper TypeScript types with JSDoc comments
- [x] Separate native event types from public API types
- [x] All native code modernized (Kotlin for Android, Objective-C for iOS)
- [x] New Architecture only (no old arch support)
- [x] Biome suppressions properly placed

### Platform Support
- [x] iOS implementation complete (RNPDFView, RNPDFViewManager, PdfKit module)
- [x] Android implementation complete (Kotlin conversion from Java)
- [x] Podspec simplified for new architecture
- [x] AndroidPdfViewer dependency configured

### Documentation
- [x] README.md comprehensive with usage examples
- [x] API documentation in TypeScript interfaces
- [x] Example app functional

## ⚠️ Before Publishing

### Package Configuration
- [x] Update `package.json` version (currently `0.1.0`)
- [x] Update author information in `package.json`
  - Current: `"author": "Your Name <your.email@example.com>"`
  - Update to your actual name and email
- [x] Update repository URLs in `package.json`
  - Current: `https://github.com/yourusername/react-native-pdf-kit.git`
  - Update to your actual GitHub repository
- [x] Verify license file exists (MIT)

### Testing
- [ ] Test in example app on iOS device/simulator
- [ ] Test in example app on Android device/emulator  
- [ ] Verify all PDF loading types work:
  - [ ] URL loading
  - [ ] Base64 loading
  - [ ] File loading
- [ ] Test all callbacks:
  - [ ] `onError`
  - [ ] `onLoad`
  - [ ] `onPageChanged`
  - [ ] `onScrolled`
- [ ] Test `reload()` method via ref

### Build Verification
- [ ] Run `bun install` in example app
- [ ] Run `cd example/ios && pod install`
- [ ] Build iOS example: `cd example && npx react-native run-ios`
- [ ] Build Android example: `cd example && npx react-native run-android`

### npm Publishing
- [ ] Login to npm: `npm login`
- [ ] Dry run: `npm publish --dry-run`
- [ ] Publish: `npm publish` or `npm publish --access public` (if scoped)

## 📦 Package Contents

The published package will include:
- `dist/` - Built JavaScript and TypeScript definitions
- `src/` - Source TypeScript files
- `android/` - Android native code (Kotlin)
- `ios/` - iOS native code (Objective-C)
- `react-native-pdf-kit.podspec` - CocoaPods specification
- `README.md` - Documentation
- `LICENSE` - MIT License

Files excluded (via `.files` in package.json):
- `!android/build`
- `!ios/build`
- `!**/__tests__`
- `!**/__fixtures__`
- `!**/__mocks__`

## 🚀 Post-Publish

- [ ] Tag the release in Git: `git tag v0.1.0 && git push --tags`
- [ ] Create GitHub release with changelog
- [ ] Update README badges if applicable
- [ ] Test installation from npm: `npm install react-native-pdf-kit`

## 📝 Current Status

**Build:** ✅ Passing  
**TypeScript:** ✅ No errors  
**Biome Lint:** ✅ Passing  
**Dependencies:** ✅ Installed  
**Ready to publish:** ⚠️ After updating package.json metadata

---

**Next Steps:**
1. Update author and repository info in `package.json`
2. Test in example app
3. Publish to npm
