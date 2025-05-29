import { useChat } from "@/context/ChatContext";

const ChannelList = () => {
  const { setSelectedChannel } = useChat();

  const channels = [
    { id: 1, name: "일반" },
    { id: 2, name: "개발" },
    { id: 3, name: "잡담" },
  ];

  return (
    <div className="w-48 bg-gray-800 text-white p-2">
      {channels.map((ch) => (
        <div
          key={ch.id}
          className="p-2 hover:bg-gray-700 cursor-pointer"
          onClick={() => setSelectedChannel(ch)}
        >
          # {ch.name}
        </div>
      ))}
    </div>
  );
};

export default ChannelList;