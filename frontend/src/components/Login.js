import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const Login = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null); // Manejo de errores
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        try {
            const response = await axios.post("/api/users/login", { username, password });

            // Verifica si el backend devuelve un mensaje de éxito como texto
            if (response.data === "Login successful!") {
                navigate("/chat"); // Redirige a la página de chat
            } else {
                setError("Login failed: " + response.data);
            }
        } catch (error) {
            console.error("Error during login:", error);
            setError(error.response?.data || "An error occurred during login.");
        }
    };

    return (
        <div className="register-container">
            <form className="register-form" onSubmit={handleSubmit}>
                <h2>Login</h2>
                <p>Join the Pimeva community</p>
                <div className="input-group">
                    <label>Username:</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div className="input-group">
                    <label>Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" disabled={loading}>
                    {loading ? "Logging in..." : "Login"}
                </button>
                {error && <div style={{ color: "red", marginTop: "10px" }}>{error}</div>}
            </form>
        </div>
    );
};

export default Login;
