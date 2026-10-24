import { useEffect, useState } from 'react';
import { deleteUser, getUsers } from '../api';

export default function AdminSpace({ currentUser }) {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    getUsers()
      .then(setUsers)
      .catch((err) => setError(err.message));
  }, []);

  async function handleDelete(u) {
    if (!window.confirm(`Supprimer ${u.firstName} ${u.lastName} (${u.email}) ?`)) return;
    setError('');
    try {
      await deleteUser(u.id);
      setUsers((current) => current.filter((x) => x.id !== u.id));
    } catch (err) {
      setError(err.message);
    }
  }

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
            <th></th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.firstName} {u.lastName}</td>
              <td>{u.email}</td>
              <td>{u.role}</td>
              <td>{u.teamName || '-'}</td>
              <td>
                {u.id !== currentUser.id && (
                  <button onClick={() => handleDelete(u)}>Supprimer</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}