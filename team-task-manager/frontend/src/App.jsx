import { useEffect, useState } from 'react';
import { getMe, logout } from './api';
import Login from './pages/Login';

export default function App() {
  const [user, setUser] = useState(null);
  const [checking, setChecking] = useState(true);

  useEffect(() => {
    if (!localStorage.getItem('token')) {
      setChecking(false);
      return;
    }
    getMe()
      .then(setUser)
      .catch(() => logout())
      .finally(() => setChecking(false));
  }, []);

  function handleLogout() {
    logout();
    setUser(null);
  }

  if (checking) return <p>Chargement...</p>;
  if (!user) return <Login onLogin={setUser} />;

  return (
    <div style={{ maxWidth: 480, margin: '80px auto' }}>
      <h1>Bonjour {user.firstName} {user.lastName}</h1>
      <p>Rôle : <strong>{user.role}</strong></p>
      <button onClick={handleLogout}>Se déconnecter</button>
    </div>
  );
}