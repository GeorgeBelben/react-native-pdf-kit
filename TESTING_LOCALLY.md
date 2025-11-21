# Testing react-native-pdf-kit Locally

## Option 1: Use the Example App (Recommended)

The example app is already configured to use your local package via `"react-native-pdf-kit": "link:.."`.

### Steps:
```bash
# 1. Build the package first
cd /Users/georgebelben/Development/react-native-pdf-kit
bun run build

# 2. Install example dependencies
cd example
bun install

# 3. Install iOS pods
cd ios
pod install
cd ..

# 4. Run on iOS
npx react-native run-ios

# 5. Run on Android
npx react-native run-android
```

This tests the **actual built package** from `dist/` just like npm would serve it.

---

## Option 2: npm pack + Test in Another Project

Create a tarball and install it in a test React Native app:

```bash
# 1. Build your package
cd /Users/georgebelben/Development/react-native-pdf-kit
bun run build

# 2. Create a tarball (like npm publish does)
npm pack
# This creates: react-native-pdf-kit-0.1.0.tgz

# 3. In a separate test React Native app
cd /path/to/test-app
npm install /Users/georgebelben/Development/react-native-pdf-kit/react-native-pdf-kit-0.1.0.tgz

# 4. Run the app
npx react-native run-ios
npx react-native run-android
```

### What `npm pack` does:
- ✅ Runs `prepare` script (builds the package)
- ✅ Includes only files from `files` array in package.json
- ✅ Creates exact same structure as `npm publish`
- ✅ Shows what will be published

---

## Option 3: Verdaccio (Local npm Registry)

For testing the full publish/install workflow:

```bash
# 1. Install Verdaccio
npm install -g verdaccio

# 2. Start local registry (runs on http://localhost:4873)
verdaccio

# 3. In new terminal, point npm to local registry
npm set registry http://localhost:4873/

# 4. Create user and login
npm adduser --registry http://localhost:4873

# 5. Publish to local registry
cd /Users/georgebelben/Development/react-native-pdf-kit
npm publish

# 6. Install in test app
cd /path/to/test-app
npm install react-native-pdf-kit

# 7. Reset npm registry when done
npm set registry https://registry.npmjs.org/
```

---

## Option 4: Bun Link (Quick Testing)

Symlink the package globally:

```bash
# 1. In your package directory
cd /Users/georgebelben/Development/react-native-pdf-kit
bun link

# 2. In test app
cd /path/to/test-app
bun link react-native-pdf-kit

# 3. Run pod install for iOS
cd ios && pod install && cd ..

# 4. Run the app
npx react-native run-ios
```

⚠️ **Warning**: `bun link` uses symlinks, which can behave differently than a real install. Use for quick iteration only.

---

## 🎯 Recommended Testing Workflow

### For Your Package (Best Approach):

1. **Use the example app** - It's already set up!
   ```bash
   cd example
   bun install
   cd ios && pod install && cd ..
   npx react-native run-ios
   ```

2. **Test actual package structure** with `npm pack`:
   ```bash
   cd /Users/georgebelben/Development/react-native-pdf-kit
   npm pack
   tar -tzf react-native-pdf-kit-0.1.0.tgz  # See what will be published
   ```

3. **Verify package contents** before publishing:
   ```bash
   npm publish --dry-run
   ```

---

## ✅ What to Test

- [ ] PDF loads from URL
- [ ] PDF loads from base64
- [ ] PDF loads from file
- [ ] `onLoad` callback fires
- [ ] `onError` callback fires (try invalid URL)
- [ ] `onPageChanged` callback fires when scrolling
- [ ] `onScrolled` callback fires
- [ ] `reload()` method works via ref
- [ ] Both iOS and Android work
- [ ] TypeScript types work (no errors in your app)
- [ ] Build succeeds: `cd ios && xcodebuild -workspace ...` (iOS) and `cd android && ./gradlew build` (Android)

---

## 🚀 When to Publish

**Publish when:**
- ✅ Example app runs successfully on both platforms
- ✅ All callbacks work as expected
- ✅ No build errors
- ✅ TypeScript types work correctly
- ✅ `npm pack` shows expected files

**Consider versioning:**
- Use `0.1.0-beta.1` for initial testing
- Use `0.1.0` for first stable release

```bash
# Publish beta for testing
npm version 0.1.0-beta.1
npm publish --tag beta

# Users can install with: npm install react-native-pdf-kit@beta

# Later publish stable
npm version 0.1.0
npm publish
```

---

## My Recommendation

**Start with Option 1** (example app) since you already have it set up:

```bash
# Quick test
cd /Users/georgebelben/Development/react-native-pdf-kit/example
bun install
cd ios && pod install && cd ..
npx react-native run-ios  # Test on iOS simulator

# If that works, try Android
npx react-native run-android
```

If the example app works perfectly, you can confidently publish! The `link:..` setup means it's using your actual built `dist/` files, which is what npm will serve.
