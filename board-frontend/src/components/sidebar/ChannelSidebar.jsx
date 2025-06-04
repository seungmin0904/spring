import { useEffect, useState } from "react";
import axiosInstance from "@/lib/axiosInstance";

export default function ChannelSidebar({ serverId, selectedChannelId, onSelectChannel }) {
  const [channels, setChannels] = useState([]);

  useEffect(() => {
    if (!serverId) { setChannels([]); return; }
    axiosInstance.get(`/servers/${serverId}/channels`)
      .then(res => setChannels(Array.isArray(res.data) ? res.data : []))
      .catch(() => setChannels([]));
  }, [serverId]);

  return (
    <div className="w-52 bg-neutral-950 border-r border-neutral-800 flex flex-col py-2">
      <div className="px-4 text-neutral-300 mb-3 font-bold">채널</div>
      {channels.map(ch => (
        <button
          key={ch.id}
          className={`px-4 py-2 text-left w-full ${selectedChannelId === ch.id ? "bg-neutral-800 text-white" : "hover:bg-neutral-800 text-neutral-400"}`}
          onClick={() => onSelectChannel(ch.id)}
        >
          #{ch.name}
        </button>
      ))}
    </div>
  );
}