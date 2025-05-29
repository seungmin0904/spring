import ChannelList from "@/chat/ChannelList";
import ChatRoom from "@/chat/ChatRoom";

const HomePage = () => {
  return (
    <div className="flex h-screen">
      <ChannelList />
      <ChatRoom />
    </div>
  );
};

export default HomePage;

