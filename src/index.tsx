import { forwardRef, useCallback, useImperativeHandle, useRef } from 'react';
import {
  NativeModules,
  Platform,
  UIManager,
  findNodeHandle,
  requireNativeComponent,
} from 'react-native';
import type { HostComponent, StyleProp, ViewStyle } from 'react-native';

export type ResourceType = 'url' | 'base64' | 'file';

export type FileLocation =
  | 'bundle'
  | 'documentsDirectory'
  | 'libraryDirectory'
  | 'cachesDirectory'
  | 'tempDirectory';

export type TextEncoding = 'utf-8' | 'utf-16';

export interface UrlRequestOptions {
  /**
   * HTTP method to use. Defaults to GET if not specified.
   */
  method?: string;

  /**
   * HTTP headers to send along with the request.
   */
  headers?: Record<string, string>;

  /**
   * HTTP body to send with the request. This must be a valid
   * UTF-8 string, and will be sent exactly as specified, with no
   * additional encoding (e.g. URL-escaping or base64) applied.
   */
  body?: string;
}

interface NativePdfViewProps {
  style?: StyleProp<ViewStyle>;
  onError?: (event: { nativeEvent: { message: string } }) => void;
  onLoad?: () => void;
  onPageChanged?: (event: {
    nativeEvent: { page: number; pageCount: number };
  }) => void;
  onScrolled?: (event: { nativeEvent: { offset: number } }) => void;
  resource: string;
  resourceType?: ResourceType;
  fileFrom?: FileLocation;
  urlProps?: UrlRequestOptions;
  textEncoding?: TextEncoding;
  fadeInDuration?: number;
  enableAnnotations?: boolean;
}

export interface PdfViewProps {
  /**
   * Style for the PDF viewer container.
   */
  style?: StyleProp<ViewStyle>;

  /**
   * Callback invoked when the PDF fails to load.
   */
  onError?: (error: { message: string }) => void;

  /**
   * Callback invoked when the PDF loads successfully.
   */
  onLoad?: () => void;

  /**
   * Callback invoked when the current page changes.
   * @param page - The current page number (0-indexed)
   * @param pageCount - Total number of pages in the document
   */
  onPageChanged?: (page: number, pageCount: number) => void;

  /**
   * Callback invoked when the document is scrolled.
   * @param offset - Scroll position (0 = top, 1 = bottom)
   */
  onScrolled?: (offset: number) => void;

  /**
   * The PDF resource to render. Can be:
   * - URL: "https://example.com/document.pdf"
   * - Base64 string: "JVBERi0xLjcKCjEgMCBvYmogICUgZW50..."
   * - File path (iOS): "document.pdf" or (Android): "/sdcard/Download/document.pdf"
   */
  resource: string;

  /**
   * Type of resource being loaded.
   * @default 'url'
   */
  resourceType?: ResourceType;

  /**
   * iOS only: Location to load the file from.
   * @default 'bundle'
   */
  fileFrom?: FileLocation;

  /**
   * Additional options for URL requests (headers, method, body).
   */
  urlProps?: UrlRequestOptions;

  /**
   * iOS only: Text encoding for base64 content.
   * @default 'utf-8'
   */
  textEncoding?: TextEncoding;

  /**
   * iOS only: Duration in milliseconds for the fade-in animation.
   * @default 0
   */
  fadeInDuration?: number;

  /**
   * Android only: Enable PDF annotations rendering.
   * @default false
   */
  enableAnnotations?: boolean;
}

export interface PdfViewRef {
  /**
   * Reloads the PDF document.
   * @throws Error if the component is not mounted or ref is invalid
   */
  reload(): Promise<void>;
}

const NativePdfView: HostComponent<NativePdfViewProps> = requireNativeComponent('PDFView');

interface NativeCommands {
  reload?: number;
}

const getNativeCommands = (): NativeCommands => {
  const viewManager = UIManager.getViewManagerConfig
    ? UIManager.getViewManagerConfig('PDFView')
    : // biome-ignore lint/suspicious/noExplicitAny: UIManager types are not fully typed
      (UIManager as any).PDFView;
  return viewManager?.Commands || {};
};

const PdfView = forwardRef<PdfViewRef, PdfViewProps>((props, ref) => {
  const {
    onError,
    onPageChanged,
    onScrolled,
    fadeInDuration = 0,
    resourceType = 'url',
    textEncoding = 'utf-8',
    urlProps = {},
    enableAnnotations = false,
    fileFrom = 'bundle',
    ...remainingProps
  } = props;

  // biome-ignore lint/suspicious/noExplicitAny: Native ref types are complex
  const nativeRef = useRef<any>(null);

  const handleError = useCallback(
    (event: { nativeEvent: { message: string } }) => {
      const { nativeEvent } = event;
      onError?.(nativeEvent);
    },
    [onError]
  );

  const handlePageChanged = useCallback(
    (event: { nativeEvent: { page: number; pageCount: number } }) => {
      const { nativeEvent } = event;
      const { page, pageCount } = nativeEvent;
      onPageChanged?.(page, pageCount);
    },
    [onPageChanged]
  );

  const handleScrolled = useCallback(
    (event: { nativeEvent: { offset: number } }) => {
      const { nativeEvent } = event;
      const { offset } = nativeEvent;
      onScrolled?.(offset);
    },
    [onScrolled]
  );

  const reload = useCallback(async (): Promise<void> => {
    if (nativeRef.current) {
      const handle = findNodeHandle(nativeRef.current);

      if (!handle) {
        throw new Error('Cannot find node handle');
      }

      const commands = getNativeCommands();
      const reloadCommand = commands.reload;

      if (typeof reloadCommand === 'undefined') {
        throw new Error('Reload command not found');
      }

      await Platform.select({
        android: async () => UIManager.dispatchViewManagerCommand(handle, reloadCommand, []),
        ios: async () => NativeModules.PDFView?.reload(handle),
        default: async () => {},
      })();
    } else {
      throw new Error('PdfView component is not mounted');
    }
  }, []);

  useImperativeHandle(ref, () => ({
    reload,
  }));

  return (
    <NativePdfView
      ref={nativeRef}
      {...remainingProps}
      fadeInDuration={fadeInDuration}
      resourceType={resourceType}
      textEncoding={textEncoding}
      urlProps={urlProps}
      enableAnnotations={enableAnnotations}
      fileFrom={fileFrom}
      onError={handleError}
      onPageChanged={handlePageChanged}
      onScrolled={handleScrolled}
    />
  );
});

PdfView.displayName = 'PdfView';

export { PdfView };
