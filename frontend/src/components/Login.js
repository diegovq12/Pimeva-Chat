import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";

const Image = styled.img`
    width: 120px; =
    height: auto;
    border-radius: 50%;
    margin-bottom: -10px;
    margin-left: 115px;
`;

const Login = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        try {
            const response = await axios.post("/api/users/login", { username, password });

            if (response.data === "Login successful!") {
                // Redirige a la página de chat con el username como estado
                navigate("/chat", { state: { username } });
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
                <Image src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQlHGITJiW_D_3sb6Qb4SnJDym4rdp-9ip84Q&s"
                       alt="logo" />
                <h2>Login</h2>
                <p>Chat with the Pimeva community</p>
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
