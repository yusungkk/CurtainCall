import { auth } from "./auth.js";

const API_BASE_URL = "/api/users";

export const api = {
  request: async (endpoint, method = "GET", data = null) => {
    const token = auth.getToken();
    const headers = {
      "Content-Type": "application/json",
      Authorization: token ? `Bearer ${token}` : "",
    };

    const options = { method, headers };
    if (data) {
      options.body = JSON.stringify(data);
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, options);

    if (response.status === 401) {
      auth.logout();
      window.location.href = "/login";
      throw new Error("인증이 만료되었습니다. 다시 로그인하세요.");
    }

    if (!response.ok) {
      throw new Error(`API 요청 실패: ${response.statusText}`);
    }

    return response.json();
  },

  getUser: (id) => api.request(`/${id}`, "GET"),

  updateUser: (id, data) => api.request(`/update/${id}`, "PUT", data),

  deleteUser: (id) => api.request(`/${id}`, "DELETE"),
};