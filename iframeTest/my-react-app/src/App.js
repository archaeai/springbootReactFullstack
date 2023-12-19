import React, { useRef, useEffect } from 'react';
import './App.css';

function App() {
  const iframeRef = useRef(null);

  useEffect(() => {
    const iframe = iframeRef.current;
    if (iframe) {
      iframe.onload = () => {
        // 메시지를 보냅니다
        try {
          const iframeWindow = iframe.contentWindow;
          iframeWindow.postMessage({ 
            username: 'admin', 
            password: 'Tsmtech223!' 
          }, 'https://192.168.6.50'); // 'https://192.168.6.50'는 대상 도메인을 나타냅니다
        } catch (e) {
          console.error('Error sending message to iframe:', e);
        }
      };
    }
  }, []);

  return (
    <div className="App">
      <h1>IFRAME 테스트 입니다</h1>
      <iframe
        ref={iframeRef}
        src="https://192.168.6.50"
        width="100%"
        height="600px"
        style={{ border: 'none' }}
        title="Example Page"
        sandbox="allow-same-origin allow-forms allow-scripts"
      ></iframe>
    </div>
  );
}

export default App;
