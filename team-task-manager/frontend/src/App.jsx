import { useEffect, useState } from 'react';
import { getMe, logout } from './api';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';

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

    return <Dashboard user={user} onLogout={handleLogout} />;
}