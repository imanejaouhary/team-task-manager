import { useEffect, useState } from 'react';
import { getTasks, updateTaskStatus } from '../api';

const STATUSES = ['TODO', 'IN_PROGRESS', 'DONE'];

export default function EmployeeSpace() {
  const [tasks, setTasks] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    getTasks()
      .then(setTasks)
      .catch((err) => setError(err.message));
  }, []);

  async function handleStatusChange(id, status) {
    setError('');
    try {
      const updated = await updateTaskStatus(id, status);
      setTasks((current) => current.map((t) => (t.id === id ? updated : t)));
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div>
      <h2>Mes tâches</h2>
      {error && <p style={{ color: 'crimson' }}>{error}</p>}
      {tasks.length === 0 && <p>Aucune tâche ne t'est assignée.</p>}
      <ul>
        {tasks.map((t) => (
          <li key={t.id}>
            {t.title} (projet {t.projectName}){' '}
            <select value={t.status} onChange={(e) => handleStatusChange(t.id, e.target.value)}>
              {STATUSES.map((s) => (
                <option key={s} value={s}>{s}</option>
              ))}
            </select>
          </li>
        ))}
      </ul>
    </div>
  );
}