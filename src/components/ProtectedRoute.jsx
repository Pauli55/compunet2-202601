import { useContext, useEffect } from "react";
import { Outlet, useNavigate } from "react-router";
import AuthContext from "../context/AuthContext";

export default function ProtectedRoute() {
    const { isAuthenticated } = useContext(AuthContext);
    const nav = useNavigate();

    useEffect(() => {
        if (!isAuthenticated) {
            nav("/auth/login");
        };
    }, [isAuthenticated, nav]);

    return (
        <Outlet />
    );
}