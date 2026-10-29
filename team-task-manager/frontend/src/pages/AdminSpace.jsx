import { useEffect, useState } from 'react';
import {
  changeUserRole,
  changeUserTeam,
  deleteUser,
  getTeams,
  getUsers,
  updateUser,
} from '../api';

const ROLES = ['EMPLOYEE', 'MANAGER', 'CLIENT'];

export default function AdminSpace({ currentUser }) {
  const [users, setUsers] = useState([]);
  const [teams, setTeams] = useState([]);
  const [error, setError] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [draft, setDraft] = useState({ firstName: '', lastName: '', email: '' });
  const [accessId, setAccessId] = useState(null);
  const [accessDraft, setAccessDraft] = useState({ roleName: '', teamId: '', unassignOpenTasks: false });

  function loadUsers() {
    getUsers()
      .then(setUsers)
      .catch((err) => setError(err.message));
  }

  useEffect(() => {
    loadUsers();
    getTeams()
      .then(setTeams)
      .catch((err) => setError(err.message));
  }, []);

  function startEdit(u) {
    setError('');
    setAccessId(null);
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

  function startAccess(u) {
    setError('');
    setEditingId(null);
    setAccessId(u.id);
    setAccessDraft({ roleName: u.role, teamId: '', unassignOpenTasks: false });
  }

  async function saveAccess(u) {
    setError('');
    const sameRole = accessDraft.roleName === u.role;
    const needsTeam = accessDraft.roleName === 'EMPLOYEE';

    if (needsTeam && !accessDraft.teamId) {
      setError('Choisis une équipe pour un employé.');
      return;
    }
    if (sameRole && !needsTeam) {
      setError('Aucun changement à enregistrer.');
      return;
    }

    try {
      if (sameRole) {
        await changeUserTeam(u.id, Number(accessDraft.teamId));
      } else {
        const body = {
          roleName: accessDraft.roleName,
          unassignOpenTasks: accessDraft.unassignOpenTasks,
        };
        if (needsTeam) body.teamId = Number(accessDraft.teamId);
        await changeUserRole(u.id, body);
      }
      setAccessId(null);
      loadUsers();
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
          {users.map((u) => {
            const isSelf = u.id === currentUser.id;
            const canChangeAccess = !isSelf && u.role !== 'ADMIN';

            if (editingId === u.id) {
              return (
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
              );
            }

            if (accessId === u.id) {
              return (
                <tr key={u.id}>
                  <td>{u.firstName}</td>
                  <td>{u.lastName}</td>
                  <td>{u.email}</td>
                  <td>
                    <select
                      value={accessDraft.roleName}
                      onChange={(e) => setAccessDraft({ ...accessDraft, roleName: e.target.value })}
                    >
                      {ROLES.map((r) => <option key={r} value={r}>{r}</option>)}
                    </select>
                  </td>
                  <td>
                    {accessDraft.roleName === 'EMPLOYEE' ? (
                      <select
                        value={accessDraft.teamId}
                        onChange={(e) => setAccessDraft({ ...accessDraft, teamId: e.target.value })}
                      >
                        <option value="">Choisir une équipe</option>
                        {teams.map((t) => <option key={t.id} value={t.id}>{t.name}</option>)}
                      </select>
                    ) : '-'}
                  </td>
                  <td>
                    {u.role === 'EMPLOYEE' && accessDraft.roleName !== 'EMPLOYEE' && (
                      <label style={{ display: 'block' }}>
                        <input
                          type="checkbox"
                          checked={accessDraft.unassignOpenTasks}
                          onChange={(e) => setAccessDraft({ ...accessDraft, unassignOpenTasks: e.target.checked })}
                        />{' '}
                        Désassigner ses tâches en cours
                      </label>
                    )}
                    <button onClick={() => saveAccess(u)}>Enregistrer</button>{' '}
                    <button onClick={() => setAccessId(null)}>Annuler</button>
                  </td>
                </tr>
              );
            }

            return (
              <tr key={u.id}>
                <td>{u.firstName}</td>
                <td>{u.lastName}</td>
                <td>{u.email}</td>
                <td>{u.role}</td>
                <td>{u.teamName || '-'}</td>
                <td>
                  <button onClick={() => startEdit(u)}>Modifier</button>{' '}
                  {canChangeAccess && (
                    <button onClick={() => startAccess(u)}>Rôle / équipe</button>
                  )}{' '}
                  {!isSelf && (
                    <button onClick={() => handleDelete(u)}>Supprimer</button>
                  )}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}