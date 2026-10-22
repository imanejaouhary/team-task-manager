import { useEffect, useState } from 'react';
import { getUsers } from '../api';

export default function AdminSpace() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    getUsers()
      .then(setUsers)
      .catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <h2>Utilisateurs</h2>
      {error && <p style={{ color: 'crimson' }}>{error}</p>}
      <table style={{ width: '100%', textAlign: 'left' }}>
        <thead>
          <tr>
            <th>Nom</th>
            <th>Email</th>
            <th>Rôle</th>
            <th>Équipe</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.firstName} {u.lastName}</td>
              <td>{u.email}</td>
              <td>{u.role}</td>
              <td>{u.teamName || '-'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}