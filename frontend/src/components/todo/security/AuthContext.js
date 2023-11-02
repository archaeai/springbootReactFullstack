import { createContext, useContext, useState } from "react";
import { apiClient } from "../api/ApiClient";
import { executeBasicAuthenticationService, executeJwtAuthenticationService } from "../api/AuthenticationApiService";

//1: Create a Context
export const AuthContext = createContext()

export const useAuth = () => useContext(AuthContext)

//2: Share the created context with other components
export default function AuthProvider({ children }) {

    //3: Put some state in the context
    const [isAuthenticated, setAuthenticated] = useState(false)

    const [username, setusername] = useState(null)
    const [token,setToken] = useState(null)


    // async function login(username, password) {
    //     const baToken = 'Basic '+ window.btoa(username + ":" +password)

    //     try {
    //         const response = await executeBasicAuthenticationService(baToken)
    //         if(response.status==200){
    //             setAuthenticated(true)
    //             setusername(username)
    //             setToken(baToken)
    //             // 모든 header에 추가하는 코드 
    //             apiClient.interceptors.request.use(
    //                 (config) => {
    //                     console.log('intercepting and add a token')
    //                     config.headers.Authorization=baToken
    //                     return config
    //                 }
    //             )


    //             return true            
    //         } else {
    //             logout()
    //             return false
    //         }   
    //     } catch(error){
    //         logout()
    //         return false
    //     }
    // }

    async function login(username, password) {

        try {
            const response = await executeJwtAuthenticationService(username,password)
            if(response.status==200){
                const jwtToken = 'Bearer '+ response.data.token
                setAuthenticated(true)
                setusername(username)
                setToken(jwtToken)

                // 모든 header에 추가하는 코드 
                apiClient.interceptors.request.use(
                    (config) => {
                        console.log('intercepting and add a token')
                        config.headers.Authorization=jwtToken
                        return config
                    }
                )


                return true            
            } else {
                logout()
                return false
            }   
        } catch(error){
            logout()
            return false
        }
    }

    function logout() {
        setAuthenticated(false)
        setToken(null)
        setusername(null)
    }

    return (
        <AuthContext.Provider value={ {isAuthenticated, login, logout,username,token}  }>
            {children}
        </AuthContext.Provider>
    )
} 