import { createContext, useContext, useState } from "react";

//1: Create a Context
export const AuthContext = createContext()

export const useAuth = () => useContext(AuthContext)

//2: Share the created context with other components
export default function AuthProvider({ children }) {

    //3: Put some state in the context
    const [isAuthenticated, setAuthenticated] = useState(false)

    const [username, setusername] = useState(null)

    function login(username, password) {
        if(username==='in28minutes' && password==='asd'){
            setAuthenticated(true)
            setusername(username)
            return true            
        } else {
            setAuthenticated(false)
            setusername(null)
            return false
        }        
    }

    function logout() {
        setAuthenticated(false)
    }

    return (
        <AuthContext.Provider value={ {isAuthenticated, login, logout,username}  }>
            {children}
        </AuthContext.Provider>
    )
} 