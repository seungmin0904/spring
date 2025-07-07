import { createContext, useContext, useEffect } from "react";

export const UserContext = createContext({
    user: null,      // { id, name, ... }
    setUser: () => {},
});
  
export const useUser = () => useContext(UserContext);