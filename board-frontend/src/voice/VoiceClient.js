import * as mediasoupClient from 'mediasoup-client';
import io from 'socket.io-client';

const socket = io('http://localhost:4000'); // signaling server 주소

let device;
let sendTransport;

export async function joinRoom() {
  socket.emit('getRouterRtpCapabilities', async (rtpCapabilities) => {
    device = new mediasoupClient.Device();
    await device.load({ routerRtpCapabilities: rtpCapabilities });

    // 서버에게 send transport 요청
    socket.emit('createWebRtcTransport', { consumer: false }, async ({ params }) => {
      sendTransport = device.createSendTransport(params);

      sendTransport.on('connect', ({ dtlsParameters }, callback) => {
        socket.emit('transport-connect', { dtlsParameters });
        callback();
      });

      sendTransport.on('produce', async ({ kind, rtpParameters }, callback) => {
        socket.emit('transport-produce', { kind, rtpParameters }, ({ id }) => {
          callback({ id });
        });
      });

      // 마이크 얻고 producer 생성
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const track = stream.getAudioTracks()[0];
      sendTransport.produce({ track });
    });
  });
}