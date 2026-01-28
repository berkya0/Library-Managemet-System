document.addEventListener("DOMContentLoaded", () => {
    // Token kontrolü
    const token = localStorage.getItem("token");
    if (token && isTokenExpired(token)) {
        logout();
    }

    // Login Form
    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
        loginForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            try {
                const response = await fetch("/rest/api/authenticate", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        username: document.getElementById("username").value,
                        password: document.getElementById("password").value
                    })
                });

                const data = await handleResponse(response);
                if (data) {
                    saveAuthData(data.payload);
                    window.location.href = "/dashboard.html";
                }
            } catch (err) {
                showError("Giriş başarısız: " + err.message);
            }
        });
    }

    // Register Form
    const registerForm = document.getElementById("registerForm");
    if (registerForm) {
        registerForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            try {
                const response = await fetch("/rest/api/user/register", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        username: document.getElementById("regUsername").value,
                        password: document.getElementById("regPassword").value,
                        fullName: document.getElementById("fullName").value,
                        email: document.getElementById("email").value,
                        phoneNumber: document.getElementById("phoneNumber").value
                    })
                });

                const data = await handleResponse(response);
                if (data) {
                    alert("Kayıt başarılı! Giriş yapabilirsiniz.");
                    window.location.href = "/login.html";
                }
            } catch (err) {
                showError("Kayıt başarısız: " + err.message);
            }
        });
    }

    // Yardımcı Fonksiyonlar
    function saveAuthData(authData) {
        localStorage.setItem("token", authData.accessToken);
        localStorage.setItem("refreshToken", authData.refreshToken);
        localStorage.setItem("user", JSON.stringify({
            username: authData.username,
            role: authData.role
        }));
    }

    async function handleResponse(response) {
        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.errorMessage || `HTTP error! status: ${response.status}`);
        }
        return data;
    }

    function isTokenExpired(token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.exp * 1000 < Date.now();
        } catch (e) {
            return true;
        }
    }

    function logout() {
        localStorage.clear();
        window.location.href = "/login.html";
    }

    function showError(message) {
        console.error(message);
        alert(message);
    }
});