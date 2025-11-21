# react-native-pdf-kit

A modern React Native PDF viewer module with native iOS and Android support. View PDFs from URLs, base64 data, or local files with a simple, performant component.

## Features

- 📱 Native iOS and Android support
- 🌐 Load PDFs from URLs, base64, or local files
- 🎨 Customizable fade-in animations
- 📄 Page change callbacks
- 🔄 Reload functionality
- ⚡ Built with modern tooling (Bun, Bunchee, Biome)
- 📦 TypeScript support with full type definitions
- 🎯 Zero dependencies (peer dependencies only)

## Installation

```sh
npm install react-native-pdf-kit
# or
yarn add react-native-pdf-kit
# or
bun add react-native-pdf-kit
```

### iOS

```sh
cd ios && pod install
```

## Usage

### Basic Example

```jsx
import React from 'react';
import { StyleSheet, View } from 'react-native';
import PDFView from 'react-native-pdf-kit';

export default function App() {
  return (
    <View style={styles.container}>
      <PDFView
        style={styles.pdf}
        resource="https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        resourceType="url"
        onLoad={() => console.log('PDF loaded')}
        onError={(error) => console.error('Error:', error)}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  pdf: { flex: 1 },
});
```

### Load from Base64

```jsx
<PDFView
  style={styles.pdf}
  resource="JVBERi0xLjcKCjEgMCBvYmogICUgZW50..."
  resourceType="base64"
  textEncoding="utf-8"
/>
```

### Load from Local File

```jsx
// iOS - from bundle
<PDFView
  style={styles.pdf}
  resource="sample.pdf"
  resourceType="file"
  fileFrom="bundle"
/>

// Android - from absolute path
<PDFView
  style={styles.pdf}
  resource="/sdcard/Download/sample.pdf"
  resourceType="file"
/>
```

### With Custom Headers

```jsx
<PDFView
  style={styles.pdf}
  resource="https://example.com/protected.pdf"
  resourceType="url"
  urlProps={{
    method: 'GET',
    headers: {
      'Authorization': 'Bearer token123',
    },
  }}
/>
```

### With Reload Functionality

```jsx
import React, { useRef } from 'react';
import PDFView from 'react-native-pdf-kit';

function MyComponent() {
  const pdfRef = useRef();

  const handleReload = () => {
    pdfRef.current?.reload();
  };

  return (
    <>
      <Button title="Reload" onPress={handleReload} />
      <PDFView
        ref={pdfRef}
        style={styles.pdf}
        resource="https://example.com/document.pdf"
        resourceType="url"
      />
    </>
  );
}
```

## API Reference

### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `resource` | `string` | **required** | The PDF resource (URL, base64 data, or file path) |
| `resourceType` | `'url' \| 'base64' \| 'file'` | `'url'` | Type of resource being loaded |
| `style` | `ViewStyle` | - | Style for the PDF container |
| `onLoad` | `() => void` | - | Called when PDF loads successfully |
| `onError` | `(error: { message: string }) => void` | - | Called when an error occurs |
| `onPageChanged` | `(page: number, pageCount: number) => void` | - | Called when the page changes |
| `onScrolled` | `(offset: number) => void` | - | Called on scroll (0 = top, 1 = bottom) |
| `fadeInDuration` | `number` | `0` | Fade-in animation duration in ms |
| `urlProps` | `PDFViewUrlProps` | - | Additional props for URL requests |
| `textEncoding` | `'utf-8' \| 'utf-16'` | `'utf-8'` | Text encoding for base64 (iOS only) |
| `fileFrom` | `'bundle' \| 'documentsDirectory' \| 'libraryDirectory' \| 'cachesDirectory' \| 'tempDirectory'` | `'bundle'` | iOS file location (iOS only) |
| `enableAnnotations` | `boolean` | `false` | Enable PDF annotations (Android only) |

### PDFViewUrlProps

| Prop | Type | Description |
|------|------|-------------|
| `method` | `string` | HTTP method (e.g., 'GET', 'POST') |
| `headers` | `{ [key: string]: string }` | HTTP headers |
| `body` | `string` | HTTP request body |

### Methods

#### `reload(): Promise<void>`

Reloads the PDF document. Must be called via ref.

```jsx
const pdfRef = useRef();
await pdfRef.current?.reload();
```

## Platform-Specific Notes

### iOS
- Uses WKWebView for PDF rendering
- Supports all resource types (url, base64, file)
- File paths can be relative (from bundle) or from specific directories
- Requires iOS 13.0 or higher

### Android
- Uses [AndroidPdfViewer](https://github.com/mhiew/AndroidPdfViewer)
- Supports all resource types (url, base64, file)
- Requires minSdkVersion 23 or higher
- Annotations support available with `enableAnnotations` prop

### Customizing Android PDF Viewer Version

You can override the PDF viewer version in your `android/build.gradle`:

```gradle
buildscript {
  ext {
    pdfViewerVersion = "3.2.0-beta.1"
    pdfViewerRepo = "com.github.mhiew"
  }
}
```

## Development

This module uses modern tooling for development:

- **Bun** - Fast package manager and runtime
- **Bunchee** - Zero-config bundler for building the module
- **Biome** - Fast formatter and linter
- **TypeScript** - Type safety

### Setup

```sh
# Install dependencies
bun install

# Build the module
bun run build

# Watch mode for development
bun run dev

# Run linter
bun run lint

# Fix linting issues
bun run lint:fix

# Format code
bun run format

# Type check
bun run typecheck
```

### Example App

The `example` folder contains a React Native app to test the module:

```sh
cd example
bun install

# Run on iOS
bun run ios

# Run on Android
bun run android
```

## Migrated from react-native-view-pdf

This module is a modernized version of [react-native-view-pdf](https://github.com/rumax/react-native-PDFView), updated with:

- ✅ Modern React Native compatibility (0.76+)
- ✅ TypeScript with full type definitions
- ✅ Kotlin for Android (from Java)
- ✅ Modern build tooling (Bunchee, Biome, Bun)
- ✅ Updated dependencies and gradle configuration
- ✅ Improved developer experience

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository.

## License

MIT

---

Made with ❤️ and modern tooling
