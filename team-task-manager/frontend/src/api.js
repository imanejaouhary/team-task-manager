
const API_URL = 'http://localhost:8080';

async function request(path, options = {}) {
  const token = localStorage.getItem('token');
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const res = await fetch(`${API_URL}${path}`, {
    ...options,
    headers,
  });

  if (!res.ok) {
    let message = `Erreur ${res.status}`;

    try {
      const data = await res.json();
      if (data.message) message = data.message;
    } catch {
      // Réponse sans corps JSON
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

export function getProjects() {
  return request('/projects');
}

export function getTasks() {
  return request('/tasks');
}

export function updateTaskStatus(id, status) {
  return request(`/tasks/${id}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status }),
  });
}

export function getTeams() {
  return request('/teams');
}

export function getTeamMembers(teamId) {
  return request(`/teams/${teamId}/members`);
}

export function createTask(taskData) {
  return request('/tasks', {
    method: 'POST',
    body: JSON.stringify(taskData),
  });
}
export function updateTask(id, taskData) {
  return request(`/tasks/${id}`, {
    method: 'PUT',
    body: JSON.stringify(taskData),
  });
}
export function getUsers() {
  return request('/users');
}

