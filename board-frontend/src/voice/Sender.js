// src/components/Sender.js
import { useEffect } from 'react';
import io from 'socket.io-client';
import * as mediasoupClient from 'mediasoup-client';

const socket = io('http://localhost:4000'); // 포트 맞춰줌

export default function Sender() {
  useEffect(() => {
    const startSending = async () => {
      await new Promise((resolve) => socket.on('connect', resolve));

      console.log('✅ Socket connected:', socket.id);

      // 1. 서버에서 RTP Capabilities 받아옴
      const rtpCapabilities = await new Promise((resolve) => {
        socket.emit('getRtpCapabilities', resolve);
      });

      // 2. Device 생성
      const device = new mediasoupClient.Device();
      await device.load({ routerRtpCapabilities: rtpCapabilities });

      // 3. 서버에 Transport 생성 요청
      const transportParams = await new Promise((resolve) => {
        socket.emit('createWebRtcTransport', { direction: 'send' }, resolve);
      });

      const transport = device.createSendTransport(transportParams);

      // 4. 서버와 트랜스포트 연결
      transport.on('connect', ({ dtlsParameters }, callback, errback) => {
        socket.emit('connectTransport', { transportId: transport.id, dtlsParameters }, callback);
      });

      // 5. 서버에 media 정보 보내기 (Producer 생성)
      transport.on('produce', ({ kind, rtpParameters }, callback, errback) => {
        socket.emit('produce', { transportId: transport.id, kind, rtpParameters }, ({ id }) => {
          callback({ id });
        });
      });

      // 6. 마이크 접근해서 track 추출
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const track = stream.getAudioTracks()[0];

      // 7. 송신 시작
      const producer = await transport.produce({ track });
      console.log('🎤 Sending audio with producer ID:', producer.id);
    };

    startSending();
  }, []);

  return <div>🎤 송신자 준비 완료</div>;
}