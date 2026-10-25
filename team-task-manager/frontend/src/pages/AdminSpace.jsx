import { useEffect, useState } from 'react';
import { deleteUser, getUsers, updateUser } from '../api';

export default function AdminSpace({ currentUser }) {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [draft, setDraft] = useState({ firstName: '', lastName: '', email: '' });

  useEffect(() => {
    getUsers()
      .then(setUsers)
      .catch((err) => setError(err.message));
  }, []);

  function startEdit(u) {
    setError('');
    setEditingId(u.id);
    setDraft({ firstName: u.firstName, lastName: u.lastName, email: u.email });
  }

  async function saveEdit(id) {
    setError('');
    try {
      const updated = await updateUser(id, draft);
      setUsers((current) => current.map((u) => (u.id === id ? updated : u)));
      setEditingId(null);
    } catch (err) {
      setError(err.message);
    }
  }

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
            <th>Prénom</th>
            <th>Nom</th>
            <th>Email</th>
            <th>Rôle</th>
            <th>Équipe</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) =>
            editingId === u.id ? (
              <tr key={u.id}>
                <td><input value={draft.firstName} onChange={(e) => setDraft({ ...draft, firstName: e.target.value })} /></td>
                <td><input value={draft.lastName} onChange={(e) => setDraft({ ...draft, lastName: e.target.value })} /></td>
                <td><input value={draft.email} onChange={(e) => setDraft({ ...draft, email: e.target.value })} /></td>
                <td>{u.role}</td>
                <td>{u.teamName || '-'}</td>
                <td>
                  <button onClick={() => saveEdit(u.id)}>Enregistrer</button>{' '}
                  <button onClick={() => setEditingId(null)}>Annuler</button>
                </td>
              </tr>
            ) : (
              <tr key={u.id}>
                <td>{u.firstName}</td>
                <td>{u.lastName}</td>
                <td>{u.email}</td>
                <td>{u.role}</td>
                <td>{u.teamName || '-'}</td>
                <td>
                  <button onClick={() => startEdit(u)}>Modifier</button>{' '}
                  {u.id !== currentUser.id && (
                    <button onClick={() => handleDelete(u)}>Supprimer</button>
                  )}
                </td>
              </tr>
            )
          )}
        </tbody>
      </table>
    </div>
  );
}