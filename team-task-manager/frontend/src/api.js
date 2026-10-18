const API_URL = 'http://localhost:8080';

async function request(path, options = {}) {
  const token = localStorage.getItem('token');
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  if (token) headers.Authorization = `Bearer ${token}`;

  const res = await fetch(`${API_URL}${path}`, { ...options, headers });

  if (!res.ok) {
    let message = `Erreur ${res.status}`;
    try {
      const data = await res.json();
      if (data.message) message = data.message;
    } catch {
      // réponse sans corps
    }
    throw new Error(message);
  }
  if (res.status === 204) return null;
  return res.json();
}

export function getMe() {
  return request('/users/me');
}

export async function login(email, password) {
  localStorage.removeItem('token');
  const data = await request('/users/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  });
  localStorage.setItem('token', data.token);
  return getMe();
}

export function logout() {
  localStorage.removeItem('token');
}