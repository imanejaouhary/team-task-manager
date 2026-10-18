import { useEffect, useState } from 'react';
import { getProjects, getTasks } from '../api';

export default function ClientSpace() {
  const [projects, setProjects] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([getProjects(), getTasks()])
      .then(([p, t]) => {
        setProjects(p);
        setTasks(t);
      })
      .catch((err) => setError(err.message));
  }, []);

  if (error) return <p style={{ color: 'crimson' }}>{error}</p>;

  return (
    <div>
      <h2>Mes projets</h2>
      {projects.length === 0 && <p>Aucun projet pour le moment.</p>}
      <ul>
        {projects.map((p) => (
          <li key={p.id}>
            {p.name} ({p.status}) : équipe {p.teamName}
          </li>
        ))}
      </ul>

      <h2>Tâches de mes projets</h2>
      {tasks.length === 0 && <p>Aucune tâche pour le moment.</p>}
      <ul>
        {tasks.map((t) => (
          <li key={t.id}>
            {t.title} ({t.status}) : projet {t.projectName}
          </li>
        ))}
      </ul>
    </div>
  );
}