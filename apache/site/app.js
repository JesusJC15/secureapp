const appConfig = window.SECURE_APP_CONFIG || { apiBaseUrl: "/api" };
const sessionStorageKey = "secureapp.session.token";

let sessionToken = sessionStorage.getItem(sessionStorageKey) || "";

const messagePanel = document.querySelector("#message-panel");
const publicInfoPanel = document.querySelector("#public-info");
const secureInfoPanel = document.querySelector("#secure-info");
const sessionPanel = document.querySelector("#session-panel");
const registerForm = document.querySelector("#register-form");
const loginForm = document.querySelector("#login-form");
const profileButton = document.querySelector("#load-profile");
const statusButton = document.querySelector("#load-status");
const logoutButton = document.querySelector("#logout");

function setMessage(text, type = "info") {
    messagePanel.textContent = text;
    messagePanel.dataset.type = type;
}

function saveSession(token) {
    sessionToken = token || "";
    if (sessionToken) {
        sessionStorage.setItem(sessionStorageKey, sessionToken);
    } else {
        sessionStorage.removeItem(sessionStorageKey);
    }
    sessionPanel.textContent = sessionToken
        ? "Session token loaded. Protected requests will include a Bearer token."
        : "No active session.";
}

async function apiRequest(path, options = {}, requiresAuth = false) {
    const headers = {
        Accept: "application/json",
        ...(options.headers || {})
    };

    if (options.body && !headers["Content-Type"]) {
        headers["Content-Type"] = "application/json";
    }

    if (requiresAuth && sessionToken) {
        headers.Authorization = `Bearer ${sessionToken}`;
    }

    const response = await fetch(`${appConfig.apiBaseUrl}${path}`, {
        ...options,
        headers
    });

    const payload = await response.json().catch(() => ({}));
    if (!response.ok) {
        throw new Error(payload.message || `HTTP ${response.status}`);
    }

    return payload;
}

async function loadPublicInfo() {
    try {
        const payload = await apiRequest("/public/info");
        publicInfoPanel.textContent = JSON.stringify(payload, null, 2);
        setMessage("Connected to the public API endpoint over HTTPS.", "success");
    } catch (error) {
        setMessage(`Unable to load public info: ${error.message}`, "error");
    }
}

async function loadProfile() {
    try {
        const payload = await apiRequest("/secure/profile", {}, true);
        secureInfoPanel.textContent = JSON.stringify(payload, null, 2);
        setMessage("Protected profile loaded successfully.", "success");
    } catch (error) {
        setMessage(`Protected profile failed: ${error.message}`, "error");
    }
}

async function loadSecureStatus() {
    try {
        const payload = await apiRequest("/secure/status", {}, true);
        secureInfoPanel.textContent = JSON.stringify(payload, null, 2);
        setMessage("Secure status loaded from Spring.", "success");
    } catch (error) {
        setMessage(`Secure status failed: ${error.message}`, "error");
    }
}

registerForm.addEventListener("submit", async event => {
    event.preventDefault();
    const formData = new FormData(registerForm);
    const body = JSON.stringify({
        username: formData.get("register-username"),
        displayName: formData.get("register-display-name"),
        password: formData.get("register-password")
    });

    try {
        const payload = await apiRequest("/auth/register", { method: "POST", body });
        setMessage(payload.message, "success");
        registerForm.reset();
    } catch (error) {
        setMessage(`Registration failed: ${error.message}`, "error");
    }
});

loginForm.addEventListener("submit", async event => {
    event.preventDefault();
    const formData = new FormData(loginForm);
    const body = JSON.stringify({
        username: formData.get("login-username"),
        password: formData.get("login-password")
    });

    try {
        const payload = await apiRequest("/auth/login", { method: "POST", body });
        saveSession(payload.token);
        secureInfoPanel.textContent = JSON.stringify(payload, null, 2);
        setMessage(`Logged in as ${payload.displayName}.`, "success");
        loginForm.reset();
    } catch (error) {
        saveSession("");
        setMessage(`Login failed: ${error.message}`, "error");
    }
});

profileButton.addEventListener("click", loadProfile);
statusButton.addEventListener("click", loadSecureStatus);
logoutButton.addEventListener("click", async () => {
    try {
        await apiRequest("/auth/logout", { method: "POST" }, true);
    } catch (error) {
        setMessage(`Logout warning: ${error.message}`, "error");
    } finally {
        saveSession("");
        secureInfoPanel.textContent = "{\n  \"message\": \"Session closed\"\n}";
    }
});

saveSession(sessionToken);
loadPublicInfo();
