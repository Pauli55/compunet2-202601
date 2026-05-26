import { createContext, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [token, setToken] = useState(() => {
        const storedToken = localStorage.getItem("token");

        if (!storedToken) {
            return null;
        }

        return storedToken;
    });
    // 
    return (
        <AuthContext.Provider value={{ isAuthenticated: token, setToken }}>
            {children}
        </AuthContext.Provider>
    );
}

export default AuthContext;