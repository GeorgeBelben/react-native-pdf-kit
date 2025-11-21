import React, { useRef } from 'react';
import { Button, SafeAreaView, StyleSheet, Text, View } from 'react-native';
import PDFView, { type PdfViewRef } from 'react-native-pdf-kit';

export default function App() {
  const pdfRef = useRef<PdfViewRef>(null);

  const handleReload = () => {
    pdfRef.current?.reload();
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>PDF Viewer Example</Text>
        <Button title="Reload PDF" onPress={handleReload} />
      </View>
      <PDFView
        ref={pdfRef}
        style={styles.pdf}
        resource="https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        resourceType="url"
        onLoad={() => console.log('PDF loaded successfully')}
        onError={(error: { message: string }) => console.error('PDF load error:', error)}
        onPageChanged={(page: number, pageCount: number) => {
          console.log(`Page ${page} of ${pageCount}`);
        }}
        fadeInDuration={250}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  header: {
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 12,
  },
  pdf: {
    flex: 1,
  },
});
