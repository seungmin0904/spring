import { createContext, useContext } from "react";

export const UserContext = createContext({
    user: null,      // { id, name, ... }
    setUser: () => {},
  });

export const useUser = () => useContext(UserContext);