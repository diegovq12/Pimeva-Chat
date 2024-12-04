import React, { useState } from "react";
import axios from "axios"; // Importamos axios
import "./Register.css"
const Register = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [usernameExists, setUsernameExists] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null); // Para manejar errores

    const handleUsernameChange = async (e) => {
        const value = e.target.value;
        setUsername(value);

        if (value.length > 0) {
            setLoading(true);
            try {
                // Llamamos al backend para verificar si el nombre de usuario ya existe
                const response = await axios.get("/api/users/check-username", {
                    params: { username: value },
                });

                setUsernameExists(response.data); // true si el nombre de usuario ya existe
            } catch (error) {
                console.error("Error checking username:", error);
                setError("Error checking username");
            }
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (password !== confirmPassword) {
            alert("Passwords do not match!");
            return;
        }
        if (usernameExists) {
            alert("Username already exists!");
            return;
        }

        // Ahora mandamos los datos de registro al backend
        const user = {
            username,
            password
        };

        try {
            // Realizamos la solicitud de registro
            await axios.post("/api/users/register", user);

            // Si todo va bien, redirigimos al login o mostramos un mensaje de éxito
            alert("Registration successful!");
        } catch (error) {
            console.error("Error during registration:", error);
            setError("Registration failed. Please try again.");
        }
    };

    return (
        <div className="register-container">
            {/*<img src="../images/logo.jpg" alt="Logo"/>*/}
            <form className="register-form" onSubmit={handleSubmit}>
                <h2>Register</h2>
                <div className="input-group">
                    <label>Username:</label>
                    <input
                        type="text"
                        value={username}
                        onChange={handleUsernameChange}
                        required
                    />
                    {loading && <small>Checking username...</small>}
                    {usernameExists && <small style={{ color: "red" }}>Username already exists</small>}
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
                <div className="input-group">
                    <label>Confirm Password:</label>
                    <input
                        type="password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" disabled={usernameExists || loading}>
                    Register
                </button>
                {error && <div style={{ color: "red" }}>{error}</div>}
            </form>
        </div>
    );
};

export default Register;
