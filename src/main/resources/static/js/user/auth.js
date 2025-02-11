const API_BASE_URL = "/api/users";

export const auth = {
  login: async (email, password) => {
    try {
      const response = await fetch(`${API_BASE_URL}/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        throw new Error("로그인 실패");
      }

      const token = await response.text();
      localStorage.setItem("jwt", token); // JWT 저장
      return token;
    } catch (error) {
      console.error("Login error:", error);
      throw error;
    }
  },

  logout: () => {
    localStorage.removeItem("jwt"); // 로그아웃 시 토큰 삭제
  },

  getToken: () => {
    return localStorage.getItem("jwt");
  },

  isAuthenticated: () => {
    const token = auth.getToken();
    return token !== null;
  },
};