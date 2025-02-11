const API_BASE_URL = "/api/users";

export const registerUser = async (userData) => {
  try {
    const response = await fetch(API_BASE_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userData),
    });

    if (!response.ok) {
      throw new Error("회원가입 실패");
    }

    return await response.json();
  } catch (error) {
    console.error("Register error:", error);
    throw error;
  }
};