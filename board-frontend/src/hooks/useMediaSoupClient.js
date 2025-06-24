import { useEffect, useRef } from 'react';
import io from 'socket.io-client';
import * as mediasoupClient from 'mediasoup-client';

const SERVER_URL = 'http://localhost:4000';

export default function useMediasoupClient() {
  const socketRef = useRef(null);
  const deviceRef = useRef(null);
  const sendTransportRef = useRef(null);

  // 1. 소켓 연결 및 Device 준비
  useEffect(() => {
    console.log("🧩 useEffect: Connecting to mediasoup server...");
    socketRef.current = io(SERVER_URL);

    socketRef.current.on('connect', async () => {
      console.log('✅ Connected to mediasoup server:', socketRef.current.id);

      try {
        const device = new mediasoupClient.Device();
        deviceRef.current = device;
        console.log("📱 mediasoup Device created");

        // 1-2. 서버로부터 RTP Capabilities 수신
        socketRef.current.emit('getRtpCapabilities', async (rtpCapabilities) => {
          try {
            console.log("📡 Received RTP Capabilities:", rtpCapabilities);
            await device.load({ routerRtpCapabilities: rtpCapabilities });
            console.log('✅ Device loaded with RTP Capabilities');
          } catch (err) {
            console.error("❌ device.load() 실패:", err);
          }
        });
      } catch (err) {
        console.error("❌ Device 생성 실패:", err);
      }
    });

    socketRef.current.on('connect_error', (err) => {
      console.error("❌ Socket connect_error:", err);
    });

    socketRef.current.on('disconnect', (reason) => {
      console.warn("⚠️ Socket disconnected:", reason);
    });

    return () => {
      console.log("🔌 Cleaning up socket connection");
      socketRef.current.disconnect();
    };
  }, []);

  // 2. WebRTC Transport 생성 및 연결
  const createSendTransport = async () => {
    console.log("🛠️ createSendTransport called");

    if (!socketRef.current || !socketRef.current.connected) {
      console.error("❌ socket not connected");
      return;
    }
    if (!deviceRef.current) {
      console.error("❌ deviceRef is null");
      return;
    }

    return new Promise((resolve, reject) => {
      socketRef.current.emit('createWebRtcTransport', { direction: 'send' }, async (params) => {
        console.log("📡 createWebRtcTransport response:", params);
        if (params.error) {
          console.error('❌ Transport creation failed:', params.error);
          reject(params.error);
          return;
        }

        try {
          const transport = deviceRef.current.createSendTransport(params);
          sendTransportRef.current = transport;
          console.log("🚚 SendTransport created:", transport.id);

          transport.on('connect', ({ dtlsParameters }, callback, errback) => {
            console.log("🔗 SendTransport connecting with DTLS:", dtlsParameters);
            socketRef.current.emit('connectTransport', { transportId: transport.id, dtlsParameters }, (response) => {
              console.log("✅ connectTransport response:", response);
              if (response.error) {
                console.error("❌ Transport connect error:", response.error);
                errback(response.error);
              } else {
                callback();
              }
            });
          });

          transport.on('produce', ({ kind, rtpParameters }, callback, errback) => {
            console.log("📦 Producing track:", kind, rtpParameters);
            socketRef.current.emit('produce', { transportId: transport.id, kind, rtpParameters }, ({ id, error }) => {
              if (error) {
                console.error("❌ Produce error:", error);
                errback(error);
              } else {
                console.log("✅ Producer created:", id);
                callback({ id });
              }
            });
          });

          resolve(); // ✅ transport 생성 완료됨
        } catch (err) {
          console.error("❌ createSendTransport exception:", err);
          reject(err);
        }
      });
    });
  };

  // 3. 오디오 트랙 전송
  const sendAudio = async () => {
    console.log("🎤 sendAudio called");

    if (!sendTransportRef.current) {
      console.error('❌ Transport not ready');
      return;
    }

    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const track = stream.getAudioTracks()[0];
      console.log("🎙️ Got local audio track:", track);

      const producer = await sendTransportRef.current.produce({ track });
      console.log("✅ Audio track sent. Producer ID:", producer.id);
    } catch (err) {
      console.error("❌ Failed to get audio or produce track:", err);
    }
  };

  return {
    createSendTransport,
    sendAudio,
  };
}
