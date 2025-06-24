import { useEffect, useRef } from 'react';
import io from 'socket.io-client';
import * as mediasoupClient from 'mediasoup-client';

const SERVER_URL = 'http://localhost:4000';

export default function useMediasoupClient() {
  const socketRef = useRef(null);
  const deviceRef = useRef(null);
  const sendTransportRef = useRef(null);
  const recvTransportRef = useRef(null);

  useEffect(() => {
    socketRef.current = io(SERVER_URL);

    socketRef.current.on('connect', async () => {
      console.log('✅ Connected to mediasoup server');

      const device = new mediasoupClient.Device();
      deviceRef.current = device;

      socketRef.current.emit('getRtpCapabilities', async (rtpCapabilities) => {
        console.log("🎯 RTP Capabilities:", rtpCapabilities.codecs.map(c => c.mimeType));
        await device.load({ routerRtpCapabilities: rtpCapabilities });
        console.log('📡 Device loaded');
      });
    });

    socketRef.current.on('newProducer', async ({ producerId }) => {
      console.log("🎧 New producer detected:", producerId);
      await consumeSpecificAudio(producerId);
    });

    return () => socketRef.current.disconnect();
  }, []);

  const createSendTransport = async () => {
    return new Promise((resolve, reject) => {
      socketRef.current.emit('createWebRtcTransport', { direction: 'send' }, async (params) => {
        try {
          const transport = deviceRef.current.createSendTransport(params);
          sendTransportRef.current = transport;

          transport.on('connect', ({ dtlsParameters }, callback, errback) => {
            socketRef.current.emit('connectTransport', { transportId: transport.id, dtlsParameters }, (res) => {
              res.error ? errback(res.error) : callback();
            });
          });

          transport.on('produce', ({ kind, rtpParameters }, callback, errback) => {
            socketRef.current.emit('produce', { transportId: transport.id, kind, rtpParameters }, ({ id, error }) => {
              error ? errback(error) : callback({ id });
            });
          });

          resolve();
        } catch (err) {
          reject(err);
        }
      });
    });
  };

  const sendAudio = async () => {
    console.log("🎤 sendAudio called");
  
    if (!sendTransportRef.current) {
      console.warn("❌ sendTransportRef is null");
      return;
    }
  
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const track = stream.getAudioTracks()[0];
      console.log("🎙️ Got local audio track:", track.label);
  
      // 🔍 마이크 입력 볼륨 확인용 분석기 연결
      const audioContext = new (window.AudioContext || window.webkitAudioContext)();
      const source = audioContext.createMediaStreamSource(stream);
      const analyser = audioContext.createAnalyser();
      source.connect(analyser);
      const dataArray = new Uint8Array(analyser.frequencyBinCount);
  
      setInterval(() => {
        analyser.getByteFrequencyData(dataArray);
        const avg = dataArray.reduce((a, b) => a + b, 0) / dataArray.length;
        console.log("🎙️ Mic volume level:", avg.toFixed(2));
      }, 500);
  
      // ✅ 실제 송신
      await sendTransportRef.current.produce({ track });
      console.log("📤 Audio produced successfully");
    } catch (err) {
      console.error("❌ sendAudio error:", err);
    }
  };
  

  const createRecvTransport = async () => {
    return new Promise((resolve, reject) => {
      socketRef.current.emit('createWebRtcTransport', { direction: 'recv' }, async (params) => {
        try {
          const transport = deviceRef.current.createRecvTransport(params);
          recvTransportRef.current = transport;

          transport.on('connect', ({ dtlsParameters }, callback, errback) => {
            socketRef.current.emit('connectTransport', { transportId: transport.id, dtlsParameters }, (res) => {
              res.error ? errback(res.error) : callback();
            });
          });

          // ✅ 기존 producer들 요청
          socketRef.current.emit('getProducers', async (producerIds) => {
            for (const producerId of producerIds) {
              console.log("📦 Found existing producer:", producerId);
              await consumeSpecificAudio(producerId);
            }
          });

          resolve();
        } catch (err) {
          reject(err);
        }
      });
    });
  };

  const consumeSpecificAudio = async (producerId) => {
    if (!recvTransportRef.current) {
      console.warn("⚠️ recvTransportRef is null at consume time");
      return;
    }
  
    console.log("🔄 Attempting to consume audio from producer:", producerId);
  
    socketRef.current.emit(
      'consume',
      {
        transportId: recvTransportRef.current.id,
        producerId,
      },
      async (params) => {
        if (params.error) return console.error("❌ consume error:", params.error);
  
        const { id, kind, rtpParameters } = params;
  
        console.log("🎧 Consumed codec mimeType:", rtpParameters.codecs?.[0]?.mimeType);
  
        try {
          const consumer = await recvTransportRef.current.consume({
            id,
            producerId,
            kind,
            rtpParameters,
          });
  
          console.log("👂 consumer.track:", consumer.track);
          console.log("🎤 consumer.track.enabled:", consumer.track.enabled);
  
          const stream = new MediaStream();
          stream.addTrack(consumer.track);
  
          console.log("📦 MediaStream tracks:", stream.getTracks());
          console.log("🎧 stream.getAudioTracks():", stream.getAudioTracks());
  
          // ✅ GainNode로 볼륨 증폭 처리 + AudioContext 상태 확인
          const audioContext = new (window.AudioContext || window.webkitAudioContext)();
          console.log("🔊 AudioContext state (초기):", audioContext.state);
  
          audioContext.onstatechange = () => {
            console.log("📻 AudioContext state changed to:", audioContext.state);
          };
  
          const source = audioContext.createMediaStreamSource(stream);
          const gainNode = audioContext.createGain();
          gainNode.gain.value = 3.0;
  
          source.connect(gainNode);
          gainNode.connect(audioContext.destination);
  
          try {
            await audioContext.resume();
            console.log("🟢 AudioContext resumed");
          } catch (e) {
            console.warn("❌ AudioContext resume 실패:", e);
          }
  
          // 🔇 <audio>는 별도로 유지 (Autoplay 정책 대응 및 시각화용)
          const audio = new Audio();
          audio.srcObject = stream;
          audio.autoplay = true;
          audio.muted = false;
          audio.volume = 1.0;
  
          document.body.appendChild(audio);
  
          audio.addEventListener("playing", () => {
            console.log("🔈 actually playing");
          });
          audio.addEventListener("error", (e) => {
            console.warn("❗ Audio playback error:", e);
          });
  
          console.log("🔊 Audio element 상태:", audio);
  
          try {
            await audio.play();
            console.log("🔊 Audio playback started for producer:", producerId);
          } catch (err) {
            console.warn("🔇 Audio playback failed:", err);
          }
        } catch (err) {
          console.error("❌ Failed to create consumer or audio:", err);
        }
      }
    );
  };
  
  return {
    createSendTransport,
    sendAudio,
    createRecvTransport,
    consumeSpecificAudio,
  };
}
