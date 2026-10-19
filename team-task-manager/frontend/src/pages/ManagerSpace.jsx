
import { useEffect, useState } from 'react';
import {
  getProjects,
  getTasks,
  updateTaskStatus,
  getTeams,
  getTeamMembers,
  createTask,
  updateTask,
} from '../api';

const STATUSES = ['TODO', 'IN_PROGRESS', 'DONE'];
const PRIORITIES = ['LOW', 'MEDIUM', 'HIGH'];

export default function ManagerSpace() {
  const [projects, setProjects] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [teams, setTeams] = useState([]);
  const [membersByTeam, setMembersByTeam] = useState({});
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loadingMembers, setLoadingMembers] = useState({});
  const [creating, setCreating] = useState(false);

  const [assignmentTaskId, setAssignmentTaskId] = useState(null);
  const [assigneeByTask, setAssigneeByTask] = useState({});
  const [assigningTaskId, setAssigningTaskId] = useState(null);

  const [form, setForm] = useState({
    title: '',
    description: '',
    priority: 'MEDIUM',
    projectId: '',
    assignedUserId: '',
  });

  useEffect(() => {
    Promise.all([getProjects(), getTasks(), getTeams()])
      .then(([projectsData, tasksData, teamsData]) => {
        setProjects(projectsData);
        setTasks(tasksData);
        setTeams(teamsData);
      })
      .catch((err) => setError(err.message));
  }, []);

  const selectedProject = projects.find(
    (project) => String(project.id) === form.projectId
  );

  const selectedTeamId = selectedProject?.teamId;

  const teamMembers = selectedTeamId
    ? membersByTeam[selectedTeamId] || []
    : [];

  async function handleShowMembers(teamId) {
    setError('');

    setLoadingMembers((current) => ({
      ...current,
      [teamId]: true,
    }));

    try {
      const members = await getTeamMembers(teamId);

      setMembersByTeam((current) => ({
        ...current,
        [teamId]: members,
      }));

      return members;
    } catch (err) {
      setError(err.message);
      return null;
    } finally {
      setLoadingMembers((current) => ({
        ...current,
        [teamId]: false,
      }));
    }
  }

  async function handleProjectChange(projectId) {
    setError('');
    setSuccess('');

    setForm((current) => ({
      ...current,
      projectId,
      assignedUserId: '',
    }));

    if (!projectId) return;

    const project = projects.find(
      (item) => String(item.id) === projectId
    );

    if (!project?.teamId) {
      setError("Impossible de trouver l'équipe de ce projet.");
      return;
    }

    await handleShowMembers(project.teamId);
  }

  async function handleCreateTask(event) {
    event.preventDefault();
    setError('');
    setSuccess('');

    if (!form.title.trim()) {
      setError('Veuillez saisir un titre.');
      return;
    }

    if (!form.projectId) {
      setError('Veuillez sélectionner un projet.');
      return;
    }

    setCreating(true);

    try {
      const created = await createTask({
        title: form.title.trim(),
        description: form.description.trim(),
        priority: form.priority,
        projectId: Number(form.projectId),
        assignedUserId: form.assignedUserId
          ? Number(form.assignedUserId)
          : null,
      });

      setTasks((current) => [...current, created]);

      setForm({
        title: '',
        description: '',
        priority: 'MEDIUM',
        projectId: '',
        assignedUserId: '',
      });

      setSuccess('La tâche a été créée avec succès.');
    } catch (err) {
      setError(err.message);
    } finally {
      setCreating(false);
    }
  }

  async function handleStatusChange(id, status) {
    setError('');
    setSuccess('');

    try {
      const updated = await updateTaskStatus(id, status);

      setTasks((current) =>
        current.map((task) => (task.id === id ? updated : task))
      );
    } catch (err) {
      setError(err.message);
    }
  }

  async function handlePrepareAssignment(task) {
    setError('');
    setSuccess('');

    const project = projects.find(
      (item) => String(item.id) === String(task.projectId)
    );

    if (!project?.teamId) {
      setError(
        "Impossible de trouver l'équipe du projet associé à cette tâche."
      );
      return;
    }

    setAssignmentTaskId(task.id);

    const members = await handleShowMembers(project.teamId);

    if (members === null) {
      setAssignmentTaskId(null);
    } else if (members.length === 0) {
      setError('Aucun employé disponible dans cette équipe.');
    }
  }

  async function handleAssignTask(task) {
    const assigneeId = assigneeByTask[task.id];

    if (!assigneeId) {
      setError('Veuillez choisir un employé.');
      return;
    }

    setError('');
    setSuccess('');
    setAssigningTaskId(task.id);

    try {
      const updatedTask = await updateTask(task.id, {
        assignedUserId: Number(assigneeId),
      });

      setTasks((current) =>
        current.map((item) =>
          item.id === task.id ? updatedTask : item
        )
      );

      setAssignmentTaskId(null);

      setAssigneeByTask((current) => {
        const next = { ...current };
        delete next[task.id];
        return next;
      });

      setSuccess(
        `La tâche "${updatedTask.title}" a été assignée avec succès.`
      );
    } catch (err) {
      setError(err.message);
    } finally {
      setAssigningTaskId(null);
    }
  }

  return (
    <div>
      {error && (
        <p role="alert" style={{ color: 'crimson' }}>
          {error}
        </p>
      )}

      {success && (
        <p role="status" style={{ color: 'green' }}>
          {success}
        </p>
      )}

      <h2>Mes équipes</h2>

      {teams.length === 0 && <p>Aucune équipe trouvée.</p>}

      {teams.map((team) => (
        <section
          key={team.id}
          style={{
            border: '1px solid #ddd',
            borderRadius: '8px',
            padding: '16px',
            marginBottom: '12px',
          }}
        >
          <h3>{team.name}</h3>

          {team.description && <p>{team.description}</p>}

          <button
            type="button"
            onClick={() => handleShowMembers(team.id)}
            disabled={Boolean(loadingMembers[team.id])}
          >
            {loadingMembers[team.id]
              ? 'Chargement...'
              : 'Voir les membres'}
          </button>

          {membersByTeam[team.id] && (
            <ul>
              {membersByTeam[team.id].length === 0 ? (
                <li>Aucun employé dans cette équipe.</li>
              ) : (
                membersByTeam[team.id].map((member) => (
                  <li key={member.id}>
                    {member.firstName} {member.lastName} — {member.email}
                  </li>
                ))
              )}
            </ul>
          )}
        </section>
      ))}

      <hr />

      <h2>Créer une tâche</h2>

      <form
        onSubmit={handleCreateTask}
        style={{
          display: 'grid',
          gap: '12px',
          maxWidth: '500px',
          marginBottom: '24px',
        }}
      >
        <label>
          Titre *
          <input
            type="text"
            value={form.title}
            onChange={(event) =>
              setForm((current) => ({
                ...current,
                title: event.target.value,
              }))
            }
            required
            maxLength={150}
            style={{ display: 'block', width: '100%' }}
          />
        </label>

        <label>
          Description
          <textarea
            value={form.description}
            onChange={(event) =>
              setForm((current) => ({
                ...current,
                description: event.target.value,
              }))
            }
            rows={3}
            style={{ display: 'block', width: '100%' }}
          />
        </label>

        <label>
          Priorité
          <select
            value={form.priority}
            onChange={(event) =>
              setForm((current) => ({
                ...current,
                priority: event.target.value,
              }))
            }
            style={{ display: 'block', width: '100%' }}
          >
            {PRIORITIES.map((priority) => (
              <option key={priority} value={priority}>
                {priority}
              </option>
            ))}
          </select>
        </label>

        <label>
          Projet *
          <select
            value={form.projectId}
            onChange={(event) =>
              handleProjectChange(event.target.value)
            }
            required
            style={{ display: 'block', width: '100%' }}
          >
            <option value="">Choisir un projet</option>

            {projects.map((project) => (
              <option key={project.id} value={project.id}>
                {project.name} — {project.teamName}
              </option>
            ))}
          </select>
        </label>

        <label>
          Assigner à un employé
          <select
            value={form.assignedUserId}
            onChange={(event) =>
              setForm((current) => ({
                ...current,
                assignedUserId: event.target.value,
              }))
            }
            disabled={
              !selectedProject ||
              Boolean(loadingMembers[selectedTeamId]) ||
              !membersByTeam[selectedTeamId]
            }
            style={{ display: 'block', width: '100%' }}
          >
            <option value="">Aucun employé</option>

            {teamMembers.map((member) => (
              <option key={member.id} value={member.id}>
                {member.firstName} {member.lastName} — {member.email}
              </option>
            ))}
          </select>
        </label>

        {selectedProject && loadingMembers[selectedTeamId] && (
          <p>Chargement des employés de l'équipe...</p>
        )}

        <button
          type="submit"
          disabled={creating || projects.length === 0}
        >
          {creating ? 'Création...' : 'Créer la tâche'}
        </button>
      </form>

      <h2>Projets de mes équipes</h2>

      {projects.length === 0 && <p>Aucun projet.</p>}

      <ul>
        {projects.map((project) => (
          <li key={project.id}>
            {project.name} ({project.status}) : équipe{' '}
            {project.teamName}, client {project.clientName}
          </li>
        ))}
      </ul>

      <h2>Tâches</h2>

      {tasks.length === 0 && <p>Aucune tâche.</p>}

      <ul>
        {tasks.map((task) => {
          const project = projects.find(
            (item) => String(item.id) === String(task.projectId)
          );

          const teamId = project?.teamId;
          const members = teamId
            ? membersByTeam[teamId] || []
            : [];

          return (
            <li key={task.id} style={{ marginBottom: '16px' }}>
              <strong>{task.title}</strong>
              {' — '}
              Projet : {task.projectName}
              {' — '}
              Assignée à : {task.assignedUserName || 'personne'}
              {' — '}

              <select
                value={task.status}
                onChange={(event) =>
                  handleStatusChange(task.id, event.target.value)
                }
              >
                {STATUSES.map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </select>

              {!task.assignedUserId && (
                <div style={{ marginTop: '8px' }}>
                  {assignmentTaskId !== task.id ? (
                    <button
                      type="button"
                      onClick={() => handlePrepareAssignment(task)}
                      disabled={!project?.teamId}
                    >
                      Assigner un employé
                    </button>
                  ) : (
                    <div
                      style={{
                        display: 'flex',
                        gap: '8px',
                        flexWrap: 'wrap',
                        marginTop: '8px',
                      }}
                    >
                      <select
                        value={assigneeByTask[task.id] || ''}
                        onChange={(event) =>
                          setAssigneeByTask((current) => ({
                            ...current,
                            [task.id]: event.target.value,
                          }))
                        }
                        disabled={
                          Boolean(loadingMembers[teamId]) ||
                          members.length === 0
                        }
                      >
                        <option value="">Choisir un employé</option>

                        {members.map((member) => (
                          <option key={member.id} value={member.id}>
                            {member.firstName} {member.lastName}
                          </option>
                        ))}
                      </select>

                      <button
                        type="button"
                        onClick={() => handleAssignTask(task)}
                        disabled={
                          !assigneeByTask[task.id] ||
                          assigningTaskId === task.id
                        }
                      >
                        {assigningTaskId === task.id
                          ? 'Assignation...'
                          : 'Confirmer'}
                      </button>

                      <button
                        type="button"
                        onClick={() => setAssignmentTaskId(null)}
                      >
                        Annuler
                      </button>
                    </div>
                  )}
                </div>
              )}
            </li>
          );
        })}
      </ul>
    </div>
  );
}
