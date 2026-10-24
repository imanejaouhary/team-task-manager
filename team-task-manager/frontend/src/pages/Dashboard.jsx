import ClientSpace from './ClientSpace';
import EmployeeSpace from './EmployeeSpace';
import ManagerSpace from './ManagerSpace';
import AdminSpace from './AdminSpace';

export default function Dashboard({ user, onLogout }) {
  let content;
  if (user.role === 'CLIENT') {
    content = <ClientSpace />;
  } else if (user.role === 'EMPLOYEE') {
    content = <EmployeeSpace />;
  } else if (user.role === 'MANAGER') {
    content = <ManagerSpace />;
  } else if (user.role === 'ADMIN') {
       content = <AdminSpace currentUser={user} />;
  } else {
    content = <p>Rôle inconnu : {user.role}</p>;
  }

  return (
    <div style={{ maxWidth: 640, margin: '40px auto' }}>
      <h1>Bonjour {user.firstName} {user.lastName}</h1>
      <p>Rôle : <strong>{user.role}</strong></p>
      <button onClick={onLogout}>Se déconnecter</button>
      <hr />
      {content}
    </div>
  );
}